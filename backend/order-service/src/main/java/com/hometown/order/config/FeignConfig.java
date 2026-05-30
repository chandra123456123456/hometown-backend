package com.hometown.order.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("microservices")
@EnableFeignClients(basePackages = "com.hometown.order.client")
public class FeignConfig {
}
