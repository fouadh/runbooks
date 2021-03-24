package com.fha.runbooks.rabbitmq.dlq;

import com.rabbitmq.jms.admin.RMQConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.jms.ConnectionFactory;

@Configuration
public class JmsConfig {
  @Bean
  public ConnectionFactory connectionFactory() {
    RMQConnectionFactory factory = new RMQConnectionFactory();
    factory.setUsername("guest");
    factory.setPassword("guest");
    factory.setVirtualHost("/");
    factory.setHost("localhost");
    factory.setPort(5672);
    return factory;
  }
}
