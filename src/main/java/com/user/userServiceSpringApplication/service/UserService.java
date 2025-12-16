package com.user.userServiceSpringApplication.service;

import com.user.userServiceSpringApplication.dto.UserRequest;
import com.user.userServiceSpringApplication.dto.UserUpdateRequest;
import com.user.userServiceSpringApplication.entity.User;
import com.user.userServiceSpringApplication.exception.EmailExistException;
import com.user.userServiceSpringApplication.kafka.producer.UserProducer;
import com.user.userServiceSpringApplication.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserProducer userProducer;

    @Cacheable(value = "user-caching", key = "#userId")
    public User getUserById(UUID userId){
        System.out.println("👉 Fetching from DATABASE...");
        return userRepository.findById(userId).orElseThrow(()-> new RuntimeException("user not available"));
    }

    public User createUser(UserRequest userRequest){
        User user = new User();
        user.setName(userRequest.getName());
        user.setEmail(userRequest.getEmail());
        user.setPassword(userRequest.getPassword());
        user.setActive(userRequest.isActive());
        //userProducer.sendUser(user);
        return userRepository.save(user);
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
                user.setActive(u.isActive());

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
