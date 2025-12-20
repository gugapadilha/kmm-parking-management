package com.example.gestodeestacionamento.domain.repository

import com.example.gestodeestacionamento.domain.model.PaymentMethod
import com.example.gestodeestacionamento.domain.model.PaymentMethodTotal
import kotlinx.coroutines.flow.Flow

interface PaymentRepository {
    fun getAllPaymentMethods(): Flow<List<PaymentMethod>>
    suspend fun getPaymentMethodById(id: Long): PaymentMethod?
    suspend fun insertAllPaymentMethods(paymentMethods: List<PaymentMethod>)
    suspend fun insertPayment(vehicleId: Long, paymentMethodId: Long, amount: Double)
    fun getPaymentsGroupedByMethod(): Flow<List<PaymentMethodTotal>>
    fun getTotalPayments(): Flow<Double>
    suspend fun deleteAllPayments()
    suspend fun deleteAllPaymentMethods()
}

