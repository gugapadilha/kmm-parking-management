package com.example.gestodeestacionamento.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vehicles")
data class VehicleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val plate: String,
    val model: String,
    val color: String,
    val priceTableId: Long,
    val entryDateTime: Long, // timestamp
    val exitDateTime: Long? = null, // timestamp, null se ainda está no pátio
    val totalAmount: Double? = null,
    val paymentMethodId: Long? = null,
    val isInParkingLot: Boolean = true
)

