package com.anand.demo.service;

import org.springframework.stereotype.Service;

import com.anand.demo.model.Deal;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PilMessageService {

	public Deal processMessage(Deal deal) {
		log.info("Message Processed @ PilMessageService :: {}", deal);
		return deal;
	}
}
