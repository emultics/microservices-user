package com.user.userServiceSpringApplication.dto;

import lombok.*;

@Getter
@Setter
public class UserUpdateRequest {
    private String name;
    private String email;
    private boolean isActive;
}
