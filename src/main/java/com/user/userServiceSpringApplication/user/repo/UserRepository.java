package com.user.userServiceSpringApplication.user.repo;

import com.user.userServiceSpringApplication.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    public boolean existsByEmail(String email);
}
