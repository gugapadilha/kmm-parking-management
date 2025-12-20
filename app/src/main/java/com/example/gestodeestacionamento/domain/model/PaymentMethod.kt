package com.example.gestodeestacionamento.domain.model

data class PaymentMethod(
    val id: Long,
    val name: String,
    val description: String? = null
)

