package com.example.gestodeestacionamento.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ManualLoadResponse(
    val response: String,
    val data: ManualLoadData? = null
)

@Serializable
data class ManualLoadData(
    val prices: List<PriceTableDto>? = null,
    val paymentMethods: List<PaymentMethodDto>? = null,
    val sessionId: Long? = null
)

@Serializable
data class PriceTableDto(
    @kotlinx.serialization.SerialName("establishmentId") val establishmentId: Long? = null,
    @kotlinx.serialization.SerialName("typePrice") val typePrice: String? = null,
    @kotlinx.serialization.SerialName("tolerance") val tolerance: Int? = null, // em minutos
    @kotlinx.serialization.SerialName("maximumPeriod") val maximumPeriod: Int? = null, // em minutos
    @kotlinx.serialization.SerialName("maximumValue") val maximumValue: String? = null,
    @kotlinx.serialization.SerialName("items") val items: List<PriceTableItemDto>? = null,
    // Campos legados para compatibilidade
    @kotlinx.serialization.SerialName("id") val id: Long? = null,
    @kotlinx.serialization.SerialName("priceTableId") val priceTableId: Long? = null,
    @kotlinx.serialization.SerialName("name") val name: String? = null,
    @kotlinx.serialization.SerialName("priceTableName") val priceTableName: String? = null
) {
    // Propriedades computadas para normalizar os campos
    val normalizedId: Long
        get() {
            // Gerar um ID único baseado em establishmentId + typePrice
            if (establishmentId != null && typePrice != null) {
                return (establishmentId.toString() + typePrice).hashCode().toLong()
            }
            return id ?: priceTableId ?: 0L
        }
    
    val normalizedName: String
        get() = typePrice ?: name ?: priceTableName ?: ""
    
    val normalizedInitialTolerance: String
        get() = tolerance?.let { formatMinutesToTime(it) } ?: "00:00"
    
    val normalizedMaxChargePeriod: String?
        get() = maximumPeriod?.let { formatMinutesToTime(it) }
    
    val normalizedMaxChargeValue: Double?
        get() = maximumValue?.toDoubleOrNull()
    
    private fun formatMinutesToTime(minutes: Int): String {
        val hours = minutes / 60
        val mins = minutes % 60
        return String.format("%02d:%02d", hours, mins)
    }
}

@Serializable
data class PriceTableItemDto(
    @kotlinx.serialization.SerialName("itemId") val itemId: Long? = null,
    @kotlinx.serialization.SerialName("price") val price: String? = null,
    @kotlinx.serialization.SerialName("period") val period: Int? = null, // em minutos
    @kotlinx.serialization.SerialName("since") val since: Int? = null // em minutos - quando começa a aplicar
) {
    val normalizedPrice: Double
        get() = price?.toDoubleOrNull() ?: 0.0
    
    val normalizedPeriod: Int
        get() = period ?: 0
    
    val normalizedSince: Int
        get() = since ?: 0
}

@Serializable
data class PaymentMethodDto(
    val establishmentPaymentMethodId: Long,
    val paymentMethodName: String,
    val primitivePaymentMethodId: Long,
    val receivingDays: Int = 0,
    val receivingFee: String = "0.00",
    val accountId: Long
)

