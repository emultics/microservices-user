package com.user.userServiceSpringApplication.config;

import com.user.userServiceSpringApplication.constants.AppConstants;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.sql.DataSource;

@DependsOn({"userDataSource", "outboxDataSource"})
@Configuration
public class FlywayConfig {
    @Bean(initMethod = "migrate")
    public Flyway userFlyway(@Qualifier("userDataSource")DataSource ds){
        return Flyway.configure()
                .dataSource(ds)
                .locations(AppConstants.USER_FLYWAY_MIGRATION_LOCATION)
                .baselineOnMigrate(true)
                .load();
    }

    @Bean(initMethod = "migrate")
    public Flyway outBoxFlyway(@Qualifier("outboxDataSource")DataSource ds){
        return Flyway.configure()
                .dataSource(ds)
                .locations(AppConstants.OUTBOX_FLYWAY_MIGRATION_LOCATION)
                .baselineOnMigrate(true)
                .load();
    }
}
