package com.user.userServiceSpringApplication.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


public final class JsonUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private JsonUtil() {
    }


    public static <T> String toJson(T object){
        try{
            return objectMapper.writeValueAsString(object);
        }catch (JsonProcessingException ex){
            throw  new RuntimeException("JSON Serialization failed", ex);
        }
    }

    public static <T>JsonNode toJsonNode(T object){
        return objectMapper.valueToTree(object);
    }
}
