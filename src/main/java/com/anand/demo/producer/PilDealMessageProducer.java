package com.anand.demo.producer;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.anand.demo.model.Deal;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class PilDealMessageProducer {
	
	@Value("${rabbitmq.config.exchange.pil-deal-exchange.name}")
	private String exchange;
	
	@Value("${rabbitmq.config.binding.pil-deal-binding.routing-key}")
	private String routingKey;
	
	@Autowired
	private AmqpTemplate amqpTemplate;
	
	public void sentMessage(Deal deal) {
		log.info("Message produced @ PilDealMessageProducer :: {}",deal);
		amqpTemplate.convertAndSend(exchange, routingKey, deal);
		log.info("Message produced sucessfully @ PilDealMessageProducer :: {}",deal);
	}

}
