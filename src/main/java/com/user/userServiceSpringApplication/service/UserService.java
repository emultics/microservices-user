package com.user.userServiceSpringApplication.service;

import com.user.userServiceSpringApplication.dto.UserRequest;
import com.user.userServiceSpringApplication.dto.UserUpdateRequest;
import com.user.userServiceSpringApplication.user.entity.User;
import com.user.userServiceSpringApplication.exception.EmailExistException;
import com.user.userServiceSpringApplication.user.repo.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    @Cacheable(value = "user-caching", key = "#userId", unless = "#result == null")
    public User getUserById(UUID userId){
        System.out.println("👉 Fetching from DATABASE...");
        return userRepository.findById(userId).orElseThrow(()-> new RuntimeException("user not available"));
    }

    public User createUser(User user){
        try {
            log.info("Initiate to Save record in user db");
            User userSaved = userRepository.save(user);
            log.info("User record created: {}", userSaved.toString());
            return userSaved;
        } catch (Exception e) {
            throw new RuntimeException("failed to create User!: "+ e.getMessage());
        }
    }

//    public List<User> createUsers(List<UserRequest> userRequest){
//        List<User> users = new ArrayList<>();
//        List<String> error = new ArrayList<>();
//        for(UserRequest u: userRequest){
//            boolean isExistUser = userRepository.existsByEmail(u.getEmail());
//            User user = new User();
//            if(!isExistUser) {
//                user.setName(u.getName());
//                user.setEmail(u.getEmail());
//                user.setPassword(u.getPassword());
//                user.setActive(u.isActive());
//
//                users.add(user);
//            }
//            else{
//                error.add("Email already exist: "+u.getEmail());
//            }
//        }
//        return userRepository.saveAll(users);
//    }

    public Map<String, Object> createUsers(List<UserRequest> userRequest){
        List<User> users = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        for(UserRequest u: userRequest){
            boolean isExistUser = userRepository.existsByEmail(u.getEmail());
            User user = new User();
            if(!isExistUser) {
                user.setName(u.getName());
                user.setEmail(u.getEmail());
                user.setPassword(u.getPassword());
                user.setActive(u.getActive());

                users.add(user);
            }
            else{
                errors.add(u.getEmail());
            }
        }
        if(!errors.isEmpty()){
            throw new EmailExistException("Email allready exist",errors);
        }
        List<User> newCreatedUser = userRepository.saveAll(users);

        Map<String, Object> response = new HashMap<>();
            response.put("NewCreatedUser", newCreatedUser);

        return response;
    }

    public User updateUsers(UUID id, UserUpdateRequest userRequest) {
        User user = userRepository.findById(id).orElseThrow(()-> new RuntimeException("user not found"));
        applyPatch(userRequest, user);

        return userRepository.save(user);
    }

    private static void applyPatch(UserUpdateRequest userRequest, User user) {
        if(userRequest.getName()!=null) {
            user.setName(userRequest.getName());
        }
        if(userRequest.getEmail()!=null) {
            user.setEmail(userRequest.getEmail());
        }
        if(!userRequest.isActive()) {
            user.setActive(userRequest.isActive());
        }
    }

    public void deleteUserById(UUID id) {
        User user = userRepository.findById(id).orElseThrow(()-> new RuntimeException("user not found"));
        userRepository.deleteById(id);
    }
}
