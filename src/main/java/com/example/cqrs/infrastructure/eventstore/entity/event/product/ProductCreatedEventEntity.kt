package com.example.cqrs.infrastructure.eventstore.entity.event.product

import com.example.cqrs.infrastructure.eventstore.entity.base.ProductEventBaseEntity
import com.example.cqrs.infrastructure.eventstore.entity.base.metadata.EventMetadata
import com.example.cqrs.infrastructure.persistence.command.entity.ProductType
import jakarta.persistence.*
import java.time.LocalDateTime

/**
 * 금융 상품 생성 이벤트
 */
@DiscriminatorValue("ProductCreatedEvent")
@Entity
class ProductCreatedEventEntity(
    @Column(name = "productId", nullable = false)
    val productId: String,

    @Column(name = "name", nullable = false)
    val name: String,

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    val type: ProductType,

    @Column(name = "description")
    val description: String?,

    @Column(name = "interestRate", nullable = false)
    val interestRate: Double,

    @Column(name = "termInMonths", nullable = false)
    val termInMonths: Int,

    @Column(name = "minimumAmount", nullable = false)
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
            type: ProductType,
            description: String?,
            interestRate: Double,
            termInMonths: Int,
            minimumAmount: Double,
            eventDate: LocalDateTime,
            metadata: EventMetadata
        ): ProductCreatedEventEntity {
            return ProductCreatedEventEntity(
                productId = productId,
                name = name,
                type = type,
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