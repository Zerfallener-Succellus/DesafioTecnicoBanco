package com.techbank.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties; // IMPORTANTE
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "com.techbank.domain.account",
        entityManagerFactoryRef = "accountEntityManagerFactory",
        transactionManagerRef = "accountTransactionManager"
)
public class AccountDbConfig {


    @Bean
    @ConfigurationProperties("spring.datasource.account")
    public DataSourceProperties accountDataSourceProperties() {
        return new DataSourceProperties();
    }


    @Primary
    @Bean(name = "accountDataSource")
    public DataSource accountDataSource() {
        return accountDataSourceProperties()
                .initializeDataSourceBuilder()
                .build();
    }

    @Primary
    @Bean(name = "accountEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("accountDataSource") DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages("com.techbank.domain.account")
                .persistenceUnit("account")
                .build();
    }

    @Primary
    @Bean(name = "accountTransactionManager")
    public PlatformTransactionManager transactionManager(
            @Qualifier("accountEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}