package com.example.gestodeestacionamento.data.repository

import com.example.gestodeestacionamento.data.local.AppDatabase
import com.example.gestodeestacionamento.data.local.entity.PaymentEntity
import com.example.gestodeestacionamento.data.mapper.toDomain
import com.example.gestodeestacionamento.data.mapper.toEntity
import com.example.gestodeestacionamento.domain.model.PaymentMethod
import com.example.gestodeestacionamento.domain.model.PaymentMethodTotal
import com.example.gestodeestacionamento.domain.repository.PaymentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class PaymentRepositoryImpl(
    private val database: AppDatabase
) : PaymentRepository {

    override fun getAllPaymentMethods(): Flow<List<PaymentMethod>> {
        return database.paymentMethodDao().getAllPaymentMethods().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getPaymentMethodById(id: Long): PaymentMethod? {
        return database.paymentMethodDao().getPaymentMethodById(id)?.toDomain()
    }

    override suspend fun insertAllPaymentMethods(paymentMethods: List<PaymentMethod>) {
        database.paymentMethodDao().insertAllPaymentMethods(paymentMethods.map { it.toEntity() })
    }

    override suspend fun insertPayment(vehicleId: Long, paymentMethodId: Long, amount: Double) {
        val payment = PaymentEntity(
            vehicleId = vehicleId,
            paymentMethodId = paymentMethodId,
            amount = amount,
            dateTime = System.currentTimeMillis()
        )
        database.paymentDao().insertPayment(payment)
    }

    override fun getPaymentsGroupedByMethod(): Flow<List<PaymentMethodTotal>> {
        return combine(
            database.paymentDao().getAllPayments(),
            database.paymentMethodDao().getAllPaymentMethods()
        ) { payments, methods ->
            val methodMap = methods.associateBy { it.id }
            payments
                .groupBy { it.paymentMethodId }
                .mapNotNull { (methodId, paymentList) ->
                    val method = methodMap[methodId] ?: return@mapNotNull null
                    PaymentMethodTotal(
                        paymentMethodId = methodId,
                        paymentMethodName = method.name,
                        total = paymentList.sumOf { it.amount }
                    )
                }
        }
    }

    override fun getTotalPayments(): Flow<Double> {
        return database.paymentDao().getTotalPayments().map { it ?: 0.0 }
    }

    override suspend fun deleteAllPayments() {
        database.paymentDao().deleteAllPayments()
    }

    override suspend fun deleteAllPaymentMethods() {
        database.paymentMethodDao().deleteAllPaymentMethods()
    }
}

