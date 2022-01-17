package com.fha.playground;

import com.rabbitmq.client.ConnectionFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import reactor.rabbitmq.*;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

@SpringBootTest(properties = "spring.cloud.function.definition=monitorUppercaseIn")
class PlaygroundApplicationTests {


    @Test
    void sendMessages() throws InterruptedException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.useNio();

        SenderOptions senderOptions = new SenderOptions()
                .connectionFactory(connectionFactory)
                .resourceManagementScheduler(Schedulers.boundedElastic());

        Sender sender = RabbitFlux.createSender(senderOptions);

        Flux<OutboundMessage> outboundFlux = Flux.range(1, 1000000)
                .map(i -> new OutboundMessage("uppercase-in-0",
                        "", ("Message " + i).getBytes(StandardCharsets.UTF_8)));


        sender.send(outboundFlux, new SendOptions()
                        .exceptionHandler(
                                new ExceptionHandlers.RetrySendingExceptionHandler(
                                        Duration.ofSeconds(20), Duration.ofMillis(500),
                                        ExceptionHandlers.CONNECTION_RECOVERY_PREDICATE
                                )
                        ))
                .doOnError(e -> System.out.println("Send failed: " + e.getMessage()))
                .subscribe();

        Thread.sleep(240 * 400);
    }
}
