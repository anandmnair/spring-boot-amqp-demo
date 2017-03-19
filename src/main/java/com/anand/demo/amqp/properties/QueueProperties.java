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
public class QueueProperties {

	private String name;

	private Boolean durable=Boolean.TRUE;

	private Boolean exclusive;

	private Boolean autoDelete;
	
	private boolean deadLetterEnabled;
	
	private Map<String, Object> arguments = new LinkedHashMap<>();

}
