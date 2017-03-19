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
public class BindingProperties {
	
	private String exchange;

	private String queue;

	private String routingKey;

	private Map<String, Object> arguments = new LinkedHashMap<>();

}
