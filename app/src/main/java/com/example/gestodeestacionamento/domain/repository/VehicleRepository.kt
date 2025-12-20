package com.example.gestodeestacionamento.domain.repository

import com.example.gestodeestacionamento.domain.model.Vehicle
import kotlinx.coroutines.flow.Flow

interface VehicleRepository {
    fun getVehiclesInParkingLot(): Flow<List<Vehicle>>
    suspend fun getVehicleById(id: Long): Vehicle?
    fun getVehiclesInParkingLotCount(): Flow<Int>
    suspend fun insertVehicle(vehicle: Vehicle): Long
    suspend fun updateVehicle(vehicle: Vehicle)
    suspend fun deleteAllVehicles()
}

