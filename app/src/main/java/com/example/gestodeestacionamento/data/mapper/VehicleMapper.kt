package com.example.gestodeestacionamento.data.mapper

import com.example.gestodeestacionamento.data.local.entity.VehicleEntity
import com.example.gestodeestacionamento.domain.model.Vehicle

fun VehicleEntity.toDomain(): Vehicle {
    return Vehicle(
        id = id,
        plate = plate,
        model = model,
        color = color,
        priceTableId = priceTableId,
        entryDateTime = entryDateTime,
        exitDateTime = exitDateTime,
        totalAmount = totalAmount,
        paymentMethodId = paymentMethodId,
        isInParkingLot = isInParkingLot
    )
}

fun Vehicle.toEntity(): VehicleEntity {
    return VehicleEntity(
        id = id,
        plate = plate,
        model = model,
        color = color,
        priceTableId = priceTableId,
        entryDateTime = entryDateTime,
        exitDateTime = exitDateTime,
        totalAmount = totalAmount,
        paymentMethodId = paymentMethodId,
        isInParkingLot = isInParkingLot
    )
}

