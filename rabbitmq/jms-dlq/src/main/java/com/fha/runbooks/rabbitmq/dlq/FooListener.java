package com.fha.runbooks.rabbitmq.dlq;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.Message;

@Component
public class FooListener {
  @JmsListener(destination = "foo")
  @Dlq("dlq.foo")
  public void onMessage(Message message) {
    throw new RuntimeException("boom 💥");
  }
}
