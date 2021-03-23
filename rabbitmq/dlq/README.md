# RabbitMQ Dead Letter Queue Setup

A simple Spring Boot/RabbitMQ project with a dead letter queue configured.

## In the Spring configuration

* Configure the queue with the dlq features:

```java
return QueueBuilder.durable("foo")
        .withArgument("x-dead-letter-exchange", "dlx")
        .withArgument("x-dead-letter-routing-key", "dlq.foo")
        .build();
```  

* Configure the dlq
* Configure the exchange
* Configure the binding

## In the application.yml

```yaml
spring:  
  rabbitmq:  
    listener:  
      simple:  
        default-requeue-rejected: false
```