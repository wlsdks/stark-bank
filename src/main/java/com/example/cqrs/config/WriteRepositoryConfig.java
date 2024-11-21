package com.example.cqrs.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * 쓰기용 Repository 설정
 */
@Configuration
@EnableJpaRepositories(
        basePackages = "com.example.cqrs.repository.write",        // 이 패키지 하위는
        entityManagerFactoryRef = "writeEntityManagerFactory",     // 쓰기 EntityManager 사용
        transactionManagerRef = "writeTransactionManager"          // 쓰기 TransactionManager 사용
)
public class WriteRepositoryConfig {
}