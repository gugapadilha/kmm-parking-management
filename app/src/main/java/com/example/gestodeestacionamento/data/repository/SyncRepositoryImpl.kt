package com.example.gestodeestacionamento.data.repository

import android.util.Log
import com.example.gestodeestacionamento.data.mapper.toDomain
import com.example.gestodeestacionamento.data.remote.ApiService
import com.example.gestodeestacionamento.domain.model.PaymentMethod
import com.example.gestodeestacionamento.domain.model.PriceTable
import com.example.gestodeestacionamento.domain.repository.ManualLoadResult
import com.example.gestodeestacionamento.domain.repository.SyncRepository

class SyncRepositoryImpl(
    private val apiService: ApiService
) : SyncRepository {

    override suspend fun manualLoad(
        userId: Long,
        establishmentId: Long,
        token: String
    ): Result<ManualLoadResult> {
        return try {
            Log.d("SyncRepository", "Calling manualLoad: userId=$userId, establishmentId=$establishmentId")
            val responseResult = apiService.manualLoad(userId, establishmentId, token)
            responseResult.fold(
                onSuccess = { response ->
                    val prices = response.data?.prices
                    val paymentMethods = response.data?.paymentMethods
                    val sessionId = response.data?.sessionId
                    
                    Log.d("SyncRepository", "Manual load success: prices=${prices?.size ?: "null"}, paymentMethods=${paymentMethods?.size ?: "null"}")
                    if (prices == null || prices.isEmpty()) {
                        Log.w("SyncRepository", "WARNING: API returned null or empty prices list!")
                    } else {
                        prices.forEach { priceDto ->
                            Log.d("SyncRepository", "PriceDto from API: id=${priceDto.id}, name=${priceDto.name}")
                        }
                    }
                    val priceTables = prices?.map { it.toDomain() } ?: emptyList()
                    val paymentMethodsMapped = paymentMethods?.map { it.toDomain() } ?: emptyList()
                    Log.d("SyncRepository", "Mapped: priceTables=${priceTables.size}, paymentMethods=${paymentMethodsMapped.size}")
                    if (priceTables.isEmpty()) {
                        Log.w("SyncRepository", "WARNING: Mapped priceTables list is empty!")
                    } else {
                        priceTables.forEach { table ->
                            Log.d("SyncRepository", "PriceTable domain: id=${table.id}, name=${table.name}")
                        }
                    }
                    Result.success(
                        ManualLoadResult(
                            priceTables = priceTables,
                            paymentMethods = paymentMethodsMapped,
                            sessionId = sessionId
                        )
                    )
                },
                onFailure = { exception ->
                    Log.e("SyncRepository", "Manual load failed", exception)
                    Result.failure(exception)
                }
            )
        } catch (e: Exception) {
            Log.e("SyncRepository", "Manual load exception", e)
            Result.failure(e)
        }
    }

    override suspend fun closeSession(
        userId: Long,
        establishmentId: Long,
        sessionId: Long,
        token: String
    ): Result<Unit> {
        return try {
            val responseResult = apiService.closeSession(userId, establishmentId, sessionId, token)
            responseResult.fold(
                onSuccess = {
                    Result.success(Unit)
                },
                onFailure = { exception ->
                    Result.failure(exception)
                }
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

