package com.card;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class CardApiFlux {
    public static void main(String[] args) {
        SpringApplication.run(CardApiFlux.class, args);
    }
}
