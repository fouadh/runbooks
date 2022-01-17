package com.fha.playground;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.function.Consumer;
import java.util.function.Function;

@SpringBootApplication
public class PlaygroundApplication {
    private final static Logger LOG = LoggerFactory.getLogger(PlaygroundApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(PlaygroundApplication.class, args);
    }

    @Bean
    public Function<String, String> uppercase() {
        return s -> {
            LOG.info("received: {}", s);
            return s.toUpperCase();
        };
    }

    @Bean
    public Function<Flux<String>, Flux<String>> reactiveUppercase() {
        return s -> s.log()
                .flatMap(value ->
                        Flux.create(emitter -> {
                            var elements = value.split(",");
                            for (var e : elements) {
                                System.out.println("emet message à " + LocalDateTime.now());
                                emitter.next(e);
                                try {
                                    Thread.sleep(30000);
                                } catch (Exception exception) {
                                }
                            }
                        })
                );
    }

    @Bean
    public Consumer<String> monitorUppercaseIn() {
        return s -> {
        };
    }
}
