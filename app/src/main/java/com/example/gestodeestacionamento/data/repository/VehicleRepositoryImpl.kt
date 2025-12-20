package com.example.gestodeestacionamento.data.repository

import com.example.gestodeestacionamento.data.local.AppDatabase
import com.example.gestodeestacionamento.data.local.dao.PriceTableDao
import com.example.gestodeestacionamento.data.mapper.toDomain
import com.example.gestodeestacionamento.data.mapper.toEntity
import com.example.gestodeestacionamento.domain.model.Vehicle
import com.example.gestodeestacionamento.domain.repository.VehicleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class VehicleRepositoryImpl(
    private val database: AppDatabase,
    private val priceTableDao: PriceTableDao
) : VehicleRepository {

    override fun getVehiclesInParkingLot(): Flow<List<Vehicle>> {
        return database.vehicleDao().getAllVehiclesInParkingLot().map { entities ->
            entities.map { entity ->
                val priceTable = priceTableDao.getPriceTableById(entity.priceTableId)
                entity.toDomain().copy(priceTableName = priceTable?.name)
            }
        }
    }

    override suspend fun getVehicleById(id: Long): Vehicle? {
        val entity = database.vehicleDao().getVehicleById(id) ?: return null
        val priceTable = priceTableDao.getPriceTableById(entity.priceTableId)
        return entity.toDomain().copy(priceTableName = priceTable?.name)
    }

    override fun getVehiclesInParkingLotCount(): Flow<Int> {
        return database.vehicleDao().getVehiclesInParkingLotCount()
    }

    override suspend fun insertVehicle(vehicle: Vehicle): Long {
        return database.vehicleDao().insertVehicle(vehicle.toEntity())
    }

    override suspend fun updateVehicle(vehicle: Vehicle) {
        database.vehicleDao().updateVehicle(vehicle.toEntity())
    }

    override suspend fun deleteAllVehicles() {
        database.vehicleDao().deleteAllVehicles()
    }
}

