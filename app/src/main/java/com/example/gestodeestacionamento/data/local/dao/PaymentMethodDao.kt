package com.example.gestodeestacionamento.data.local.dao

import androidx.room.*
import com.example.gestodeestacionamento.data.local.entity.PaymentMethodEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PaymentMethodDao {
    @Query("SELECT * FROM payment_methods")
    fun getAllPaymentMethods(): Flow<List<PaymentMethodEntity>>
    
    @Query("SELECT * FROM payment_methods WHERE id = :id")
    suspend fun getPaymentMethodById(id: Long): PaymentMethodEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPaymentMethod(paymentMethod: PaymentMethodEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllPaymentMethods(paymentMethods: List<PaymentMethodEntity>)
    
    @Query("DELETE FROM payment_methods")
    suspend fun deleteAllPaymentMethods()
}

