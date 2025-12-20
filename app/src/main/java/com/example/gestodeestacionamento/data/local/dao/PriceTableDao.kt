package com.example.gestodeestacionamento.data.local.dao

import androidx.room.*
import com.example.gestodeestacionamento.data.local.entity.PriceTableEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PriceTableDao {
    @Query("SELECT * FROM price_tables")
    fun getAllPriceTables(): Flow<List<PriceTableEntity>>
    
    @Query("SELECT * FROM price_tables WHERE id = :id")
    suspend fun getPriceTableById(id: Long): PriceTableEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPriceTable(priceTable: PriceTableEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllPriceTables(priceTables: List<PriceTableEntity>)
    
    @Query("DELETE FROM price_tables")
    suspend fun deleteAllPriceTables()
}

