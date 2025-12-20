package com.example.gestodeestacionamento.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.gestodeestacionamento.data.local.dao.*
import com.example.gestodeestacionamento.data.local.entity.*

@Database(
    entities = [
        VehicleEntity::class,
        PriceTableEntity::class,
        PaymentMethodEntity::class,
        PaymentEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun vehicleDao(): VehicleDao
    abstract fun priceTableDao(): PriceTableDao
    abstract fun paymentMethodDao(): PaymentMethodDao
    abstract fun paymentDao(): PaymentDao
}

