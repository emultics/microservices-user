package com.user.userServiceSpringApplication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class UserServiceSpringApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserServiceSpringApplication.class, args);
	}

}
