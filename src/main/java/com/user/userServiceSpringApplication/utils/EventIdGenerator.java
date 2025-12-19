package com.user.userServiceSpringApplication.utils;

import com.user.userServiceSpringApplication.enums.USEREVENT;

import java.util.UUID;

public class EventIdGenerator {
    public static String generate(UUID aggregateId, USEREVENT event){
        return aggregateId + ":"+event.name();
    }


}
