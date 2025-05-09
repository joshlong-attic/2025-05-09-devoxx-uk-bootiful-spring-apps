package com.example.scheduler;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@SpringBootApplication
public class SchedulerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SchedulerApplication.class, args);
    }

    @Bean
    MethodToolCallbackProvider methodToolCallbackProvider(DogAdoptionScheduler scheduler) {
        return MethodToolCallbackProvider
                .builder()
                .toolObjects(scheduler)
                .build() ;
    }
}

@Service
class DogAdoptionScheduler {

    @Tool(description = "schedule an appointment to pickup or adopt a dog at a Pooch Palace location")
    String schedule(int dogId, String dogName) {
        System.out.println("scheduling " + dogId + "/" + dogName);
        return Instant
                .now()
                .plus(3, ChronoUnit.DAYS)
                .toString();
    }
}