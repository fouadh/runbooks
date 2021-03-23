package com.fha.runbooks.rabbitmq.dlq;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class FooListener {
  @RabbitListener(queues = "foo")
  public void onMessage() {
    throw new RuntimeException("boom 💥");
  }
}
