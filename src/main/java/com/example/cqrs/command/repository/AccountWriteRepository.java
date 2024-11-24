package com.example.cqrs.command.repository;

import com.example.cqrs.command.entity.AccountWrite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * readEntityManagerFactory와 readTransactionManager 사용
 * 패키지 위치에 따라 자동으로 읽기 DB 사용
 */
@Repository
public interface AccountWriteRepository extends JpaRepository<AccountWrite, String> {
}
