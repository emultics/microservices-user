package com.user.userServiceSpringApplication;

import com.user.userServiceSpringApplication.dto.UserRequest;
import com.user.userServiceSpringApplication.user.entity.User;
import com.user.userServiceSpringApplication.user.entity.UserKafkaOutbox;
import com.user.userServiceSpringApplication.enums.USEREVENT;
import com.user.userServiceSpringApplication.utils.EventIdGenerator;
import com.user.userServiceSpringApplication.utils.JsonUtil;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class Common {
    public static User fromUserRequestToUser(UserRequest userRequest){
        User user = new User();
        user.setName(userRequest.getName());
        user.setEmail(userRequest.getEmail());
        user.setPassword(userRequest.getPassword());
        user.setIsActive(Optional.ofNullable(userRequest.getActive()).orElse(true));
        return user;
    }

    public static UserKafkaOutbox toOutBox(User user, USEREVENT userevent){
        UserKafkaOutbox kafkaOutbox = new UserKafkaOutbox();
        kafkaOutbox.setAggregateType("USER");
        kafkaOutbox.setAggregateId(user.getId());
        kafkaOutbox.setEventId(EventIdGenerator.generate(user.getId(), userevent));
        kafkaOutbox.setEventType("USER_"+userevent.name());
        kafkaOutbox.setUserPayload(JsonUtil.toJsonNode(user));
        return kafkaOutbox;
    }


}
