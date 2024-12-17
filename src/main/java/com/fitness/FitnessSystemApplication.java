package com.fitness;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.fitness"})
public class FitnessSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(FitnessSystemApplication.class, args);
    }

}
