package com.example.cqrs.query.repository;

import com.example.cqrs.query.document.AccountDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

/**
 * 계좌 읽기 모델의 저장소
 */
public interface AccountQueryRepository extends MongoRepository<AccountDocument, String> {

    Optional<AccountDocument> findByAccountId(String accountId);

}
