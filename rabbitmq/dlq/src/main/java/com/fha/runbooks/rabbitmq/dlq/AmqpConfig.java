package com.fha.runbooks.rabbitmq.dlq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmqpConfig {
  @Bean
  Queue fooQueue() {
    return QueueBuilder.durable("foo")
        .withArgument("x-dead-letter-exchange", "dlx")
        .withArgument("x-dead-letter-routing-key", "dlq.foo")
        .build();
  }

  @Bean
  Exchange dlxExchange() {
    return ExchangeBuilder.topicExchange("dlx")
        .build();
  }

  @Bean
  Queue fooDlqQueue() {
    return QueueBuilder.durable("dlq.foo")
        .build();
  }

  @Bean
  Binding dlqBinding(Exchange dlxExchange, Queue fooDlqQueue) {
    return BindingBuilder.bind(fooDlqQueue)
              .to(dlxExchange)
              .with("dlq.foo")
              .noargs();
  }
}
