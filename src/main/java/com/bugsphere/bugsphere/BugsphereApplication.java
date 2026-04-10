package com.bugsphere.bugsphere;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// @SpringBootApplication is a shortcut annotation that does 3 things:
// 1. Marks this as a Spring configuration class
// 2. Enables auto-scanning of all @Component, @Service, @Controller classes
// 3. Enables Spring Boot's auto-configuration magic
@SpringBootApplication
public class BugsphereApplication {

    // This is the entry point — Spring Boot starts from here
    public static void main(String[] args) {
        SpringApplication.run(BugsphereApplication.class, args);
        System.out.println("BugSphere is running at http://localhost:8080");
    }
}