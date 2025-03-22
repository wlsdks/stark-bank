package com.example.cqrs.infrastructure.eventstore.entity.event.product

import com.example.cqrs.infrastructure.eventstore.entity.base.ProductEvent
import com.example.cqrs.infrastructure.eventstore.entity.base.metadata.EventMetadata
import jakarta.persistence.Column
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import java.time.LocalDateTime

/**
 * 금융 상품 비활성화 이벤트
 */
@Entity
@DiscriminatorValue("ProductDeactivatedEvent")
class ProductDeactivatedEvent(
    @Column(nullable = false)
    val productId: String,

    @Column(nullable = true)
    val reason: String?,

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
            reason: String?,
            eventDate: LocalDateTime,
            metadata: EventMetadata
        ): ProductDeactivatedEvent {
            return ProductDeactivatedEvent(
                productId = productId,
                reason = reason,
                eventDate = eventDate,
                metadata = metadata
            )
        }
    }
}