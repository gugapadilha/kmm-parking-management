package com.example.gestodeestacionamento.platform

import com.example.gestodeestacionamento.data.local.dao.*

expect class DatabaseFactory {
    fun createDatabase(): AppDatabase
}

expect interface AppDatabase {
    fun vehicleDao(): VehicleDao
    fun priceTableDao(): PriceTableDao
    fun paymentMethodDao(): PaymentMethodDao
    fun paymentDao(): PaymentDao
}

