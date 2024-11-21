package com.example.cqrs.repository.read;

import event_sourcing.study.entity.read.AccountReadEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * readEntityManagerFactory와 readTransactionManager 사용
 * 패키지 위치에 따라 자동으로 읽기 DB 사용
 */
@Repository
public interface AccountReadRepository extends JpaRepository<AccountReadEntity, String> {
}
