package com.example.cqrs.repository.write;

import com.example.cqrs.entity.write.AccountSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountSnapshotRepository extends JpaRepository<AccountSnapshot, String> {
}
