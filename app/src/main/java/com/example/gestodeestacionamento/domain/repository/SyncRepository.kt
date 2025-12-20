package com.example.gestodeestacionamento.domain.repository

import com.example.gestodeestacionamento.domain.model.PriceTable
import com.example.gestodeestacionamento.domain.model.PaymentMethod

data class ManualLoadResult(
    val priceTables: List<PriceTable>,
    val paymentMethods: List<PaymentMethod>,
    val sessionId: Long?
)

interface SyncRepository {
    suspend fun manualLoad(userId: Long, establishmentId: Long, token: String): Result<ManualLoadResult>
    suspend fun closeSession(userId: Long, establishmentId: Long, sessionId: Long, token: String): Result<Unit>
}

