package com.example.gestodeestacionamento.data.local.dao

import androidx.room.*
import com.example.gestodeestacionamento.data.local.entity.PaymentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PaymentDao {
    @Query("SELECT * FROM payments")
    fun getAllPayments(): Flow<List<PaymentEntity>>
    
    @Query("SELECT SUM(amount) FROM payments")
    fun getTotalPayments(): Flow<Double?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: PaymentEntity): Long
    
    @Query("DELETE FROM payments")
    suspend fun deleteAllPayments()
}


