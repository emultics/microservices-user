package com.user.userServiceSpringApplication.service;

import com.user.userServiceSpringApplication.Common;
import com.user.userServiceSpringApplication.dto.UserRequest;
import com.user.userServiceSpringApplication.enums.USEREVENT;
import com.user.userServiceSpringApplication.kafka.producer.UserProducer;
import com.user.userServiceSpringApplication.user.entity.UserKafkaOutbox;
import com.user.userServiceSpringApplication.user.repo.UserKafkaOutboxRepository;
import com.user.userServiceSpringApplication.user.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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
        User savedUser = userService.createUser(Common.fromUserRequestToUser(user));

        UserKafkaOutbox outbox = Common.toOutBox(savedUser, USEREVENT.CREATE);

        try {
            userKafkaOutboxRepository.save(outbox);
            log.info("Outbox Event Stored: {}", outbox.getEventId().toString());
        } catch (DataIntegrityViolationException e) {
            log.info("Duplicate Event ignored: {}", e.getMessage());
            throw new DataIntegrityViolationException(e.getMessage());
        } catch (Exception ex){
            log.info("Failed to created User! {}", ex.getMessage());
            throw new RuntimeException(ex.getMessage());
        }
    }



}
