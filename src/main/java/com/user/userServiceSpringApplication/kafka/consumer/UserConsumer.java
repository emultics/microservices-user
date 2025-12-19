package com.user.userServiceSpringApplication.kafka.consumer;

import com.user.userServiceSpringApplication.user.entity.User;
import com.user.userServiceSpringApplication.user.entity.UserKafkaOutbox;
import com.user.userServiceSpringApplication.user.repo.UserKafkaOutboxRepository;
import com.user.userServiceSpringApplication.user.repo.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

@Service
public class UserConsumer {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserKafkaOutboxRepository userKafkaOutboxRepository;

    private Logger logger = LoggerFactory.getLogger(UserConsumer.class);

//    @KafkaListener(topics = "${kafka.topic.name}", groupId = "user-group")
    @Transactional
    public void consume(String message,  @Header(KafkaHeaders.RECEIVED_KEY) String outboxId){
        logger.info("OutboxId: {}", outboxId);
        UserKafkaOutbox outbox = userKafkaOutboxRepository
                .findById(UUID.fromString(outboxId))
                .orElseThrow(() -> new RuntimeException("Outbox not found"));


        logger.info("Getting User from Outbox: {}", outbox.getId().toString());

        try {
            User user = convertToUser(outbox.getUserPayload().asText());
            userRepository.save(user);
            logger.info("User saved and outbox marked as consumed: {}", outbox.getId());
        } catch (Exception e) {
            throw new RuntimeException("User Creation Failed: "+e.getMessage());
        }
    }

    private User convertToUser(String userPayload) {
        try {
            return new ObjectMapper().readValue(userPayload, User.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
