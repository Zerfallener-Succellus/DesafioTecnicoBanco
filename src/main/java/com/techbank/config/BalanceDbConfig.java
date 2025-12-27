package com.techbank.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties; // IMPORTANTE
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "com.techbank.domain.balance",
        entityManagerFactoryRef = "balanceEntityManagerFactory",
        transactionManagerRef = "balanceTransactionManager"
)
public class BalanceDbConfig {

    // 1. Propriedades da Base B
    @Bean
    @ConfigurationProperties("spring.datasource.balance")
    public DataSourceProperties balanceDataSourceProperties() {
        return new DataSourceProperties();
    }

    // 2. DataSource da Base B
    @Bean(name = "balanceDataSource")
    public DataSource balanceDataSource() {
        return balanceDataSourceProperties()
                .initializeDataSourceBuilder()
                .build();
    }

    @Bean(name = "balanceEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("balanceDataSource") DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages("com.techbank.domain.balance")
                .persistenceUnit("balance")
                .build();
    }

    @Bean(name = "balanceTransactionManager")
    public PlatformTransactionManager transactionManager(
            @Qualifier("balanceEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}