package com.fha.batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.batch.BatchingStrategy;
import org.springframework.amqp.rabbit.batch.SimpleBatchingStrategy;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.core.BatchingRabbitTemplate;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;

import java.nio.charset.StandardCharsets;
import java.util.List;

@SpringBootApplication
@Slf4j
public class AmqpBatchApplication {

    public static void main(String[] args) {
        SpringApplication.run(AmqpBatchApplication.class, args);
    }

    @Bean
    public RabbitAdmin admin(RabbitTemplate template) {
        return new RabbitAdmin(template.getConnectionFactory());
    }

    @Bean
    public Queue helloWorldQueue(RabbitAdmin admin) {
        Queue queue = new Queue("hello.world.queue");
        admin.declareQueue(queue);
        return queue;
    }

    @Bean
    public Exchange helloExchange(RabbitAdmin admin) {
        TopicExchange exchange = new TopicExchange("hello.exchange");
        admin.declareExchange(exchange);
        return exchange;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(RabbitTemplate template) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(template.getConnectionFactory());
        factory.setBatchListener(true);
        return factory;
    }

    @Bean
    public ApplicationRunner runner(RabbitTemplate template) {
        BatchingStrategy strategy = new SimpleBatchingStrategy(5, 25_000, 1_000);
        TaskScheduler scheduler = new ConcurrentTaskScheduler();
        BatchingRabbitTemplate batch = new BatchingRabbitTemplate(strategy, scheduler);
        batch.setConnectionFactory(template.getConnectionFactory());

        return args -> {
            var props = new MessageProperties();
            props.setContentType("text/plain");
            for (var i = 0; i < 10; i++) {
                Message message = new Message(("hello from batch " + i).getBytes(StandardCharsets.UTF_8), props);
                batch.send("hello.exchange", "", message, null);
            }
        };
    }

    @RabbitListener(queues = "hello.world.queue")
    void listen(List<String> in) {
        log.info("received input: {}", in);
    }
}
