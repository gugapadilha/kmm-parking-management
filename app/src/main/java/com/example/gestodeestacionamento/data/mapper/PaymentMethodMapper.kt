package com.example.gestodeestacionamento.data.mapper

import com.example.gestodeestacionamento.data.local.entity.PaymentMethodEntity
import com.example.gestodeestacionamento.data.remote.dto.PaymentMethodDto
import com.example.gestodeestacionamento.domain.model.PaymentMethod

fun PaymentMethodEntity.toDomain(): PaymentMethod {
    return PaymentMethod(
        id = id,
        name = name,
        description = description
    )
}

fun PaymentMethod.toEntity(): PaymentMethodEntity {
    return PaymentMethodEntity(
        id = id,
        name = name,
        description = description
    )
}

fun PaymentMethodDto.toEntity(): PaymentMethodEntity {
    return PaymentMethodEntity(
        id = establishmentPaymentMethodId,
        name = paymentMethodName,
        description = null
    )
}

fun PaymentMethodDto.toDomain(): PaymentMethod {
    return PaymentMethod(
        id = establishmentPaymentMethodId,
        name = paymentMethodName,
        description = null
    )
}
