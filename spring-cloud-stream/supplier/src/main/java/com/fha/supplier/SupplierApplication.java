package com.fha.supplier;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;
import java.util.function.Supplier;

@SpringBootApplication
public class SupplierApplication {

    public static void main(String[] args) {
        SpringApplication.run(SupplierApplication.class, args);
    }

    @Bean
    public Supplier<String> supplier() {
        return () -> {
            System.out.println("Supplier is called...");
            return "hello " + LocalDateTime.now();
        };
    }
}
