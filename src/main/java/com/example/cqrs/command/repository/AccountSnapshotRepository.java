package com.example.cqrs.command.repository;

import com.example.cqrs.command.entity.AccountSnapshotEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountSnapshotRepository extends JpaRepository<AccountSnapshotEntity, String> {
}
