package com.yalla.meshwar

object PriceCalculator {
    fun calculateEstimatedFare(distanceInKm: Double, vehicleType: String): Double {
        val baseFare = when (vehicleType) {
            "TUKTUK" -> 10.0
            "SCOOTER" -> 15.0
            "MALAKI" -> 30.0
            else -> 20.0
        }
        val ratePerKm = when (vehicleType) {
            "TUKTUK" -> 5.0
            "SCOOTER" -> 6.0
            "MALAKI" -> 10.0
            else -> 8.0
        }
        return baseFare + (distanceInKm * ratePerKm)
    }
}
