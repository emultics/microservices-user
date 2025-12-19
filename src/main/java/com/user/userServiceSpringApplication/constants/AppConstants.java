package com.user.userServiceSpringApplication.constants;

public final class AppConstants {
    public static final String USER_AGENT = "User-Agent";

    public static final String SENSITIVE_DATA_REGEX = "(\"(password|otp|token|jwt)\"\\s*:\\s*\")[^\"]+(\")";
    public static final String SENSITIVE_MASK_DATA = "$1********$3";

    public static final String REDIS_HOST = "localhost";
    public static final int REDIS_PORT = 6379;
    public static final String REDIS_USERNAME = "default";
    public static final String REDIS_PASSWORD = "redis!";

    public static final String USER_ENTITY_PACKAGE = "com.user.userServiceSpringApplication.user.entity";
    public static final String OUTBOX_ENTITY_PACKAGE = "com.user.userServiceSpringApplication.outbox.entity";

    public static final String USER_DATASOURCE = "userDataSource";
    public static final String OUTBOX_DATASOURCE = "outboxDataSource";
    public static final String USER_FLYWAY_MIGRATION_LOCATION = "classpath:db/migration/user";
    public static final String OUTBOX_FLYWAY_MIGRATION_LOCATION = "classpath:db/migration/outbox";

}
