package com.example.gestodeestacionamento.data.mapper

import com.example.gestodeestacionamento.data.local.entity.PriceTableEntity
import com.example.gestodeestacionamento.data.remote.dto.PriceTableDto
import com.example.gestodeestacionamento.domain.model.PriceTable

fun PriceTableEntity.toDomain(): PriceTable {
    return PriceTable(
        id = id,
        name = name,
        initialTolerance = initialTolerance,
        untilTime = untilTime,
        untilValue = untilValue,
        fromTime = fromTime,
        everyInterval = everyInterval,
        addValue = addValue,
        maxChargePeriod = maxChargePeriod,
        maxChargeValue = maxChargeValue
    )
}

fun PriceTable.toEntity(): PriceTableEntity {
    return PriceTableEntity(
        id = id,
        name = name,
        initialTolerance = initialTolerance,
        untilTime = untilTime,
        untilValue = untilValue,
        fromTime = fromTime,
        everyInterval = everyInterval,
        addValue = addValue,
        maxChargePeriod = maxChargePeriod,
        maxChargeValue = maxChargeValue
    )
}

fun PriceTableDto.toEntity(): PriceTableEntity {
    // Processar os itens para extrair as regras de preço
    val items = this.items ?: emptyList()
    
    android.util.Log.d("PriceTableMapper", "Processing ${items.size} items for table: ${normalizedName}")
    items.forEachIndexed { index, item ->
        android.util.Log.d("PriceTableMapper", "  Item[$index]: period=${item.normalizedPeriod}, price=${item.normalizedPrice}, since=${item.normalizedSince}")
    }
    
    // Encontrar o maior período "até" (since = 0) - este será o valor fixo até um período
    val untilItems = items.filter { it.normalizedSince == 0 }
    val untilItem = untilItems.maxByOrNull { it.normalizedPeriod }
    
    android.util.Log.d("PriceTableMapper", "Until item: period=${untilItem?.normalizedPeriod}, price=${untilItem?.normalizedPrice}")
    
    // Encontrar o primeiro item incremental (since > 0) - este será o valor incremental
    val incrementalItems = items.filter { it.normalizedSince > 0 }
    val incrementalItem = incrementalItems.minByOrNull { it.normalizedSince }
    
    android.util.Log.d("PriceTableMapper", "Incremental item: since=${incrementalItem?.normalizedSince}, period=${incrementalItem?.normalizedPeriod}, price=${incrementalItem?.normalizedPrice}")
    
    val untilTime = untilItem?.let { formatMinutesToTime(it.normalizedPeriod) }
    val untilValue = untilItem?.normalizedPrice
    
    val fromTime = incrementalItem?.let { formatMinutesToTime(it.normalizedSince) }
    val everyInterval = incrementalItem?.let { formatMinutesToTime(it.normalizedPeriod) }
    val addValue = incrementalItem?.normalizedPrice
    
    android.util.Log.d("PriceTableMapper", "Mapped: untilTime=$untilTime, untilValue=$untilValue, fromTime=$fromTime, everyInterval=$everyInterval, addValue=$addValue")
    
    return PriceTableEntity(
        id = normalizedId,
        name = normalizedName,
        initialTolerance = normalizedInitialTolerance,
        untilTime = untilTime,
        untilValue = untilValue,
        fromTime = fromTime,
        everyInterval = everyInterval,
        addValue = addValue,
        maxChargePeriod = normalizedMaxChargePeriod,
        maxChargeValue = normalizedMaxChargeValue
    )
}

fun PriceTableDto.toDomain(): PriceTable {
    // Processar os itens para extrair as regras de preço
    val items = this.items ?: emptyList()
    
    android.util.Log.d("PriceTableMapper", "Processing ${items.size} items for table: ${normalizedName}")
    items.forEachIndexed { index, item ->
        android.util.Log.d("PriceTableMapper", "  Item[$index]: period=${item.normalizedPeriod}, price=${item.normalizedPrice}, since=${item.normalizedSince}")
    }
    
    // Encontrar o maior período "até" (since = 0)
    val untilItems = items.filter { it.normalizedSince == 0 }
    val untilItem = untilItems.maxByOrNull { it.normalizedPeriod }
    
    // Encontrar o primeiro item incremental (since > 0)
    val incrementalItems = items.filter { it.normalizedSince > 0 }
    val incrementalItem = incrementalItems.minByOrNull { it.normalizedSince }
    
    val untilTime = untilItem?.let { formatMinutesToTime(it.normalizedPeriod) }
    val untilValue = untilItem?.normalizedPrice
    
    val fromTime = incrementalItem?.let { formatMinutesToTime(it.normalizedSince) }
    val everyInterval = incrementalItem?.let { formatMinutesToTime(it.normalizedPeriod) }
    val addValue = incrementalItem?.normalizedPrice
    
    android.util.Log.d("PriceTableMapper", "Mapped to domain: untilTime=$untilTime, untilValue=$untilValue, fromTime=$fromTime, everyInterval=$everyInterval, addValue=$addValue")
    
    return PriceTable(
        id = normalizedId,
        name = normalizedName,
        initialTolerance = normalizedInitialTolerance,
        untilTime = untilTime,
        untilValue = untilValue,
        fromTime = fromTime,
        everyInterval = everyInterval,
        addValue = addValue,
        maxChargePeriod = normalizedMaxChargePeriod,
        maxChargeValue = normalizedMaxChargeValue
    )
}

private fun formatMinutesToTime(minutes: Int): String {
    val hours = minutes / 60
    val mins = minutes % 60
    return String.format("%02d:%02d", hours, mins)
}
