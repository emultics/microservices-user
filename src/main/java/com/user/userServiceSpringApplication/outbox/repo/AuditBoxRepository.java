package com.user.userServiceSpringApplication.outbox.repo;

import com.user.userServiceSpringApplication.outbox.entity.AuditOutBox;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AuditBoxRepository extends JpaRepository<AuditOutBox, UUID> {
}
