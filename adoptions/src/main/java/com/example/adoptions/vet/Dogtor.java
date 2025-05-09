package com.example.adoptions.vet;

import com.example.adoptions.adoptions.DogAdoptionEvent;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
class Dogtor {

    @ApplicationModuleListener
    void checkup(DogAdoptionEvent dogId) {
        System.out.println("checking up on dog " + dogId.dogId() + "!");
    }
}
