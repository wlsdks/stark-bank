package com.example.cqrs.infrastructure.config.database;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * 쓰기용 Repository 설정
 */
@Configuration
@EnableJpaRepositories(
        basePackages = {
                "com.example.cqrs.infrastructure.persistence.command.repository",
                "com.example.cqrs.infrastructure.eventstore.repository"
        },
        entityManagerFactoryRef = "writeEntityManagerFactory",     // 쓰기 EntityManager 사용
        transactionManagerRef = "writeTransactionManager"          // 쓰기 TransactionManager 사용
)
public class WriteRepositoryConfig {
}