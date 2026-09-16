package com.yalla.meshwar.utils

object FareCalculator {

    // نسبة عمولة التطبيق المحددة (12%)
    private const val APP_COMMISSION_RATE = 0.12

    /**
     * حساب السعر التنافسي للرحلة (أرخص سعر)
     * @param distanceInKm المسافة بالكيلومتر
     * @param vehicleType نوع المركبة (TUKTUK / SCOOTER / MALAKI)
     * @return السعر النهائي للزبون
     */
    fun calculateCheapestFare(distanceInKm: Double, vehicleType: String): Double {
        // 1. تحديد السعر الأساسي وسعر الكيلو التنافسي
        val (baseFare, ratePerKm) = when (vehicleType) {
            "TUKTUK" -> Pair(8.0, 3.5)   // فتح العداد + سعر الكيلو
            "SCOOTER" -> Pair(10.0, 4.0)
            "MALAKI" -> Pair(20.0, 7.0)
            else -> Pair(10.0, 5.0)
        }

        // 2. حساب تكلفة الرحلة المباشرة للكابتن
        val rawFare = baseFare + (distanceInKm * ratePerKm)

        // 3. إضافة عمولة التطبيق المخفضة (12% فقط)
        val finalFare = rawFare + (rawFare * APP_COMMISSION_RATE)

        // تقريب الناتج لأقرب جنيه لسهولة التعامل
        return Math.round(finalFare * 10.0) / 10.0
    }

    /**
     * حساب صافي ربح الكابتن وحصة التطبيق (12%)
     */
    fun getFareBreakdown(totalFare: Double): Pair<Double, Double> {
        val appShare = totalFare * APP_COMMISSION_RATE  // حصة التطبيق (12%)
        val captainShare = totalFare - appShare          // صافي ربح الكابتن (88%)
        return Pair(captainShare, appShare)
    }
}
