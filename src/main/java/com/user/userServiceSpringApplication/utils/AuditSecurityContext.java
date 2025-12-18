package com.user.userServiceSpringApplication.utils;

public class AuditSecurityContext {
    public static String userId(){
        return "GUEST";
    }

    public static String roles(){
        return "NONE";
    }

    public static String authType(){
        return "NONE";
    }
}
