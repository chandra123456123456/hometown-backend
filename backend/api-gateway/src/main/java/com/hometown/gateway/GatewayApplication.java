package com.hometown.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * HomeTown API Gateway — single public entry point for the Angular app.
 * Routes to backend services (via Eureka in local mode), applies CORS, and
 * validates JWTs on protected paths.
 */
@SpringBootApplication
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
}
