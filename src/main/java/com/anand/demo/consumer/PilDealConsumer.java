package com.anand.demo.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.anand.demo.model.Deal;
import com.anand.demo.service.PilMessageService;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class PilDealConsumer {
	
	@Autowired
	private PilMessageService pilMessageService;
	
	@RabbitListener(queues ={"${rabbitmq.config.queue.pil-deal-queue.name}"})
	public void onMessage(@Payload Deal deal) {
		log.info("Message Received @ PilDealConsumer :: {}",deal);
		pilMessageService.processMessage(deal);
		log.info("Message processed successfully @ PilDealConsumer :: {}",deal);
	}

}
