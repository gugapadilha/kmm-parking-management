package com.example.gestodeestacionamento.data.repository

import com.example.gestodeestacionamento.data.local.AppDatabase
import com.example.gestodeestacionamento.data.mapper.toDomain
import com.example.gestodeestacionamento.data.mapper.toEntity
import com.example.gestodeestacionamento.domain.model.PriceTable
import com.example.gestodeestacionamento.domain.repository.PriceTableRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class PriceTableRepositoryImpl(
    private val database: AppDatabase
) : PriceTableRepository {

    override fun getAllPriceTables(): Flow<List<PriceTable>> {
        return database.priceTableDao().getAllPriceTables().map { entities ->
            android.util.Log.d("PriceTableRepository", "getAllPriceTables: found ${entities.size} entities in database")
            entities.forEach { entity ->
                android.util.Log.d("PriceTableRepository", "PriceTable entity: id=${entity.id}, name=${entity.name}")
            }
            val domain = entities.map { it.toDomain() }
            android.util.Log.d("PriceTableRepository", "Mapped to domain: ${domain.size} price tables")
            domain
        }
    }

    override suspend fun getPriceTableById(id: Long): PriceTable? {
        android.util.Log.d("PriceTableRepository", "getPriceTableById: searching for id=$id")
        val entity = database.priceTableDao().getPriceTableById(id)
        if (entity == null) {
            android.util.Log.w("PriceTableRepository", "PriceTable not found for id=$id")
            // Listar todas as tabelas disponíveis para debug
            val allEntities = database.priceTableDao().getAllPriceTables().first()
            android.util.Log.d("PriceTableRepository", "Available price tables in database: ${allEntities.size}")
            allEntities.forEach { e ->
                android.util.Log.d("PriceTableRepository", "Available: id=${e.id}, name=${e.name}")
            }
        } else {
            android.util.Log.d("PriceTableRepository", "PriceTable found: id=${entity.id}, name=${entity.name}, untilTime=${entity.untilTime}, untilValue=${entity.untilValue}, fromTime=${entity.fromTime}, everyInterval=${entity.everyInterval}, addValue=${entity.addValue}")
        }
        return entity?.toDomain()
    }

    override suspend fun insertAllPriceTables(priceTables: List<PriceTable>) {
        android.util.Log.d("PriceTableRepository", "insertAllPriceTables: received ${priceTables.size} price tables")
        if (priceTables.isEmpty()) {
            android.util.Log.w("PriceTableRepository", "WARNING: Trying to insert empty list of price tables!")
            return
        }
        priceTables.forEach { table ->
            android.util.Log.d("PriceTableRepository", "PriceTable to insert: id=${table.id}, name=${table.name}")
        }
        val entities = priceTables.map { it.toEntity() }
        android.util.Log.d("PriceTableRepository", "Mapped to entities: ${entities.size} entities")
        database.priceTableDao().insertAllPriceTables(entities)
        android.util.Log.d("PriceTableRepository", "insertAllPriceTables: successfully inserted ${entities.size} entities")
    }

    override suspend fun deleteAllPriceTables() {
        database.priceTableDao().deleteAllPriceTables()
    }
}

