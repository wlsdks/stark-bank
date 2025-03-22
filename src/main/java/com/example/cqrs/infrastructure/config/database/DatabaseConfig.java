package com.example.cqrs.infrastructure.config.database;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * 데이터베이스 설정을 관리하는 설정 클래스입니다.
 * CQRS 패턴 구현을 위해 읽기/쓰기 데이터소스를 분리합니다.
 *
 * @Primary 애노테이션은 쓰기 작업 관련 빈에 지정되어 있습니다.
 * 이는 특정 데이터소스나 트랜잭션 매니저가 명시적으로 지정되지 않은 경우,
 * 기본적으로 쓰기 작업용 빈이 주입되도록 하기 위함입니다.
 */
@Configuration
@EnableTransactionManagement
public class DatabaseConfig {

    /**
     * 쓰기 작업을 위한 데이터소스를 생성합니다.
     *
     * @return 쓰기 작업용 DataSource
     * @Primary 지정으로 기본 데이터소스로 사용됩니다.
     * application.yml의 spring.datasource.write 설정을 사용합니다.
     */
    @Primary
    @Bean(name = "writeDataSource")
    @ConfigurationProperties("spring.datasource.write")
    public DataSource writeDataSource() {
        return DataSourceBuilder.create().build();
    }

    /**
     * 읽기 작업을 위한 데이터소스를 생성합니다.
     * application.yml의 spring.datasource.read 설정을 사용합니다.
     *
     * @return 읽기 작업용 DataSource
     */
    @Bean(name = "readDataSource")
    @ConfigurationProperties("spring.datasource.read")
    public DataSource readDataSource() {
        return DataSourceBuilder.create().build();
    }

    /**
     * 쓰기 작업을 위한 EntityManagerFactory를 생성합니다.
     * 이벤트 저장소 관련 엔티티만을 관리하도록 패키지를 제한합니다.
     *
     * @param builder    EntityManagerFactory 생성을 위한 빌더
     * @param dataSource 쓰기 작업용 데이터소스
     * @return 쓰기 작업용 EntityManagerFactory
     * @Primary 지정으로 기본 EntityManagerFactory로 사용됩니다.
     */
    @Primary
    @Bean(name = "writeEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean writeEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("writeDataSource") DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages(
                        "com.example.cqrs.infrastructure.persistence.command.entity",  // 커맨드 엔티티 패키지
                        "com.example.cqrs.infrastructure.eventstore.event"  // 계좌 이벤트 엔티티 패키지
                )
                .persistenceUnit("write")
                .properties(hibernateProperties())
                .build();
    }

    /**
     * 읽기 작업을 위한 EntityManagerFactory를 생성합니다.
     * 읽기 모델 관련 엔티티만을 관리하도록 패키지를 제한합니다.
     *
     * @param builder    EntityManagerFactory 생성을 위한 빌더
     * @param dataSource 읽기 작업용 데이터소스
     * @return 읽기 작업용 EntityManagerFactory
     */
    @Bean(name = "readEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean readEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("readDataSource") DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages("com.example.cqrs.infrastructure.persistence.query.entity")  // 쿼리 엔티티 패키지
                .persistenceUnit("read")
                .properties(hibernateProperties())
                .build();
    }

    /**
     * 쓰기 작업을 위한 트랜잭션 매니저를 생성합니다.
     *
     * @param entityManagerFactory 쓰기 작업용 EntityManagerFactory
     * @return 쓰기 작업용 트랜잭션 매니저
     * @Primary 지정으로 기본 트랜잭션 매니저로 사용됩니다.
     */
    @Primary
    @Bean(name = "writeTransactionManager")
    public PlatformTransactionManager writeTransactionManager(
            @Qualifier("writeEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    /**
     * 읽기 작업을 위한 트랜잭션 매니저를 생성합니다.
     *
     * @param entityManagerFactory 읽기 작업용 EntityManagerFactory
     * @return 읽기 작업용 트랜잭션 매니저
     */
    @Bean(name = "readTransactionManager")
    public PlatformTransactionManager readTransactionManager(
            @Qualifier("readEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    /**
     * Hibernate 관련 속성을 설정합니다.
     * 읽기/쓰기 데이터소스 모두에 적용되는 공통 설정입니다.
     *
     * @return Hibernate 설정 속성
     */
    private Map<String, Object> hibernateProperties() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "update");
        properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        properties.put("hibernate.format_sql", "true");
        properties.put("hibernate.default_batch_fetch_size", 10);
        return properties;
    }

}