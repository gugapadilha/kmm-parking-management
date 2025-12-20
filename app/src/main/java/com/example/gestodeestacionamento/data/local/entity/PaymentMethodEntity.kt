package com.example.gestodeestacionamento.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "payment_methods")
data class PaymentMethodEntity(
    @PrimaryKey
    val id: Long,
    val name: String,
    val description: String? = null
)

