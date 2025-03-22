package com.example.cqrs.infrastructure.persistence.command.repository

import com.example.cqrs.infrastructure.persistence.command.entity.ProductEntity
import org.springframework.data.jpa.repository.JpaRepository

// command에서 작업에 필요한 조회는 여기서 처리 (CQRS에서 중요한 것은 명령과 쿼리의 책임을 분리하는 것이지 모든 종류의 조회 작업을 쿼리 측으로 해결하는것이 아니다.)
interface ProductRepository : JpaRepository<ProductEntity, String> {

    fun findByActiveTrue(): List<ProductEntity>

}