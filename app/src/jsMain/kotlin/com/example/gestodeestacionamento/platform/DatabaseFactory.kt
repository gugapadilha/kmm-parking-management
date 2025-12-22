package com.example.gestodeestacionamento.platform

import com.example.gestodeestacionamento.data.local.dao.*

// Para Web, vamos usar uma implementação simples com IndexedDB ou LocalStorage
// Por enquanto, criamos uma implementação mock
actual class DatabaseFactory {
    actual fun createDatabase(): AppDatabase {
        return JsAppDatabase()
    }
}

actual interface AppDatabase {
    actual fun vehicleDao(): VehicleDao
    actual fun priceTableDao(): PriceTableDao
    actual fun paymentMethodDao(): PaymentMethodDao
    actual fun paymentDao(): PaymentDao
}

// Implementação simples para Web usando LocalStorage
class JsAppDatabase : AppDatabase {
    override fun vehicleDao(): VehicleDao = JsVehicleDao()
    override fun priceTableDao(): PriceTableDao = JsPriceTableDao()
    override fun paymentMethodDao(): PaymentMethodDao = JsPaymentMethodDao()
    override fun paymentDao(): PaymentDao = JsPaymentDao()
}

