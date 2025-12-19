package com.user.userServiceSpringApplication.config;

import com.user.userServiceSpringApplication.constants.AppConstants;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.user.userServiceSpringApplication.outbox.repo",
        entityManagerFactoryRef = "outboxEntityManagerFactory",
        transactionManagerRef = "outboxTransactionManager"
)
public class OutBoxDbConfig {
    @Bean("outboxDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.outbox")
    public DataSource outboxDataSource(){
        return new HikariDataSource();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean outboxEntityManagerFactory(
            EntityManagerFactoryBuilder builder
    ){
        return builder.dataSource(outboxDataSource())
                .packages(AppConstants.OUTBOX_ENTITY_PACKAGE)
                .persistenceUnit("outboxPU")
                .build();
    }

    @Bean
    public PlatformTransactionManager outboxTransactionManager(@Qualifier("outboxEntityManagerFactory") EntityManagerFactory emf){
        return new JpaTransactionManager(emf);
    }


}
