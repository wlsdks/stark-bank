package com.example.cqrs.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * 읽기용 Repository 설정
 */
@Configuration
@EnableJpaRepositories(
        basePackages = "com.example.cqrs.repository.read",         // 이 패키지 하위는
        entityManagerFactoryRef = "readEntityManagerFactory",      // 읽기 EntityManager 사용
        transactionManagerRef = "readTransactionManager"           // 읽기 TransactionManager 사용
)
public class ReadRepositoryConfig {
}