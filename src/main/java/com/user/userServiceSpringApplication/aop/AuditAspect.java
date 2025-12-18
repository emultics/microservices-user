package com.user.userServiceSpringApplication.aop;

import com.user.userServiceSpringApplication.annotation.AuditRequest;
import com.user.userServiceSpringApplication.dto.AuditEvent;
import com.user.userServiceSpringApplication.enums.AuditEventType;
import com.user.userServiceSpringApplication.outbox.entity.AuditOutBox;
import com.user.userServiceSpringApplication.outbox.repo.AuditBoxRepository;
import com.user.userServiceSpringApplication.utils.AuditSecurityContext;
import com.user.userServiceSpringApplication.utils.JsonUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Aspect
@Component
@RequiredArgsConstructor
public class AuditAspect {
    private final AuditBoxRepository auditBoxRepository;

    private final Logger log = LoggerFactory.getLogger(AuditAspect.class);

    @Around("@annotation(auditRequest)")
    public Object audit(ProceedingJoinPoint pjp, AuditRequest auditRequest) throws Throwable{
        log.info("Trigger");
        long start = System.currentTimeMillis();
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        Object response = null;
        Exception error = null;
        try{
            response = pjp.proceed();
            return response;
        }catch (Exception ex){
            error = ex;
            throw ex;
        }finally {
            long time = System.currentTimeMillis() - start;
            AuditEvent auditEvent = AuditEvent.builder()
                    .userId(AuditSecurityContext.userId())
                    .roles(AuditSecurityContext.roles())
                    .authType(AuditSecurityContext.authType())
                    .httpMethod(request.getMethod())
                    .uri(request.getRequestURI())
                    .clientIp(request.getRemoteAddr())
                    .userAgent(request.getHeader("User-Agent"))
                    .request(mask(pjp.getArgs()))
                    .response(mask(response))
                    .executionTimeMs(time)
                    .timestamp(Instant.now())
                    .build();

            log.info("Audit: {}, time: {}", auditEvent.toString(), time);


            AuditOutBox audit = new AuditOutBox();

            audit.setId(UUID.randomUUID());
            audit.setAggregateId(auditEvent.getUserId());
            audit.setEventType(AuditEventType.USER_API_CALL.name());
            audit.setAggregateType("AUDIT_LOG");
            audit.setPayload(JsonUtil.toJsonNode(auditEvent));
            log.info("Payload: {}", audit.toString());
            auditBoxRepository.save(audit);
            log.info("Audit recorded");
        }

    }

    private String mask(Object obj){
        if(obj==null) return null;
        String jsonObject = JsonUtil.toJson(obj);
        return jsonObject.toString().replaceAll(
                "(\"(password|otp|token|jwt)\"\\s*:\\s*\")[^\"]+(\")", "$1********$3"
        );
    }
}
