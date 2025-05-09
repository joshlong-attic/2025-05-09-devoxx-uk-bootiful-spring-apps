package com.example.adoptions.adoptions;

import com.example.adoptions.adoptions.grpc.AdoptionsGrpc;
import com.example.adoptions.adoptions.grpc.Dogs;
import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.annotation.Id;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@Service
class GrpcService extends AdoptionsGrpc.AdoptionsImplBase {


    private final DogService dogService;

    GrpcService(DogService dogService) {
        super();
        this.dogService = dogService;
    }

    @Override
    public void all(Empty request, StreamObserver<Dogs> responseObserver) {
        var grpcDogs = dogService
                .all()
                .stream()
                .map(ourDog ->
                        com.example.adoptions.adoptions.grpc.Dog.newBuilder()
                                .setDescription(ourDog.description())
                                .setId(ourDog.id())
                                .setName(ourDog.name())
                                .build())
                .toList();
        responseObserver.onNext(Dogs.newBuilder().addAllDogs(grpcDogs).build());
        responseObserver.onCompleted();
    }
}


@Controller
class GraphqlController {

    private final DogService dogService;

    GraphqlController(DogService dogService) {
        this.dogService = dogService;
    }

    @QueryMapping
    Collection<Dog> all() {
        return dogService.all();
    }
}


@Controller
@ResponseBody
class HttpController {

    private final DogService dogService;

    HttpController(DogService dogService) {
        this.dogService = dogService;
    }

    @GetMapping("/dogs")
    Collection<Dog> dogs() {
        return this.dogService.all();
    }

    @PostMapping("/dogs/{dogId}/adoptions")
    void adopt(@PathVariable int dogId, @RequestParam String owner) {
        this.dogService.adopt(dogId, owner);
    }
}

@Service
@Transactional
class DogService {

    private final DogRepository repository;
    private final ApplicationEventPublisher applicationEventPublisher;

    DogService(DogRepository repository, ApplicationEventPublisher applicationEventPublisher) {
        this.repository = repository;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    Collection<Dog> all() {
        return this.repository.findAll();
    }

    void adopt(int dogId, String owner) {
        this.repository.findById(dogId).ifPresent(dog -> {
            var updated = this.repository.save(new Dog(dogId, dog.name(), owner, dog.description()));
            System.out.println("Updated dog: " + updated);
            applicationEventPublisher.publishEvent(new DogAdoptionEvent(dogId));
        });
    }

}


record Dog(@Id int id, String name, String owner, String description) {
}

interface DogRepository extends ListCrudRepository<Dog, Integer> {

}