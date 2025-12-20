package com.example.gestodeestacionamento.data.local.dao

import androidx.room.*
import com.example.gestodeestacionamento.data.local.entity.VehicleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VehicleDao {
    @Query("SELECT * FROM vehicles WHERE isInParkingLot = 1")
    fun getAllVehiclesInParkingLot(): Flow<List<VehicleEntity>>
    
    @Query("SELECT * FROM vehicles WHERE id = :id")
    suspend fun getVehicleById(id: Long): VehicleEntity?
    
    @Query("SELECT COUNT(*) FROM vehicles WHERE isInParkingLot = 1")
    fun getVehiclesInParkingLotCount(): Flow<Int>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVehicle(vehicle: VehicleEntity): Long
    
    @Update
    suspend fun updateVehicle(vehicle: VehicleEntity)
    
    @Query("DELETE FROM vehicles")
    suspend fun deleteAllVehicles()
}

