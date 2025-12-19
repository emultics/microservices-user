package com.user.userServiceSpringApplication.user.entity;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_kafka_outbox", uniqueConstraints = @UniqueConstraint(
        name = "uq_outbox_event",
        columnNames = "eventId"
))
public class UserKafkaOutbox {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "event_id", nullable = false, updatable = false)
    private String eventId;

    @Column(name = "aggregate_id", nullable = false, updatable = false)
    private UUID aggregateId;

    @Column(name = "aggregate_type", nullable = false, updatable = false)
    private String aggregateType; // USER record

    @Column(name = "event_type", nullable = false, updatable = false)
    private String eventType; // USER_CREATED

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", name = "payload", nullable = false)
    private JsonNode userPayload;

    @Override
    public String toString() {
        return "UserKafkaOutbox{" +
                "id=" + id +
                ", userPayload='" + userPayload + '\'' +
                ", eventType='" + eventType + '\'' +
                '}';
    }
}
