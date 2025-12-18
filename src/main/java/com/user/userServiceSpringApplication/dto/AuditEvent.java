package com.user.userServiceSpringApplication.dto;

import lombok.*;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuditEvent {
    private String userId;
    private String roles;
    private String authType;
    private String httpMethod;

    private String uri;
    private int status;
    private String clientIp;
    private String userAgent;
    private Object request;
    private Object response;

    private long executionTimeMs;
    private Instant timestamp;

        @Override
        public String toString() {
            return "Builder{" +
                    "userId='" + userId + '\'' +
                    ", roles='" + roles + '\'' +
                    ", authType='" + authType + '\'' +
                    ", httpMethod='" + httpMethod + '\'' +
                    ", uri='" + uri + '\'' +
                    ", status=" + status +
                    ", clientIp='" + clientIp + '\'' +
                    ", userAgent='" + userAgent + '\'' +
                    ", request=" + request +
                    ", response=" + response +
                    ", executionTimeMs=" + executionTimeMs +
                    ", timestamp=" + timestamp +
                    '}';
        }
}
