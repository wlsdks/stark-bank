package com.example.cqrs.query.entity;

import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * MongoDB에 저장되는 계좌 읽기 모델
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Document(collection = "account_view")
public class AccountView {

    @Id
    private String accountId; // 계좌 ID
    private double balance;   // 잔액
    private LocalDateTime lastUpdated; // 마지막 업데이트 시간

    // factory method
    public static AccountView of(String accountId, double balance, LocalDateTime lastUpdated) {
        return new AccountView(accountId, balance, lastUpdated);
    }

}