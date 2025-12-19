package com.user.userServiceSpringApplication.config;


import com.user.userServiceSpringApplication.constants.AppConstants;
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
                new RedisStandaloneConfiguration(AppConstants.REDIS_HOST, AppConstants.REDIS_PORT);
        config.setUsername(AppConstants.REDIS_USERNAME);
        config.setPassword(RedisPassword.of(AppConstants.REDIS_PASSWORD));
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
