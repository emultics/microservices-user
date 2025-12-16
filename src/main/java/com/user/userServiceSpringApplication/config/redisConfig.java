package com.user.userServiceSpringApplication.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

import java.time.Duration;

@Configuration
public class redisConfig {
    @Bean
    public LettuceClientConfiguration lettuceClientConfiguration(){
        return LettuceClientConfiguration.builder()
                .commandTimeout(Duration.ofSeconds(5))
                .build();
    }

    @Bean
    public RedisStandaloneConfiguration redisStandaloneConfiguration(){
        RedisStandaloneConfiguration config =
                new RedisStandaloneConfiguration("localhost", 6379);
        config.setUsername("default");
        config.setPassword(RedisPassword.of("redis!"));
        return config;
    }

    @Bean
    public LettuceConnectionFactory redisConnectionFactory(
            RedisStandaloneConfiguration configuration,
            LettuceClientConfiguration lettuceClientConfiguration
    ){
        return new LettuceConnectionFactory(configuration, lettuceClientConfiguration);
    }
}
