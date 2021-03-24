package com.fha.runbooks.rabbitmq.dlq;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class DlqTest {
  @Autowired
  private JmsTemplate jmsTemplate;

  @Autowired
  private DlqListener dlqListener;

  @Test
  void shouldRedirectMessageToDlq() throws InterruptedException {
    String message = "this is foo";
    jmsTemplate.convertAndSend("foo", message);
    dlqListener.getLatch().await(1, TimeUnit.SECONDS);
    assertEquals(message, dlqListener.getMessage());
  }

  static class DlqListener {
    private String message;
    private CountDownLatch latch = new CountDownLatch(1);

    @JmsListener(destination = "dlq.foo")
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
