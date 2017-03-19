package com.anand.demo.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.anand.demo.model.Deal;
import com.anand.demo.producer.PilDealMessageProducer;
import com.anand.demo.service.CpmMessageService;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CpmConsumer {
	
	@Autowired
	private CpmMessageService cpmMessageService;

	@Autowired
	private PilDealMessageProducer pilDealMessageProducer;
	
	@RabbitListener(queues ={"${rabbitmq.config.queue.cpm-cd-queue.name}"})
	public void onMessage(@Payload Deal deal) {
		log.info("Message Received @ CpmConsumer :: {}",deal);
		cpmMessageService.processMessage(deal);
		pilDealMessageProducer.sentMessage(deal);
		log.info("Message processed successfully @ CpmConsumer :: {}",deal);
	}

}
