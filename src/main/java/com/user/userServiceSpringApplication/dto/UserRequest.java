package com.user.userServiceSpringApplication.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserRequest {
    @NotNull(message = "name should not be null")
    private String name;

    @Email
    private String email;

    @NotNull(message = "Password should not be null")
    @Size(min = 6, max = 20, message = "Min length should be 6")
    private String password;

    @JsonProperty(defaultValue = "true")
    @NotNull(message = "isActive should not be null, either true or false")
    private Boolean active;
}
