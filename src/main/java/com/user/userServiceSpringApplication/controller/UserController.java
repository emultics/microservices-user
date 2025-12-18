package com.user.userServiceSpringApplication.controller;

import com.user.userServiceSpringApplication.annotation.AuditRequest;
import com.user.userServiceSpringApplication.dto.UserRequest;
import com.user.userServiceSpringApplication.dto.UserUpdateRequest;
import com.user.userServiceSpringApplication.enums.AuditEventType;
import com.user.userServiceSpringApplication.user.entity.User;
import com.user.userServiceSpringApplication.enums.USEREVENT;
import com.user.userServiceSpringApplication.service.UserOutboxService;
import com.user.userServiceSpringApplication.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private UserOutboxService userOutboxService;

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @AuditRequest
    @GetMapping("/fetch/{id}")
    public ResponseEntity<User> getUserById(@PathVariable String id){
        UUID uuid = UUID.fromString(id);
        User user = userService.getUserById(uuid);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @AuditRequest
    @PostMapping("/user/create")
    public String createUser(@Valid @RequestBody UserRequest userRequest){
        log.info("Getting UserRequest Body: {}", userRequest.toString());
        userOutboxService.createUserEvent(userRequest, USEREVENT.CREATE);
        return "User event saved to outbox and published";
    }

//    @PostMapping("/users/create")
//    public ResponseEntity<List<User>> createUsers(@RequestBody List<UserRequest> userRequest){
//        List<User> users = userService.createUsers(userRequest);
//        return ResponseEntity.status(HttpStatus.CREATED).body(users);
//    }

    @PostMapping("/users/create")
    public ResponseEntity<Map<String,Object>> createUsers(@RequestBody List<UserRequest> userRequest){
        Map<String, Object> users = userService.createUsers(userRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(users);
    }

    @PatchMapping("/db/migration/user/{id}")
    public ResponseEntity<User> updateUsers(@PathVariable String id, @RequestBody UserUpdateRequest userUpdateRequest){
        User user = userService.updateUsers(UUID.fromString(id),userUpdateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @DeleteMapping("db/migration/user/{id}")
    public String deleteUserById(@PathVariable UUID id){
        userService.deleteUserById(id);
        return "UserId: "+ id +" has been deleted";
    }
}
