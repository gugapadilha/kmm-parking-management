package com.example.gestodeestacionamento.domain.usecase

import android.util.Log
import com.example.gestodeestacionamento.domain.model.PriceTable

class CalculateParkingFeeUseCase {
    
    fun execute(
        priceTable: PriceTable,
        entryDateTime: Long,
        exitDateTime: Long
    ): Double {
        val entryTime = entryDateTime
        val exitTime = exitDateTime
        
        Log.d("CalculateParkingFee", "Calculating fee for table: ${priceTable.name}, id: ${priceTable.id}")
        Log.d("CalculateParkingFee", "Entry: $entryTime, Exit: $exitTime")
        Log.d("CalculateParkingFee", "Table config: tolerance=${priceTable.initialTolerance}, untilTime=${priceTable.untilTime}, untilValue=${priceTable.untilValue}, fromTime=${priceTable.fromTime}, everyInterval=${priceTable.everyInterval}, addValue=${priceTable.addValue}")
        
        // Aplicar tolerância inicial
        val toleranceMinutes = parseTimeToMinutes(priceTable.initialTolerance)
        val entryTimeWithTolerance = entryTime + (toleranceMinutes * 60 * 1000)
        
        Log.d("CalculateParkingFee", "Tolerance minutes: $toleranceMinutes, entryTimeWithTolerance: $entryTimeWithTolerance")
        
        // Se saiu antes da tolerância, não cobra nada
        if (exitTime <= entryTimeWithTolerance) {
            Log.d("CalculateParkingFee", "Exit before tolerance, returning 0.0")
            return 0.0
        }
        
        // Calcular tempo de estadia em minutos
        val stayDurationMinutes = ((exitTime - entryTimeWithTolerance) / (60 * 1000)).toInt()
        Log.d("CalculateParkingFee", "Stay duration minutes: $stayDurationMinutes")
        
        var totalAmount = 0.0
        
        // Aplicar regra "Até"
        if (priceTable.untilTime != null && priceTable.untilValue != null) {
            val untilMinutes = parseTimeToMinutes(priceTable.untilTime)
            Log.d("CalculateParkingFee", "Until rule: $untilMinutes minutes = ${priceTable.untilValue}")
            
            if (stayDurationMinutes <= untilMinutes) {
                totalAmount = priceTable.untilValue
                Log.d("CalculateParkingFee", "Within 'until' period, amount: $totalAmount")
            } else {
                // Aplicar regra "A partir de"
                if (priceTable.fromTime != null && priceTable.everyInterval != null && priceTable.addValue != null) {
                    val fromMinutes = parseTimeToMinutes(priceTable.fromTime)
                    val everyMinutes = parseTimeToMinutes(priceTable.everyInterval)
                    
                    Log.d("CalculateParkingFee", "From rule: from=$fromMinutes minutes, every=$everyMinutes minutes, add=${priceTable.addValue}")
                    
                    // Valor até o período "até"
                    totalAmount = priceTable.untilValue
                    
                    // Calcular períodos adicionais após "até"
                    val additionalMinutes = stayDurationMinutes - untilMinutes
                    if (additionalMinutes > 0) {
                        val additionalPeriods = (additionalMinutes / everyMinutes) + if (additionalMinutes % everyMinutes > 0) 1 else 0
                        val additionalAmount = additionalPeriods * priceTable.addValue
                        totalAmount += additionalAmount
                        Log.d("CalculateParkingFee", "Additional minutes: $additionalMinutes, periods: $additionalPeriods, additional amount: $additionalAmount")
                    }
                } else {
                    // Se não tem regra "A partir de", cobra apenas o valor "até"
                    totalAmount = priceTable.untilValue
                    Log.d("CalculateParkingFee", "No 'from' rule, using 'until' value: $totalAmount")
                }
            }
        } else {
            // Se não tem regra "Até", aplicar apenas "A partir de"
            if (priceTable.fromTime != null && priceTable.everyInterval != null && priceTable.addValue != null) {
                val fromMinutes = parseTimeToMinutes(priceTable.fromTime)
                val everyMinutes = parseTimeToMinutes(priceTable.everyInterval)
                
                Log.d("CalculateParkingFee", "Only 'from' rule: from=$fromMinutes minutes, every=$everyMinutes minutes, add=${priceTable.addValue}")
                
                val periods = (stayDurationMinutes / everyMinutes) + if (stayDurationMinutes % everyMinutes > 0) 1 else 0
                totalAmount = periods * priceTable.addValue
                Log.d("CalculateParkingFee", "Periods: $periods, total amount: $totalAmount")
            } else {
                Log.w("CalculateParkingFee", "No pricing rules found! untilTime=${priceTable.untilTime}, untilValue=${priceTable.untilValue}, fromTime=${priceTable.fromTime}, everyInterval=${priceTable.everyInterval}, addValue=${priceTable.addValue}")
            }
        }
        
        // Aplicar valor máximo
        if (priceTable.maxChargePeriod != null && priceTable.maxChargeValue != null) {
            val maxPeriodMinutes = parseTimeToMinutes(priceTable.maxChargePeriod)
            if (stayDurationMinutes <= maxPeriodMinutes) {
                totalAmount = minOf(totalAmount, priceTable.maxChargeValue)
                Log.d("CalculateParkingFee", "Applied max charge: $totalAmount (max: ${priceTable.maxChargeValue})")
            }
        }
        
        Log.d("CalculateParkingFee", "Final calculated amount: $totalAmount")
        return totalAmount
    }
    
    private fun parseTimeToMinutes(timeString: String): Int {
        if (timeString.isBlank()) return 0
        val parts = timeString.split(":")
        if (parts.size != 2) {
            Log.w("CalculateParkingFee", "Invalid time format: $timeString")
            return 0
        }
        val hours = parts[0].toIntOrNull() ?: 0
        val minutes = parts[1].toIntOrNull() ?: 0
        return (hours * 60) + minutes
    }
}

