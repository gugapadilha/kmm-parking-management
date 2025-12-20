package com.example.gestodeestacionamento.domain.model

data class PaymentMethodTotal(
    val paymentMethodId: Long,
    val paymentMethodName: String,
    val total: Double
)

