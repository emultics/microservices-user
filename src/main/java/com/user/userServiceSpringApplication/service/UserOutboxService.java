package com.user.userServiceSpringApplication.service;

import com.user.userServiceSpringApplication.Common;
import com.user.userServiceSpringApplication.dto.UserRequest;
import com.user.userServiceSpringApplication.enums.USEREVENT;
import com.user.userServiceSpringApplication.kafka.producer.UserProducer;
import com.user.userServiceSpringApplication.outbox.entity.UserKafkaOutbox;
import com.user.userServiceSpringApplication.outbox.repo.UserKafkaOutboxRepository;
import com.user.userServiceSpringApplication.user.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class UserOutboxService {
    @Autowired
    private UserKafkaOutboxRepository userKafkaOutboxRepository;
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    @Autowired
    private UserProducer userProducer;

    @Autowired
    private UserService userService;

    private static final Logger log = LoggerFactory.getLogger(UserOutboxService.class);

    @Transactional
    public void createUserEvent(UserRequest user, USEREVENT userevent){
        UserKafkaOutbox outbox = Common.fromUserRequestToUserOutBox(user, USEREVENT.CREATE, false);
        log.info("Request: {}", outbox.toString());

        try {
            User savedUser = userService.createUser(Common.fromUserRequestToUser(user));
            log.info("Payload trying to save in outbox db, payload: {}", outbox.toString());
            outbox.setAggregateId(savedUser.getId());
            userKafkaOutboxRepository.save(outbox);
            log.info("Payload going to produce in kafka, with EVENT: {}", USEREVENT.CREATE.toString());
            log.info("Payload successfully Saved!");
            log.info("Payload Successfully Published!");
        } catch (Exception e) {
            log.info("Error: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }



}
