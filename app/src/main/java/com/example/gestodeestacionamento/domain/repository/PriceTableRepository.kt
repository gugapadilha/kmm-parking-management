package com.example.gestodeestacionamento.domain.repository

import com.example.gestodeestacionamento.domain.model.PriceTable
import kotlinx.coroutines.flow.Flow

interface PriceTableRepository {
    fun getAllPriceTables(): Flow<List<PriceTable>>
    suspend fun getPriceTableById(id: Long): PriceTable?
    suspend fun insertAllPriceTables(priceTables: List<PriceTable>)
    suspend fun deleteAllPriceTables()
}

