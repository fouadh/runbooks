package com.fha.runbooks.rabbitmq.dlq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.function.Function;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    Function<String, String> lowercase() {
        return this::process;
    }

    private String process(String input) {
        System.out.println("received input: " + input);
        if ("fail".equals(input))
            throw new RuntimeException("boom 💣");
        return input.toLowerCase();
    }

}
