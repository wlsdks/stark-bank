package com.example.cqrs.command.entity

import com.example.cqrs.command.entity.event.AbstractFinancialEventEntity
import com.example.cqrs.command.entity.event.metadata.EventMetadata
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@DiscriminatorValue("ProductCreatedEvent")
class ProductCreatedEventEntity(
    @Column(nullable = false)
    val productId: String,

    @Column(nullable = false)
    val name: String,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val type: ProductType,

    @Column
    val description: String?,

    @Column(nullable = false)
    val interestRate: Double,

    @Column(nullable = false)
    val termInMonths: Int,

    @Column(nullable = false)
    val minimumAmount: Double,

    eventDate: LocalDateTime,
    metadata: EventMetadata
) : AbstractFinancialEventEntity(
    id = null,
    eventDate = eventDate,
    metadata = metadata
) {
    companion object {
        // factory method
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