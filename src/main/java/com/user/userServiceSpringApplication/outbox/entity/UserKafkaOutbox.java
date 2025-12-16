package com.user.userServiceSpringApplication.outbox.entity;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_kafka_outbox")
public class UserKafkaOutbox {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "aggregate_id", nullable = false)
    private UUID aggregateId;


    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private JsonNode userPayload;

    private String eventType;

//    private Boolean isConsumed;

    @Override
    public String toString() {
        return "UserKafkaOutbox{" +
                "id=" + id +
                ", userPayload='" + userPayload + '\'' +
                ", eventType='" + eventType + '\'' +
                '}';
    }
}
