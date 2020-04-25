package com.ecosystem.batchservice;

import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class AService {

    public void printTime() {
        System.out.println(Instant.now());
    }
}
