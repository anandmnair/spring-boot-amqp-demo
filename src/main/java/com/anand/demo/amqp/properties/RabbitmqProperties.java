package com.anand.demo.amqp.properties;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Data
@ConfigurationProperties(prefix="rabbitmq.config")
public class RabbitmqProperties {
	
	private static final String DEFAULT_EXCHANGE_POSTFIX=".dlx"; 
	private static final String DEFAULT_QUEUE_POSTFIX=".dlq"; 
	
	private Map<String,ExchangeProperties> exchange = new LinkedHashMap<>();
	
	private Map<String,QueueProperties> queue = new LinkedHashMap<>();
	
	private Map<String,BindingProperties> binding = new LinkedHashMap<>();
	
	private String deadLetterExchangePostfix=DEFAULT_EXCHANGE_POSTFIX;
	
	private String deadLetterQueuePostfix=DEFAULT_QUEUE_POSTFIX;
	
	private ExchangeProperties defaultDeadLetterExchangeProperties=ExchangeProperties.builder().build();
	
}
