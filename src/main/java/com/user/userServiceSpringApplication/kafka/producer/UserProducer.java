package com.user.userServiceSpringApplication.kafka.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserProducer {
    private Logger logger = LoggerFactory.getLogger(UserProducer.class);

    private final String topic;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public UserProducer(@Value("${kafka.topic.name}") String topic, KafkaTemplate<String, String> kafkaTemplate) {
        this.topic = topic;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendUser(String key, String payload){
        logger.info("sending user to kafka: {}", payload);
        kafkaTemplate.send(topic, key, payload);
    }
}
