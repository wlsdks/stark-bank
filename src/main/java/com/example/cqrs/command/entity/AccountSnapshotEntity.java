package com.example.cqrs.command.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 계좌의 스냅샷을 저장하는 엔티티입니다.
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Entity
@Table(name = "account_snapshot")
public class AccountSnapshotEntity {

    @Id
    private String accountId;  // 계좌의 ID (기본 키)

    @Column(name = "balance")
    private double balance;    // 계좌의 잔액

    @Column(name = "snapshot_date")
    private LocalDateTime snapshotDate;  // 스냅샷 생성 시간

    // factory method
    public static AccountSnapshotEntity of(String accountId, double balance, LocalDateTime now) {
        return new AccountSnapshotEntity(accountId, balance, now);
    }

}
