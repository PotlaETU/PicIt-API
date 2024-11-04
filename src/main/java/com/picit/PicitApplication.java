package com.picit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
public class PicitApplication {

    public static void main(String[] args) {
        SpringApplication.run(PicitApplication.class, args);
    }

}
