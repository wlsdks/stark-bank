package com.example.cqrs.query.document;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * MongoDB에 저장되는 계좌 읽기 모델
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Document(collection = "account")
public class AccountDocument {

    @Id
    @Indexed(unique = true)  // accountId 필드에 유니크 인덱스 추가
    private String accountId; // 계좌 ID

    private double balance;   // 잔액

    private LocalDateTime lastUpdated; // 마지막 업데이트 시간

    // factory method
    public static AccountDocument of(String accountId, double balance, LocalDateTime lastUpdated) {
        return new AccountDocument(accountId, balance, lastUpdated);
    }

    // 잔액 변경 메서드
    public void changeBalance(double newBalance) {
        this.balance = newBalance;
        this.lastUpdated = LocalDateTime.now();
    }

    // 마지막 업데이트 시간 변경 메서드
    public void changeLastUpdated(LocalDateTime eventDate) {
        this.lastUpdated = eventDate;
    }

}