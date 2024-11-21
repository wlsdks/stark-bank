package com.example.cqrs.repository.write;

import com.example.cqrs.entity.write.event.base.BaseAccountEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * writeEntityManagerFactory와 writeTransactionManager 사용
 * @Primary로 지정되어 있어 별도 설정 없이도 쓰기 DB 사용
 */
@Repository
public interface AccountEventRepository extends JpaRepository<BaseAccountEvent, Long> {

    List<BaseAccountEvent> findByAccountIdAndEventDateAfterOrderByEventDateAsc(
            String accountId, LocalDateTime afterDate);

    List<BaseAccountEvent> findByAccountIdOrderByEventDateDesc(String accountId);

    List<BaseAccountEvent> findByAccountIdOrderByEventDateAsc(String accountId);

    List<BaseAccountEvent> findByMetadataCorrelationId(String correlationId);

    List<BaseAccountEvent> findByMetadataUserIdOrderByEventDateDesc(String userId);

    long countByAccountIdAndEventDateAfter(String accountId, LocalDateTime afterDate);

}