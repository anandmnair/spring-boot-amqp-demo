package com.anand.demo.amqp.properties;

import lombok.Getter;

@Getter
public enum ExchangeTypes {
	DIRECT("direct"),
	TOPIC("topic"),
	FANOUT("fanout"),
	HEADERS("headers"),
	SYSTEM("system");

	private String value;
	
	ExchangeTypes(String value) {
		this.value=value;
	}
}
