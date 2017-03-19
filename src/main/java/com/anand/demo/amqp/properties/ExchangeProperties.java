package com.anand.demo.amqp.properties;

import java.util.LinkedHashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeProperties {
	
	private String name;

	private ExchangeTypes type;

	private Boolean durable = Boolean.TRUE;

	private Boolean autoDelete;

	private Boolean internal;

	private Boolean delayed;

	private Map<String, Object> arguments = new LinkedHashMap<>();

}
