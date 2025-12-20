package com.example.gestodeestacionamento.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "price_tables")
data class PriceTableEntity(
    @PrimaryKey
    val id: Long,
    val name: String,
    val initialTolerance: String, // formato HH:mm
    val untilTime: String? = null, // formato HH:mm
    val untilValue: Double? = null,
    val fromTime: String? = null, // formato HH:mm
    val everyInterval: String? = null, // formato HH:mm
    val addValue: Double? = null,
    val maxChargePeriod: String? = null, // formato HH:mm
    val maxChargeValue: Double? = null
)

