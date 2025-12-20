package com.example.gestodeestacionamento.domain.model

data class Vehicle(
    val id: Long = 0,
    val plate: String,
    val model: String,
    val color: String,
    val priceTableId: Long,
    val priceTableName: String? = null,
    val entryDateTime: Long,
    val exitDateTime: Long? = null,
    val totalAmount: Double? = null,
    val paymentMethodId: Long? = null,
    val paymentMethodName: String? = null,
    val isInParkingLot: Boolean = true
)

