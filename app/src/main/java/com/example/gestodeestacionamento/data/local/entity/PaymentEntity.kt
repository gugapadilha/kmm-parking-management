package com.example.gestodeestacionamento.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "payments")
data class PaymentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val vehicleId: Long,
    val paymentMethodId: Long,
    val amount: Double,
    val dateTime: Long // timestamp
)

