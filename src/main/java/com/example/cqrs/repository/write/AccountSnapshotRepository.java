package com.example.cqrs.repository.write;

import event_sourcing.study.entity.write.AccountSnapshotEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountSnapshotRepository extends JpaRepository<AccountSnapshotEntity, String> {
}
