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
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(
        basePackages = AppConstants.USER_REPOSITORY_PACKAGE,
        entityManagerFactoryRef = AppConstants.USER_ENTITY_MANAGER_FACTORY,
        transactionManagerRef = AppConstants.USER_TRANSACTION_MANAGER
)
public class UserDbConfig {
    @Primary
    @Bean(name = AppConstants.USER_DATASOURCE)
    @ConfigurationProperties(prefix = AppConstants.USER_DATASOURCE_PROPERTIES_PREFIX)
    public DataSource userDataSource(){
        return new HikariDataSource();
    }

    @Primary
    @Bean
    public LocalContainerEntityManagerFactoryBean userEntityManagerFactory(EntityManagerFactoryBuilder builder){
        return builder.dataSource(userDataSource())
                .packages(AppConstants.USER_ENTITY_PACKAGE)
                .persistenceUnit(AppConstants.USER_PERSISTENCE_UNIT)
                .build();

    }

    @Primary
    @Bean
    public PlatformTransactionManager userTransactionManager(@Qualifier(AppConstants.USER_ENTITY_MANAGER_FACTORY)EntityManagerFactory emf){
        return new JpaTransactionManager(emf);
    }


}
