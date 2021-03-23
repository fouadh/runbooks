package com.fha.runbooks.rabbitmq.dlq;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class DlqTest {
  @Autowired
  private RabbitTemplate rabbitTemplate;

  @Autowired
  private DlqListener dlqListener;

  @Test
  void shouldRedirectMessageToDlq() throws InterruptedException {
    String message = "this is foo";
    rabbitTemplate.convertAndSend("foo", message);
    dlqListener.getLatch().await(1, TimeUnit.SECONDS);
    assertEquals(message, dlqListener.getMessage());
  }

  static class DlqListener {
    private String message;
    private CountDownLatch latch = new CountDownLatch(1);

    @RabbitListener(queues = "dlq.foo")
    public void onMessage(String message) {
      this.message = message;
      latch.countDown();
    }

    public String getMessage() {
      return message;
    }

    public CountDownLatch getLatch() {
      return latch;
    }
  }

  @TestConfiguration
  static class DlqTestConfig {
    @Bean
    public DlqListener dlqListener() {
      return new DlqListener();
    }
  }
}
