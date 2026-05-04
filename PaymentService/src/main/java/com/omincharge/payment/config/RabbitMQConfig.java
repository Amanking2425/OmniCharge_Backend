package com.omincharge.payment.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.exchange.payment}")
    private String paymentExchange;

    @Value("${rabbitmq.routing-key.payment}")
    private String paymentRoutingKey;

    // 1. Declare the Queue (Name must match what you have in RabbitMQ UI)
    @Bean
    public Queue paymentQueue() {
        return new Queue("payment.notification.queue", true);
    }

    // 2. Declare the Exchange
    @Bean
    public TopicExchange paymentTopicExchange() {
        return new TopicExchange(paymentExchange);
    }

    // 3. MUST HAVE: Bind the Queue to the Exchange using the Routing Key
    @Bean
    public Binding paymentBinding() {
        return BindingBuilder
                .bind(paymentQueue())
                .to(paymentTopicExchange())
                .with(paymentRoutingKey);
    }

    // Your existing converters below
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}