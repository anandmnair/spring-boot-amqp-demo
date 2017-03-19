package com.anand.demo.amqp.configuration;

import java.util.Map;

import org.apache.commons.lang.BooleanUtils;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.anand.demo.amqp.properties.BindingProperties;
import com.anand.demo.amqp.properties.ExchangeProperties;
import com.anand.demo.amqp.properties.QueueProperties;
import com.anand.demo.amqp.properties.RabbitmqProperties;

@Configuration
public class RabbitmqConfiguration implements ApplicationContextAware {
	
	@Autowired
	private RabbitmqProperties rabbitmqProperties;
	
	private AnnotationConfigApplicationContext applicationContext;
	
	@Bean
	public MessageConverter messageConverter() {
		return new Jackson2JsonMessageConverter();
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext= (AnnotationConfigApplicationContext) applicationContext;
		
		for(Map.Entry<String, ExchangeProperties> entry : rabbitmqProperties.getExchange().entrySet()) {
			String key=entry.getKey();
			ExchangeProperties exchangeProperties= entry.getValue();
			Exchange exchange = createExchange(exchangeProperties);
			this.applicationContext.getBeanFactory().registerSingleton(key, exchange);
		}
		
		for(Map.Entry<String, QueueProperties> entry : rabbitmqProperties.getQueue().entrySet()) {
			String key=entry.getKey();
			QueueProperties queueProperties = entry.getValue();
			if(queueProperties.isDeadLetterEnabled()) {
				ExchangeProperties defaultDeadLetterExchangeProperties = rabbitmqProperties.getDefaultDeadLetterExchangeProperties();
				ExchangeProperties deadLetterExchangeProperties=ExchangeProperties.builder().build();
				BeanUtils.copyProperties(defaultDeadLetterExchangeProperties, deadLetterExchangeProperties);
				deadLetterExchangeProperties.setName(queueProperties.getName()+rabbitmqProperties.getDeadLetterExchangePostfix());
				
				Exchange deadLetterExchange = createExchange(deadLetterExchangeProperties);
				this.applicationContext.getBeanFactory().registerSingleton(key+rabbitmqProperties.getDeadLetterExchangePostfix(),deadLetterExchange);
				
				QueueProperties deadLetterQueueProperties = QueueProperties.builder().build();
				BeanUtils.copyProperties(queueProperties, deadLetterQueueProperties);
				deadLetterQueueProperties.setName(queueProperties.getName()+rabbitmqProperties.getDeadLetterQueuePostfix());
				
				Queue deadLetterQueue = createQueue(deadLetterQueueProperties);
				this.applicationContext.getBeanFactory().registerSingleton(key+rabbitmqProperties.getDeadLetterQueuePostfix(),deadLetterQueue);
				queueProperties.getArguments().put("x-dead-letter-exchange", deadLetterExchangeProperties.getName());
				queueProperties.getArguments().put("x-dead-letter-routing-key", deadLetterQueueProperties.getName());
				
				BindingProperties dealLetterBindingProperties = BindingProperties.builder()
						.exchange(deadLetterExchangeProperties.getName())
						.queue(deadLetterQueueProperties.getName())
						.routingKey(deadLetterQueueProperties.getName())
						.build();
				this.applicationContext.getBeanFactory().registerSingleton(key+".bind",createBuiding(dealLetterBindingProperties, deadLetterExchange, deadLetterQueue));
			}
			Queue queue = createQueue(queueProperties);
			this.applicationContext.getBeanFactory().registerSingleton(key, queue);

		}
		
		for(Map.Entry<String, BindingProperties> entry : rabbitmqProperties.getBinding().entrySet()) {
			String key=entry.getKey();
			BindingProperties bindingProperties= entry.getValue();
			Binding binding = createBuiding(bindingProperties, applicationContext.getBean(bindingProperties.getExchange(),Exchange.class), applicationContext.getBean(bindingProperties.getQueue(),Queue.class));
			this.applicationContext.getBeanFactory().registerSingleton(key, binding);
		}
		
	}
	
	private Exchange createExchange(ExchangeProperties exchangeProperties) {
		ExchangeBuilder builder = ExchangeBuilder
				.directExchange(exchangeProperties.getName())
				.durable(BooleanUtils.toBoolean(exchangeProperties.getDurable()));
		if(BooleanUtils.isTrue(exchangeProperties.getAutoDelete())) {
			builder.autoDelete();
		}
		return builder.build();
	}

	private Queue createQueue(QueueProperties queueProperties) {
		QueueBuilder builder;
		if(BooleanUtils.isTrue(queueProperties.getDurable())) {
			builder = QueueBuilder.durable(queueProperties.getName());
		}
		else {
			builder = QueueBuilder.nonDurable(queueProperties.getName());
		}
		if(BooleanUtils.isTrue(queueProperties.getAutoDelete())) {
			builder.autoDelete();
		}
		
		return builder.withArguments(queueProperties.getArguments()).build();
	}
	private Binding createBuiding(BindingProperties bindingProperties, Exchange exchange, Queue queue) {
		return BindingBuilder.bind(queue).to(exchange).with(bindingProperties.getRoutingKey()).noargs();
	}
	
}
