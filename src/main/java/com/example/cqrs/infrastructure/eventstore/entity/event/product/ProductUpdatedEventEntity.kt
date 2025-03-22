package com.example.cqrs.infrastructure.eventstore.entity.event.product

import com.example.cqrs.infrastructure.eventstore.entity.base.ProductEventBaseEntity
import com.example.cqrs.infrastructure.eventstore.entity.base.metadata.EventMetadata
import jakarta.persistence.Column
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import java.time.LocalDateTime

/**
 * 금융 상품 수정 이벤트
 */
@DiscriminatorValue("ProductUpdatedEvent")
@Entity
class ProductUpdatedEventEntity(
    @Column(nullable = false)
    val productId: String,

    @Column(nullable = false)
    val name: String,

    @Column(nullable = true)
    val description: String?,

    @Column(nullable = false)
    val interestRate: Double,

    @Column(nullable = false)
    val termInMonths: Int,

    @Column(nullable = false)
    val minimumAmount: Double,

    eventDate: LocalDateTime,
    metadata: EventMetadata
) : ProductEventBaseEntity(
    id = null,
    eventDate = eventDate,
    metadata = metadata
) {
    companion object {
        fun of(
            productId: String,
            name: String,
            description: String?,
            interestRate: Double,
            termInMonths: Int,
            minimumAmount: Double,
            eventDate: LocalDateTime,
            metadata: EventMetadata
        ): ProductUpdatedEventEntity {
            return ProductUpdatedEventEntity(
                productId = productId,
                name = name,
                description = description,
                interestRate = interestRate,
                termInMonths = termInMonths,
                minimumAmount = minimumAmount,
                eventDate = eventDate,
                metadata = metadata
            )
        }
    }
}