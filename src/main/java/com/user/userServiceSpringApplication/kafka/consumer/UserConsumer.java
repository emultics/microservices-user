package com.user.userServiceSpringApplication.kafka.consumer;

import com.user.userServiceSpringApplication.entity.User;
import com.user.userServiceSpringApplication.entity.UserKafkaOutbox;
import com.user.userServiceSpringApplication.repository.UserKafkaOutboxRepository;
import com.user.userServiceSpringApplication.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

@Service
public class UserConsumer {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserKafkaOutboxRepository userKafkaOutboxRepository;

    private Logger logger = LoggerFactory.getLogger(UserConsumer.class);

    @KafkaListener(topics = "${kafka.topic.name}", groupId = "user-group")
    public void consume(String message,  @Header(KafkaHeaders.RECEIVED_KEY) String outboxId){
        UserKafkaOutbox outbox = userKafkaOutboxRepository.findById(UUID.fromString(outboxId)).orElseThrow();
        outbox.setConsumed(true);
        userKafkaOutboxRepository.save(outbox);

        if (outbox.isConsumed()) {
            User user = convertToUser(outbox.getUserPayload());
            userRepository.save(user);
            logger.info("user saved in database");
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
