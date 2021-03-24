package com.fha.runbooks.rabbitmq.dlq;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import javax.jms.Message;
import java.lang.reflect.Method;

@Component
@Aspect
public class DqlAspect {
  private JmsTemplate jmsTemplate;

  public DqlAspect(JmsTemplate jmsTemplate) {
    this.jmsTemplate = jmsTemplate;
  }

  @Around("@annotation(Dlq) && args(message,..)")
  public Object foo(ProceedingJoinPoint joinPoint, Message message) throws Throwable {
    try {
      return joinPoint.proceed();
    } catch (Exception e) {
      publishMessageInDlq(joinPoint, message);
    }
    return null;
  }

  private void publishMessageInDlq(ProceedingJoinPoint joinPoint, Message message) {
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    Method method = signature.getMethod();
    Dlq annotation = method.getAnnotation(Dlq.class);
    jmsTemplate.send(annotation.value(), session -> message);
  }
}
