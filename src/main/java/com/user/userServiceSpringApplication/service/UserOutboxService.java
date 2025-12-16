package com.user.userServiceSpringApplication.service;

import com.user.userServiceSpringApplication.dto.UserRequest;
import com.user.userServiceSpringApplication.kafka.producer.UserProducer;
import com.user.userServiceSpringApplication.entity.UserKafkaOutbox;
import com.user.userServiceSpringApplication.repository.UserKafkaOutboxRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;


@Service
public class UserOutboxService {
    @Autowired
    private UserKafkaOutboxRepository userKafkaOutboxRepository;
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    @Autowired
    private UserProducer userProducer;

    public void createUserEvent(UserRequest user, String eventType){
        UserKafkaOutbox outbox = new UserKafkaOutbox();
        //outbox.setId(UUID.randomUUID());
        outbox.setUserPayload(convertToJson(user));
        outbox.setEventType(eventType);
        outbox.setConsumed(false);

        userKafkaOutboxRepository.save(outbox);

        userProducer.sendUser(outbox.getId().toString(), outbox.getUserPayload());
    }

    private String convertToJson(UserRequest user) {
        try{
            return new ObjectMapper().writeValueAsString(user);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

}
