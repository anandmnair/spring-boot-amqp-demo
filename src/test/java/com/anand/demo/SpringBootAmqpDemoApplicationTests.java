package com.anand.demo;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.anand.demo.amqp.properties.RabbitmqProperties;
import com.anand.demo.model.Deal;
import com.anand.demo.service.CpmMessageService;
import com.anand.demo.service.PilMessageService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringBootAmqpDemoApplicationTests {
	
	
	@MockBean
	private CpmMessageService cpmMessageService;
	
	@MockBean
	private PilMessageService pilMessageService; 
	
	@Autowired
	private RabbitmqProperties rabbitmqProperties;
	
	@Autowired
	private AmqpTemplate amqpTemplate;

	private Deal deal;
	
	@Before
	public void setup() {
		deal=Deal.builder().dealId(101L).dealCode("DC101").build();
	}
	
	@Test
	public void throwExceptionInCpmMessageProcessing() {
		
		given(cpmMessageService.processMessage(deal)).willThrow(new RuntimeException("Some dummy exception in test case"));

		amqpTemplate.convertAndSend(rabbitmqProperties.getExchange().get("cpm-cd-exchange").getName(), 
				rabbitmqProperties.getBinding().get("cpm-cd-binding").getRoutingKey(), deal);
		
		Deal result = (Deal) amqpTemplate.receiveAndConvert(rabbitmqProperties.getQueue().get("cpm-cd-queue").getName()+rabbitmqProperties.getDeadLetterQueuePostfix(), 10000L);
		
		assertThat(result, equalTo(deal));
				
	}
	

	@Test
	public void throwExceptionInPilMessageProcessing() {
		
		when(cpmMessageService.processMessage(deal)).thenReturn(deal);

		given(pilMessageService.processMessage(deal)).willThrow(new RuntimeException("Some dummy exception in test case"));

		amqpTemplate.convertAndSend(rabbitmqProperties.getExchange().get("cpm-cd-exchange").getName(), 
				rabbitmqProperties.getBinding().get("cpm-cd-binding").getRoutingKey(), deal);
		
		Deal result = (Deal) amqpTemplate.receiveAndConvert(rabbitmqProperties.getQueue().get("pil-deal-queue").getName()+rabbitmqProperties.getDeadLetterQueuePostfix(), 10000L);

		
		assertThat(result, equalTo(deal));
				
	}
	
	@Test
	public void positiveFlow() {
		
		when(cpmMessageService.processMessage(deal)).thenReturn(deal);

		when(pilMessageService.processMessage(deal)).thenAnswer(new Answer<Deal>() {
			@Override
			public Deal answer(InvocationOnMock invocation) throws Throwable {
				Deal deal = invocation.getArgumentAt(0, Deal.class);
				amqpTemplate.convertAndSend(rabbitmqProperties.getExchange().get("pil-deal-mock-exchange").getName(), 
						rabbitmqProperties.getBinding().get("pil-deal-mock-binding").getRoutingKey(), deal);
				return deal;
			}
		});

		amqpTemplate.convertAndSend(rabbitmqProperties.getExchange().get("cpm-cd-exchange").getName(), 
				rabbitmqProperties.getBinding().get("cpm-cd-binding").getRoutingKey(), deal);
		
		Deal result = (Deal) amqpTemplate.receiveAndConvert(rabbitmqProperties.getQueue().get("pil-deal-mock-queue").getName(), 
				10000L);
		
		assertThat(result, equalTo(deal));
				
	}
	
	
}

