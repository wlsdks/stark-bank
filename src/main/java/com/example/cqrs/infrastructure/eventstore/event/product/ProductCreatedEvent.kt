package com.example.cqrs.infrastructure.eventstore.event.product

import com.example.cqrs.infrastructure.persistence.command.entity.ProductType
import com.example.cqrs.infrastructure.eventstore.base.ProductEvent
import com.example.cqrs.infrastructure.eventstore.base.metadata.EventMetadata
import jakarta.persistence.*
import java.time.LocalDateTime

/**
 * 금융 상품 생성 이벤트
 */
@DiscriminatorValue("ProductCreatedEvent")
@Entity
class ProductCreatedEvent(
    @Column(name = "productId", nullable = false)
    val productId: String,

    @Column(name = "name", nullable = false)
    val name: String,

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    val type: com.example.cqrs.infrastructure.persistence.command.entity.ProductType,

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
) : ProductEvent(
    id = null,
    eventDate = eventDate,
    metadata = metadata
) {
    companion object {
        fun of(
            productId: String,
            name: String,
            type: com.example.cqrs.infrastructure.persistence.command.entity.ProductType,
            description: String?,
            interestRate: Double,
            termInMonths: Int,
            minimumAmount: Double,
            eventDate: LocalDateTime,
            metadata: EventMetadata
        ): com.example.cqrs.infrastructure.eventstore.event.product.ProductCreatedEvent {
            return com.example.cqrs.infrastructure.eventstore.event.product.ProductCreatedEvent(
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