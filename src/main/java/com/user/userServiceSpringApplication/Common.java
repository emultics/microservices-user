package com.user.userServiceSpringApplication;

import com.user.userServiceSpringApplication.dto.UserRequest;
import com.user.userServiceSpringApplication.user.entity.User;
import com.user.userServiceSpringApplication.outbox.entity.UserKafkaOutbox;
import com.user.userServiceSpringApplication.enums.USEREVENT;
import com.user.userServiceSpringApplication.utils.JsonUtil;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class Common {


    public static User fromUserRequestToUser(UserRequest userRequest){
        User user = new User();
        user.setName(userRequest.getName());
        user.setEmail(userRequest.getEmail());
        user.setIsActive(Optional.ofNullable(userRequest.getActive()).orElse(true));
        return user;
    }

    public static UserKafkaOutbox fromUserRequestToUserOutBox(UserRequest userRequest, USEREVENT userevent, boolean isConsumed){
        userRequest.setActive(Optional.ofNullable(userRequest.getActive()).orElse(true));
        UserKafkaOutbox kafkaOutbox = new UserKafkaOutbox();
        kafkaOutbox.setEventType(userevent.name());
        kafkaOutbox.setUserPayload(JsonUtil.toJsonNode(userRequest));
        return kafkaOutbox;
    }
}
