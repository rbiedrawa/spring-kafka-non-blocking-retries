package com.rbiedrawa.kafka.app;


import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MyTopicListener {
	private static final String GROUP_ID = "my-topic-listener";

	@RetryableTopic(attempts = "3",
					backoff = @Backoff(delay = 1000, multiplier = 2.0))
	@KafkaListener(id = GROUP_ID, topics = "my.topic.events")
	public void listen(String event, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
		log.info("Received event {} from topic {}", event, topic);
		throw new RuntimeException(String.format("Unrecoverable failure during processing of event %s", event));
	}

	@DltHandler
	public void dlt(String event, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
		log.info("Event {} handled by dlq topic: {}", event, topic);
	}
}
