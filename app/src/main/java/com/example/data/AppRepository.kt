package com.example.data

import com.example.models.ChatMessage
import com.example.models.LocationPreset
import com.example.models.RideRequest
import com.example.models.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RewardStatus(
    val hasReward: Boolean,
    val rewardMessage: String,
    val currentCount: Int,
    val targetCount: Int
)

object AppRepository {
    private val scope = CoroutineScope(Dispatchers.Default)

    // المستخدم الحالي
    private val _currentUser = MutableStateFlow(
        User(
            uid = "user_101",
            phone = "01012345678",
            name = "محمد علي",
            role = "USER",
            gender = "MALE",
            isVerified = true, // مشترك في باقة يلا بشهر
            completedRidesWeek = 58 // 58 رحلة (الرحلة القادمة ستكون 59 ومجانية!)
        )
    )
    val currentUser: StateFlow<User> = _currentUser.asStateFlow()

    // الطلب النشط حالياً
    private val _activeRide = MutableStateFlow<RideRequest?>(null)
    val activeRide: StateFlow<RideRequest?> = _activeRide.asStateFlow()

    // قائمة جميع الطلبات للكابتن والإدارة
    private val _rideRequests = MutableStateFlow<List<RideRequest>>(
        listOf(
            RideRequest(
                requestId = "REQ_001",
                userId = "user_guest_01",
                userName = "سارة أحمد",
                userPhone = "01122334455",
                serviceType = "مشوار",
                vehicleCategory = "توكتوك",
                pickupName = "حي أول المحلة - ميدان الشون",
                destinationName = "سوق السمك - شارع سعد محمد شكري",
                pickupLat = 30.9706,
                pickupLng = 31.1669,
                destinationLat = 30.9650,
                destinationLng = 31.1600,
                initialPriceOffer = 25.0,
                negotiatedPrice = 25.0,
                paymentMethod = "CASH",
                status = "PENDING"
            ),
            RideRequest(
                requestId = "REQ_002",
                userId = "user_102",
                userName = "كريم سامي",
                userPhone = "01233445566",
                serviceType = "أفراح",
                vehicleCategory = "ملاكي",
                pickupName = "طنطا - كفر العصام",
                destinationName = "قاعة الماسة - طريق المحلة السريع",
                pickupLat = 30.7865,
                pickupLng = 31.0004,
                destinationLat = 30.8500,
                destinationLng = 31.0800,
                initialPriceOffer = 350.0,
                negotiatedPrice = 350.0,
                paymentMethod = "VODAFONE_CASH",
                status = "PENDING"
            )
        )
    )
    val rideRequests: StateFlow<List<RideRequest>> = _rideRequests.asStateFlow()

    // قائمة الكباتن المسجلين
    private val _captains = MutableStateFlow<List<User>>(
        listOf(
            User(
                uid = "cap_001",
                name = "كابتن أحمد سامي",
                phone = "01099887766",
                role = "CAPTAIN",
                vehicleType = "TUKTUK",
                vehicleModel = "توكتوك بيادجو 2023",
                vehiclePlateNumber = "٤٨٢١ ط ع س",
                rating = 4.9,
                completedRidesWeek = 78,
                blockCount = 0,
                isApprovedByAdmin = true
            ),
            User(
                uid = "cap_002",
                name = "كابتن محمود الغريب",
                phone = "01288776655",
                role = "CAPTAIN",
                vehicleType = "SCOOTER",
                vehicleModel = "اسكوتر بينيلي كافينيرو",
                vehiclePlateNumber = "١٩٥٢ ج هـ د",
                rating = 4.8,
                completedRidesWeek = 95, // قريب جداً من الـ 100 رحلة
                blockCount = 1,
                isApprovedByAdmin = true
            ),
            User(
                uid = "cap_003",
                name = "كابتن تامر المصري",
                phone = "01155443322",
                role = "CAPTAIN",
                vehicleType = "MALAKI",
                vehicleModel = "نيسان صني 2022 (مكيفة)",
                vehiclePlateNumber = "٧٣١٤ س ب ر",
                rating = 4.95,
                completedRidesWeek = 42,
                blockCount = 0,
                isApprovedByAdmin = true
            ),
            User(
                uid = "cap_pending_01",
                name = "كابتن إبراهيم فودة",
                phone = "01544332211",
                role = "CAPTAIN",
                vehicleType = "MALAKI",
                vehicleModel = "هيونداي إلنترا 2021",
                vehiclePlateNumber = "٦٢٨٩ ق ر ت",
                rating = 5.0,
                idCardFrontUrl = "بطاقة_وجه.jpg",
                idCardBackUrl = "بطاقة_ظهر.jpg",
                licenseUrl = "رخصة_قيادة_مهنية.jpg",
                carPhotoUrl = "صورة_السيارة.jpg",
                isApprovedByAdmin = false
            ),
            User(
                uid = "cap_banned_01",
                name = "كابتن سعيد مرجان",
                phone = "01011223344",
                role = "CAPTAIN",
                vehicleType = "TUKTUK",
                vehicleModel = "توكتوك باجاج 2020",
                vehiclePlateNumber = "٣١٢٠ ن م ل",
                rating = 3.6,
                completedRidesWeek = 15,
                blockCount = 3,
                isBanned = true,
                banUntilTimestamp = System.currentTimeMillis() + (18 * 3600 * 1000), // محظور لمدة 18 ساعة باقية
                isApprovedByAdmin = true
            )
        )
    )
    val captains: StateFlow<List<User>> = _captains.asStateFlow()

    // المحادثات النشطة
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                messageId = "m1",
                senderId = "cap_001",
                senderName = "كابتن أحمد",
                senderRole = "CAPTAIN",
                messageText = "السلام عليكم، أنا في طريقي إليك يا فندم",
                timestamp = System.currentTimeMillis() - 120000
            ),
            ChatMessage(
                messageId = "m2",
                senderId = "user_101",
                senderName = "محمد علي",
                senderRole = "USER",
                messageText = "وعليكم السلام، أنا واقف أمام المحطة مباشرة بجوار الكشك",
                timestamp = System.currentTimeMillis() - 60000
            )
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    // قائمة الأماكن الشهيرة في الدلتا والقاهرة
    val locationPresets = listOf(
        LocationPreset("LOC_1", "المحلة الكبرى", "شارع البحر - ميدان الشون", "أشهر ميادين المحلة الكبرى", 30.9706, 31.1669),
        LocationPreset("LOC_2", "المحلة الكبرى", "نادي بلدية المحلة", "شارع شكري القوتلي", 30.9780, 31.1710),
        LocationPreset("LOC_3", "المحلة الكبرى", "محطة قطار المحلة", "حي ثان المحلة", 30.9630, 31.1610),
        LocationPreset("LOC_4", "طنطا", "ميدان السيد البدوي", "قلب مدينة طنطا التاريخي", 30.7870, 31.0015),
        LocationPreset("LOC_5", "طنطا", "جامعة طنطا - المجمع الطبي", "شارع الجيش، طنطا", 30.8010, 30.9950),
        LocationPreset("LOC_6", "طنطا", "محطة قطار طنطا الرئيسية", "ميدان المحطة", 30.7865, 31.0004),
        LocationPreset("LOC_7", "المنصورة", "شارع المشاية السفلية", "كورنيش النيل بالمنصورة", 31.0409, 31.3785),
        LocationPreset("LOC_8", "المنصورة", "جامعة المنصورة - بوابة توشكى", "حي الجامعة", 31.0430, 31.3570),
        LocationPreset("LOC_9", "القاهرة", "مطار القاهرة الدولي - صالة 3", "طريق المطار، مصر الجديدة", 30.1219, 31.4056),
        LocationPreset("LOC_10", "القاهرة", "ميدان التحرير", "وسط البلد، القاهرة", 30.0444, 31.2357)
    )

    // تسجيل الدخول بالهاتف
    fun loginWithPhone(phoneNumber: String, name: String, role: String = "USER") {
        _currentUser.value = User(
            uid = "user_" + System.currentTimeMillis(),
            phone = phoneNumber,
            name = if (name.isNotBlank()) name else "مستخدم جديد",
            role = role,
            gender = "MALE",
            isVerified = false,
            completedRidesWeek = 0
        )
    }

    // تسجيل الدخول كزائر
    fun loginAsGuest() {
        _currentUser.value = User(
            uid = "guest_" + (100..999).random(),
            name = "زائر",
            role = "GUEST"
        )
    }

    // التبديل بين الأدوار لتجربة التطبيق بسهولة
    fun switchRole(newRole: String) {
        val current = _currentUser.value
        _currentUser.value = when (newRole) {
            "USER" -> User(
                uid = "user_101",
                phone = "01012345678",
                name = "محمد علي",
                role = "USER",
                isVerified = true,
                completedRidesWeek = 58
            )
            "CAPTAIN" -> _captains.value.firstOrNull { it.role == "CAPTAIN" && it.isApprovedByAdmin && !it.isBanned }
                ?: User(
                    uid = "cap_001",
                    name = "كابتن أحمد سامي",
                    phone = "01099887766",
                    role = "CAPTAIN",
                    vehicleType = "TUKTUK",
                    vehicleModel = "توكتوك دايو",
                    vehiclePlateNumber = "٤٨٢١ ط ع س",
                    completedRidesWeek = 78,
                    isApprovedByAdmin = true
                )
            "ADMIN" -> User(
                uid = "admin_001",
                name = "مدير يلا مشوار",
                phone = "01000000000",
                role = "ADMIN"
            )
            "GUEST" -> User(
                uid = "guest_test",
                name = "زائر",
                role = "GUEST"
            )
            else -> current.copy(role = newRole)
        }
    }

    // إنشاء طلب رحلة وتفاوض
    fun createRideRequest(
        serviceType: String,
        vehicleCategory: String,
        pickupName: String,
        destinationName: String,
        pickupLat: Double,
        pickupLng: Double,
        destLat: Double,
        destLng: Double,
        priceOffer: Double,
        paymentMethod: String
    ): RideRequest {
        val reqId = "REQ_" + System.currentTimeMillis().toString().takeLast(5)
        val user = _currentUser.value

        val newRequest = RideRequest(
            requestId = reqId,
            userId = user.uid,
            userName = user.name,
            userPhone = user.phone,
            serviceType = serviceType,
            vehicleCategory = vehicleCategory,
            pickupName = pickupName,
            destinationName = destinationName,
            pickupLat = pickupLat,
            pickupLng = pickupLng,
            destinationLat = destLat,
            destinationLng = destLng,
            initialPriceOffer = priceOffer,
            negotiatedPrice = priceOffer,
            captainOfferPrice = 0.0,
            paymentMethod = paymentMethod,
            status = "PENDING"
        )

        _activeRide.value = newRequest
        _rideRequests.value = listOf(newRequest) + _rideRequests.value

        // محاكاة تلقي عرض من كابتن قريب بعد ثانيتين للتفاوض
        scope.launch {
            delay(2500)
            if (_activeRide.value?.requestId == reqId && _activeRide.value?.status == "PENDING") {
                val nearbyCaptain = _captains.value.firstOrNull { it.isApprovedByAdmin && !it.isBanned }
                val counterPrice = priceOffer + 10.0 // تفاوض +10 جنيه
                _activeRide.value = _activeRide.value?.copy(
                    status = "NEGOTIATING",
                    assignedCaptainId = nearbyCaptain?.uid ?: "cap_001",
                    assignedCaptainName = nearbyCaptain?.name ?: "كابتن أحمد سامي",
                    assignedCaptainPhone = nearbyCaptain?.phone ?: "01099887766",
                    assignedCaptainRating = nearbyCaptain?.rating ?: 4.9,
                    assignedVehiclePlate = nearbyCaptain?.vehiclePlateNumber ?: "٤٨٢١ ط ع س",
                    captainOfferPrice = counterPrice
                )
            }
        }

        return newRequest
    }

    // قبول عرض الكابتن بواسطة العميل
    fun acceptCaptainOffer() {
        val current = _activeRide.value ?: return
        val finalPrice = if (current.captainOfferPrice > 0) current.captainOfferPrice else current.negotiatedPrice
        _activeRide.value = current.copy(
            status = "ACCEPTED",
            negotiatedPrice = finalPrice
        )
    }

    // تقديم عرض سعر بديل من العميل
    fun clientCounterOffer(newPrice: Double) {
        val current = _activeRide.value ?: return
        _activeRide.value = current.copy(
            status = "PENDING",
            negotiatedPrice = newPrice,
            initialPriceOffer = newPrice,
            captainOfferPrice = 0.0
        )
        // محاكاة موافقة الكابتن بعد ثانية
        scope.launch {
            delay(1500)
            if (_activeRide.value?.requestId == current.requestId) {
                _activeRide.value = _activeRide.value?.copy(
                    status = "ACCEPTED"
                )
            }
        }
    }

    // قبول الكابتن لطلب الرحلة مباشرة بسعر العميل
    fun captainAcceptRequest(requestId: String, captain: User) {
        _rideRequests.value = _rideRequests.value.map { req ->
            if (req.requestId == requestId) {
                req.copy(
                    status = "ACCEPTED",
                    assignedCaptainId = captain.uid,
                    assignedCaptainName = captain.name,
                    assignedCaptainPhone = captain.phone,
                    assignedCaptainRating = captain.rating,
                    assignedVehiclePlate = captain.vehiclePlateNumber
                )
            } else req
        }
        if (_activeRide.value?.requestId == requestId) {
            _activeRide.value = _activeRide.value?.copy(
                status = "ACCEPTED",
                assignedCaptainId = captain.uid,
                assignedCaptainName = captain.name,
                assignedCaptainPhone = captain.phone,
                assignedCaptainRating = captain.rating,
                assignedVehiclePlate = captain.vehiclePlateNumber
            )
        }
    }

    // تفاوض الكابتن بسعر أعلى
    fun captainCounterOffer(requestId: String, captain: User, offerPrice: Double) {
        _rideRequests.value = _rideRequests.value.map { req ->
            if (req.requestId == requestId) {
                req.copy(
                    status = "NEGOTIATING",
                    assignedCaptainId = captain.uid,
                    assignedCaptainName = captain.name,
                    assignedCaptainPhone = captain.phone,
                    assignedCaptainRating = captain.rating,
                    assignedVehiclePlate = captain.vehiclePlateNumber,
                    captainOfferPrice = offerPrice
                )
            } else req
        }
        if (_activeRide.value?.requestId == requestId) {
            _activeRide.value = _activeRide.value?.copy(
                status = "NEGOTIATING",
                assignedCaptainId = captain.uid,
                assignedCaptainName = captain.name,
                assignedCaptainPhone = captain.phone,
                assignedCaptainRating = captain.rating,
                assignedVehiclePlate = captain.vehiclePlateNumber,
                captainOfferPrice = offerPrice
            )
        }
    }

    // إنهاء الرحلة وتسجيل المكافآت
    fun completeRide(): Pair<Boolean, String> {
        val ride = _activeRide.value ?: return Pair(false, "لا توجد رحلة نشطة")
        val updatedRide = ride.copy(status = "COMPLETED")
        _activeRide.value = null

        // تحديث إحصائيات العميل
        val user = _currentUser.value
        val newCount = user.completedRidesWeek + 1
        val isCustomerRewarded = newCount >= 59
        _currentUser.value = user.copy(
            completedRidesWeek = newCount,
            isVerified = if (isCustomerRewarded) true else user.isVerified
        )

        // تحديث إحصائيات الكابتن
        val capId = ride.assignedCaptainId
        var isCaptainRewarded = false
        _captains.value = _captains.value.map { cap ->
            if (cap.uid == capId) {
                val newCapCount = cap.completedRidesWeek + 1
                if (newCapCount >= 100) isCaptainRewarded = true
                cap.copy(completedRidesWeek = newCapCount)
            } else cap
        }

        val message = when {
            isCustomerRewarded -> "تهانينا! لقد أتممت 59 رحلة وحصلت على رحلة مجانية وشارة (يلا بشهر)! 🎉"
            newCount == 58 -> "أحسنت! بقيت رحلة واحدة فقط للحصول على رحلتك المجانية!"
            else -> "تم إنهاء الرحلة بنجاح. شكراً لاختيارك يلا مشوار!"
        }

        return Pair(true, message)
    }

    // إلغاء الرحلة
    fun cancelRide() {
        val ride = _activeRide.value ?: return
        _activeRide.value = null
        _rideRequests.value = _rideRequests.value.filter { it.requestId != ride.requestId }
    }

    // تسجيل بلاغ / بلوك ضد كابتن مع الحظر التلقائي عند التكرار 3 مرات
    fun reportCaptain(captainId: String): String {
        var resultMessage = "تم تسجيل البلاغ بنجاح"
        _captains.value = _captains.value.map { cap ->
            if (cap.uid == captainId) {
                val newBlocks = cap.blockCount + 1
                if (newBlocks >= 3) {
                    val banUntil = System.currentTimeMillis() + (24 * 60 * 60 * 1000L) // حظر 24 ساعة
                    resultMessage = "وصل الكابتن إلى 3 بلاغات وتم حظره تلقائياً لمدة 24 ساعة!"
                    cap.copy(blockCount = newBlocks, isBanned = true, banUntilTimestamp = banUntil)
                } else {
                    resultMessage = "تم تسجيل بلوك للكابتن ($newBlocks / 3 قبل الحظر التلقائي)"
                    cap.copy(blockCount = newBlocks)
                }
            } else cap
        }
        return resultMessage
    }

    // فك حظر الكابتن من الإدارة
    fun unbanCaptain(captainId: String) {
        _captains.value = _captains.value.map { cap ->
            if (cap.uid == captainId) {
                cap.copy(isBanned = false, blockCount = 0, banUntilTimestamp = 0L)
            } else cap
        }
    }

    // موافقة أو رفض الإدارة على أوراق الكابتن
    fun updateCaptainApproval(captainId: String, approved: Boolean): String {
        _captains.value = _captains.value.map { cap ->
            if (cap.uid == captainId) {
                cap.copy(isApprovedByAdmin = approved)
            } else cap
        }
        return if (approved) "تمت الموافقة على أوراق الكابتن وتفعيل حسابه" else "تم رفض أوراق الكابتن"
    }

    // تسجيل كابتن جديد من شاشة التسجيل
    fun registerNewCaptain(
        name: String,
        phone: String,
        vehicleType: String,
        model: String,
        plate: String
    ) {
        val newCap = User(
            uid = "cap_" + System.currentTimeMillis(),
            name = name,
            phone = phone,
            role = "CAPTAIN",
            vehicleType = vehicleType,
            vehicleModel = model,
            vehiclePlateNumber = plate,
            rating = 5.0,
            idCardFrontUrl = "بطاقة_وجه.jpg",
            idCardBackUrl = "بطاقة_ظهر.jpg",
            licenseUrl = "رخصة_قيادة.jpg",
            carPhotoUrl = "صورة_المركبة.jpg",
            isApprovedByAdmin = false
        )
        _captains.value = listOf(newCap) + _captains.value
    }

    // إرسال رسالة في الشات الحي
    fun sendChatMessage(requestId: String, text: String, senderRole: String) {
        val user = _currentUser.value
        val newMsg = ChatMessage(
            messageId = "m_" + System.currentTimeMillis(),
            requestId = requestId,
            senderId = user.uid,
            senderName = user.name,
            senderRole = senderRole,
            messageText = text,
            timestamp = System.currentTimeMillis()
        )
        _chatMessages.value = _chatMessages.value + newMsg

        // محاكاة رد الكابتن / العميل تلقائياً
        scope.launch {
            delay(1200)
            val replyText = when {
                senderRole == "USER" && text.contains("فين") -> "أنا في الشارع الرئيسي قريب جداً منك دقيقة وأصل"
                senderRole == "USER" -> "تمام يا فندم تسلم، شايفك وبقرب منك"
                else -> "تمام يا كابتن أنا في انتظارك"
            }
            val replyMsg = ChatMessage(
                messageId = "m_" + System.currentTimeMillis(),
                requestId = requestId,
                senderId = if (senderRole == "USER") "cap_001" else user.uid,
                senderName = if (senderRole == "USER") "الكابتن" else "العميل",
                senderRole = if (senderRole == "USER") "CAPTAIN" else "USER",
                messageText = replyText,
                timestamp = System.currentTimeMillis()
            )
            _chatMessages.value = _chatMessages.value + replyMsg
        }
    }

    // فحص المكافآت الأسبوعية
    fun checkWeeklyRewards(userId: String, isCaptain: Boolean): RewardStatus {
        return if (isCaptain) {
            val captain = _captains.value.find { it.uid == userId } ?: _currentUser.value
            val count = captain.completedRidesWeek
            RewardStatus(
                hasReward = count >= 100,
                rewardMessage = if (count >= 100) "مبروك! حققت 100 رحلة وحصلت على بوناص 100 جنيه نقداً! 🎁" else "أكملت $count من 100 رحلة لكسب بوناص 100 ج.م",
                currentCount = count,
                targetCount = 100
            )
        } else {
            val user = _currentUser.value
            val count = user.completedRidesWeek
            RewardStatus(
                hasReward = count >= 59,
                rewardMessage = if (count >= 59) "مبروك! حققت 59 رحلة وحصلت على رحلة مجانية! 🎁" else "أكملت $count من 59 رحلة للحصول على رحلة مجانية",
                currentCount = count,
                targetCount = 59
            )
        }
    }
}
