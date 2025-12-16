package com.user.userServiceSpringApplication.repository;

import com.user.userServiceSpringApplication.entity.UserKafkaOutbox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserKafkaOutboxRepository extends JpaRepository<UserKafkaOutbox, UUID> {

}
