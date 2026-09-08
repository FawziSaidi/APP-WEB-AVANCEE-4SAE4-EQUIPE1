package com.esprit.publicationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient

public class PublicationServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(PublicationServiceApplication.class, args);
    }
}
