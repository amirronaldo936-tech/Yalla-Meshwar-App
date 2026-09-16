package com.example.models

// نموذج بيانات العميل والكابتن
data class User(
    val uid: String = "",
    val phone: String = "",
    val name: String = "",
    val role: String = "USER", // "USER", "CAPTAIN", "ADMIN", "GUEST"
    val gender: String = "MALE", // "MALE", "FEMALE"
    val isVerified: Boolean = false, // اشتراك "يلا بشهر"
    val completedRidesWeek: Int = 0,
    val blockCount: Int = 0,
    val isBanned: Boolean = false,
    val banUntilTimestamp: Long = 0L,
    
    // بيانات الكابتن الخاصة
    val vehicleType: String = "", // "TUKTUK", "SCOOTER", "MALAKI"
    val vehicleModel: String = "",
    val vehiclePlateNumber: String = "",
    val rating: Double = 4.9,
    val idCardFrontUrl: String = "",
    val idCardBackUrl: String = "",
    val licenseUrl: String = "",
    val carPhotoUrl: String = "",
    val isApprovedByAdmin: Boolean = false
)

// نموذج طلب الرحلة والتفاوض
data class RideRequest(
    val requestId: String = "",
    val userId: String = "",
    val userName: String = "العميل",
    val userPhone: String = "",
    val serviceType: String = "مشوار", // "مشوار", "طلبات", "أفراح", "سفر", "مطار"
    val vehicleCategory: String = "ملاكي", // "توكتوك", "اسكوتر", "ملاكي"
    val pickupName: String = "المحلة الكبرى - شارع البحر",
    val destinationName: String = "طنطا - ميدان المحطة",
    val pickupLat: Double = 30.9706,
    val pickupLng: Double = 31.1669,
    val destinationLat: Double = 30.7865,
    val destinationLng: Double = 31.0004,
    val initialPriceOffer: Double = 50.0,
    val negotiatedPrice: Double = 50.0,
    val captainOfferPrice: Double = 0.0,
    val paymentMethod: String = "CASH", // "CASH" أو "VODAFONE_CASH"
    val status: String = "PENDING", // "PENDING", "NEGOTIATING", "ACCEPTED", "WAITING_WEDDING", "COMPLETED", "CANCELLED"
    val assignedCaptainId: String = "",
    val assignedCaptainName: String = "",
    val assignedCaptainPhone: String = "",
    val assignedCaptainRating: Double = 4.9,
    val assignedVehiclePlate: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    // حقول الحجز المسبق في البيانات
    val isScheduled: Boolean = false,
    val scheduledTimestamp: Long = 0L // تاريخ ووقت الانطلاق المحدد
)

// نموذج الشات الحي
data class ChatMessage(
    val messageId: String = "",
    val requestId: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val senderRole: String = "USER",
    val messageText: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

// المواقع الشائعة للمدن المصرية
data class LocationPreset(
    val id: String,
    val city: String,
    val name: String,
    val description: String,
    val lat: Double,
    val lng: Double
)

// نموذج بيانات الكابتن المحدث
data class CaptainStats(
    val totalRidesCount: Int = 0,       // إجمالي عدد الرحلات
    val totalEarnings: Double = 0.0,    // إجمالي المبالغ والفلوس
    val weeklyRidesCount: Int = 0      // عدد رحلات الأسبوع (لمكافأة الـ 100 رحلة)
)
