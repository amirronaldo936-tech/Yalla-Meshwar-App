package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppRepository
import com.example.ui.components.InteractiveEgyptianMap
import com.example.ui.theme.CashGreen
import com.example.ui.theme.CleanWhite
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.NavyCard
import com.example.ui.theme.NavyDark
import com.example.ui.theme.OffWhiteBg
import com.example.ui.theme.VodafoneRed
import com.example.utils.CaptainManager

@Composable
fun MainRideScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToCaptainRegister: () -> Unit,
    onNavigateToAdmin: () -> Unit,
    onNavigateToChat: (String) -> Unit
) {
    val context = LocalContext.current
    val currentUser by AppRepository.currentUser.collectAsState()
    val activeRide by AppRepository.activeRide.collectAsState()

    var selectedCity by remember { mutableStateOf("المحلة الكبرى") }
    var selectedCategory by remember { mutableStateOf("ملاكي") } // "توكتوك", "اسكوتر", "ملاكي"
    var selectedService by remember { mutableStateOf("أفراح") } // "مشوار", "طلبات", "أفراح", "سفر", "مطار"
    var pickupName by remember { mutableStateOf("حي أول المحلة - شارع البحر") }
    var destinationName by remember { mutableStateOf("طنطا - قاعة الماسة") }
    var offerPrice by remember { mutableDoubleStateOf(150.0) }
    var paymentMethod by remember { mutableStateOf("VODAFONE_CASH") } // "CASH", "VODAFONE_CASH"

    var showGuestDialog by remember { mutableStateOf(false) }
    var showRoleSwitchDialog by remember { mutableStateOf(false) }
    var showReportDialog by remember { mutableStateOf(false) }
    var showCounterOfferDialog by remember { mutableStateOf(false) }
    var counterPriceInput by remember { mutableStateOf("160") }
    var showRewardAlert by remember { mutableStateOf<String?>(null) }
    var showLocationPicker by remember { mutableStateOf<String?>(null) } // "PICKUP" or "DEST"

    val cities = listOf("المحلة الكبرى", "طنطا", "المنصورة", "القاهرة")
    val services = listOf("مشوار", "طلبات", "أفراح", "سفر", "مطار")

    Box(modifier = Modifier.fillMaxSize()) {
        // الخريطة التفاعلية في الخلفية
        InteractiveEgyptianMap(
            selectedCity = selectedCity,
            pickupName = pickupName,
            destinationName = destinationName,
            selectedCategory = selectedCategory,
            isRideActive = activeRide != null,
            modifier = Modifier.fillMaxSize()
        )

        // الشريط العلوي (بيانات المستخدم، شارة يلا بشهر، أزرار التبديل والإدارة)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(top = 40.dp, start = 16.dp, end = 16.dp)
        ) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = NavyDark.copy(alpha = 0.95f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clickable { showRoleSwitchDialog = true }
                                .padding(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(EmeraldPrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = when (currentUser.role) {
                                        "CAPTAIN" -> Icons.Default.DirectionsCar
                                        "ADMIN" -> Icons.Default.AdminPanelSettings
                                        else -> Icons.Default.Person
                                    },
                                    contentDescription = "User Avatar",
                                    tint = CleanWhite,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = currentUser.name,
                                        color = CleanWhite,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                    if (currentUser.isVerified) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            Icons.Default.Verified,
                                            contentDescription = "يلا بشهر",
                                            tint = GoldAccent,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = when (currentUser.role) {
                                        "CAPTAIN" -> "كابتن معتمد (${currentUser.vehicleType})"
                                        "ADMIN" -> "مدير النظام"
                                        "GUEST" -> "زائر (غير مسجل)"
                                        else -> "عميل • يلا بشهر"
                                    },
                                    color = Color(0xFF94A3B8),
                                    fontSize = 12.sp
                                )
                            }
                        }

                        // أزرار التنقل السريع (تبديل الدور، دخول، إدارة)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { showRoleSwitchDialog = true },
                                modifier = Modifier.testTag("switch_role_btn")
                            ) {
                                Icon(Icons.Default.SwapHoriz, contentDescription = "تبديل الحساب", tint = GoldAccent)
                            }
                            IconButton(
                                onClick = onNavigateToAdmin,
                                modifier = Modifier.testTag("admin_dashboard_btn")
                            ) {
                                Icon(Icons.Default.AdminPanelSettings, contentDescription = "لوحة الإدارة", tint = CleanWhite)
                            }
                            if (currentUser.role == "GUEST") {
                                IconButton(
                                    onClick = onNavigateToLogin,
                                    modifier = Modifier.testTag("login_btn")
                                ) {
                                    Icon(Icons.Default.Login, contentDescription = "تسجيل الدخول", tint = EmeraldPrimary)
                                }
                            }
                        }
                    }

                    // شريط تقدم مكافأة الـ 59 رحلة للعميل
                    if (currentUser.role == "USER") {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF0F2B26))
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "مكافأة الأسبوع: ${currentUser.completedRidesWeek} / 59 رحلة",
                                    color = CleanWhite,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Text(
                                text = if (currentUser.completedRidesWeek >= 59) "رحلة مجانية جاهزة! 🎁" else "بقيت ${59 - currentUser.completedRidesWeek} رحلة",
                                color = GoldAccent,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // محدد المدن المصرية
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(cities) { city ->
                    val isSelected = selectedCity == city
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) EmeraldPrimary else CleanWhite.copy(alpha = 0.95f),
                        shadowElevation = 4.dp,
                        modifier = Modifier.clickable { selectedCity = city }
                    ) {
                        Text(
                            text = city,
                            color = if (isSelected) CleanWhite else NavyDark,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // كارت طلب الرحلة / لوحة التحكم بالخدمات السفلية
        Card(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
            colors = CardDefaults.cardColors(containerColor = CleanWhite)
        ) {
            Column(
                modifier = Modifier
                    .padding(18.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // عنوان كارت الخدمات
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "يلا مشوار - اختر خدمتك",
                        color = Color(0xFF111827),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = selectedCity,
                        color = EmeraldDark,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // كارت خيارات المركبات الثلاثة: توكتوك، اسكوتر، ملاكي
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // توكتوك (طلبات/مشوار)
                    VehicleCategoryCard(
                        title = "توكتوك",
                        subtitle = "(طلبات/مشوار)",
                        iconEmoji = "🛺",
                        isSelected = selectedCategory == "توكتوك",
                        modifier = Modifier.weight(1f),
                        onClick = {
                            selectedCategory = "توكتوك"
                            offerPrice = 25.0
                        }
                    )

                    // اسكوتر (سريع)
                    VehicleCategoryCard(
                        title = "اسكوتر",
                        subtitle = "(سريع)",
                        iconEmoji = "🛵",
                        isSelected = selectedCategory == "اسكوتر",
                        modifier = Modifier.weight(1f),
                        onClick = {
                            selectedCategory = "اسكوتر"
                            offerPrice = 35.0
                        }
                    )

                    // ملاكي (أفراح/سفر/مطار)
                    VehicleCategoryCard(
                        title = "ملاكي",
                        subtitle = "(أفراح/سفر/مطار)",
                        iconEmoji = "🚗",
                        isSelected = selectedCategory == "ملاكي",
                        modifier = Modifier.weight(1f),
                        onClick = {
                            selectedCategory = "ملاكي"
                            offerPrice = 150.0
                        }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // شرائح نوع الخدمة (مشوار، طلبات، أفراح، سفر، مطار)
                Text(
                    text = "نوع الخدمة المطلوبة:",
                    fontSize = 12.sp,
                    color = Color(0xFF4B5563),
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(services) { svc ->
                        val isSel = selectedService == svc
                        FilterChip(
                            selected = isSel,
                            onClick = { selectedService = svc },
                            label = { Text(svc, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = EmeraldPrimary,
                                selectedLabelColor = CleanWhite
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // موقع الانطلاق والوصول
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(OffWhiteBg)
                        .clickable { showLocationPicker = "PICKUP" }
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.MyLocation, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "نقطة الانطلاق", fontSize = 11.sp, color = Color(0xFF6B7280))
                        Text(text = pickupName, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = NavyDark)
                    }
                    Text(text = "تغيير", fontSize = 11.sp, color = EmeraldDark, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(OffWhiteBg)
                        .clickable { showLocationPicker = "DEST" }
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "الوجهة ومكان الوصول", fontSize = 11.sp, color = Color(0xFF6B7280))
                        Text(text = destinationName, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = NavyDark)
                    }
                    Text(text = "تغيير", fontSize = 11.sp, color = EmeraldDark, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(10.dp))

                // عرض السعر المبدئي والتفاوض وطريقة الدفع
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // السعر المقترح مع أزرار + و -
                    Column {
                        Text(text = "سعرك المقترح:", fontSize = 11.sp, color = Color(0xFF6B7280))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = OffWhiteBg,
                                modifier = Modifier
                                    .size(30.dp)
                                    .clickable { if (offerPrice > 10) offerPrice -= 5.0 }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("-", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${offerPrice.toInt()} ج.م",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldDark
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = CircleShape,
                                color = OffWhiteBg,
                                modifier = Modifier
                                    .size(30.dp)
                                    .clickable { offerPrice += 5.0 }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("+", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    // طريقة الدفع (كاش أو فودافون كاش)
                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "طريقة الدفع:", fontSize = 11.sp, color = Color(0xFF6B7280))
                        Row {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (paymentMethod == "CASH") CashGreen.copy(alpha = 0.15f) else Color.Transparent,
                                border = if (paymentMethod == "CASH") androidx.compose.foundation.BorderStroke(1.dp, CashGreen) else null,
                                modifier = Modifier
                                    .clickable { paymentMethod = "CASH" }
                                    .padding(4.dp)
                            ) {
                                Text(
                                    text = "💵 كاش",
                                    fontSize = 11.sp,
                                    fontWeight = if (paymentMethod == "CASH") FontWeight.Bold else FontWeight.Normal,
                                    color = if (paymentMethod == "CASH") CashGreen else Color.Gray,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (paymentMethod == "VODAFONE_CASH") VodafoneRed.copy(alpha = 0.15f) else Color.Transparent,
                                border = if (paymentMethod == "VODAFONE_CASH") androidx.compose.foundation.BorderStroke(1.dp, VodafoneRed) else null,
                                modifier = Modifier
                                    .clickable { paymentMethod = "VODAFONE_CASH" }
                                    .padding(4.dp)
                            ) {
                                Text(
                                    text = "📱 فودافون كاش",
                                    fontSize = 11.sp,
                                    fontWeight = if (paymentMethod == "VODAFONE_CASH") FontWeight.Bold else FontWeight.Normal,
                                    color = if (paymentMethod == "VODAFONE_CASH") VodafoneRed else Color.Gray,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // زر تأكيد وطلب الرحلة (الزر الرئيسي)
                Button(
                    onClick = {
                        if (currentUser.role == "GUEST") {
                            // إلزام الزائر بالتسجيل أولاً
                            showGuestDialog = true
                        } else {
                            AppRepository.createRideRequest(
                                serviceType = selectedService,
                                vehicleCategory = selectedCategory,
                                pickupName = pickupName,
                                destinationName = destinationName,
                                pickupLat = 30.9706,
                                pickupLng = 31.1669,
                                destLat = 30.7865,
                                destLng = 31.0004,
                                priceOffer = offerPrice,
                                paymentMethod = paymentMethod
                            )
                            Toast.makeText(
                                context,
                                "تم إرسال الطلب لجميع الكباتن وبانتظار التفاوض",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("btnRequestRide"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                ) {
                    Text(
                        text = "تأكيد وطلب الرحلة",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = CleanWhite
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // زر انضمام الكابتن
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    TextButton(onClick = onNavigateToCaptainRegister) {
                        Text(
                            text = "هل تملك مركبة وتريد العمل معنا؟ سجل ككابتن الآن 🚗",
                            color = EmeraldDark,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // لوحة حالة الرحلة النشطة والتفاوض الحي (Active Ride Sheet)
        activeRide?.let { ride ->
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 16.dp),
                colors = CardDefaults.cardColors(containerColor = NavyDark)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    when (ride.status) {
                        "PENDING" -> {
                            // حالة انتظار قبول الكابتن
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(
                                    color = GoldAccent,
                                    modifier = Modifier.size(28.dp),
                                    strokeWidth = 3.dp
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "جاري البحث عن كباتن بالقرب منك...",
                                        color = CleanWhite,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                    Text(
                                        text = "طلبك: ${ride.vehicleCategory} (${ride.serviceType}) • عرضك: ${ride.initialPriceOffer.toInt()} ج.م",
                                        color = Color(0xFF94A3B8),
                                        fontSize = 12.sp
                                    )
                                }
                                IconButton(onClick = { AppRepository.cancelRide() }) {
                                    Icon(Icons.Default.Close, contentDescription = "إلغاء", tint = Color.LightGray)
                                }
                            }
                        }

                        "NEGOTIATING" -> {
                            // حالة تفاوض السعر مع الكابتن
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = "🤝", fontSize = 24.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "الكابتن يقدم عرض سعر بديل!",
                                            color = GoldAccent,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp
                                        )
                                        Text(
                                            text = "${ride.assignedCaptainName} (${ride.vehicleCategory})",
                                            color = CleanWhite,
                                            fontSize = 13.sp
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(NavyCard)
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(text = "عرضك المبدئي", color = Color(0xFF94A3B8), fontSize = 11.sp)
                                        Text(text = "${ride.initialPriceOffer.toInt()} ج.م", color = CleanWhite, fontWeight = FontWeight.Bold)
                                    }
                                    Text(text = "⬅️", fontSize = 16.sp)
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(text = "عرض الكابتن", color = GoldAccent, fontSize = 11.sp)
                                        Text(text = "${ride.captainOfferPrice.toInt()} ج.م", color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                    }
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = { AppRepository.acceptCaptainOffer() },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                                    ) {
                                        Text("قبول السعر", fontWeight = FontWeight.Bold)
                                    }
                                    OutlinedButton(
                                        onClick = { showCounterOfferDialog = true },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = GoldAccent)
                                    ) {
                                        Text("تفاوض آخر")
                                    }
                                    IconButton(
                                        onClick = { AppRepository.cancelRide() }
                                    ) {
                                        Icon(Icons.Default.Close, contentDescription = "رفض", tint = Color.Red)
                                    }
                                }
                            }
                        }

                        "ACCEPTED" -> {
                            // تم قبول الرحلة والكابتن في الطريق
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(46.dp)
                                                .clip(CircleShape)
                                                .background(EmeraldDark),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = when (ride.vehicleCategory) {
                                                    "توكتوك" -> "🛺"
                                                    "اسكوتر" -> "🛵"
                                                    else -> "🚗"
                                                },
                                                fontSize = 24.sp
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = ride.assignedCaptainName,
                                                    color = CleanWhite,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 16.sp
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = "⭐ ${ride.assignedCaptainRating}",
                                                    color = GoldAccent,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                            Text(
                                                text = "لوحة: ${ride.assignedVehiclePlate} • السعر: ${ride.negotiatedPrice.toInt()} ج.م",
                                                color = Color(0xFF94A3B8),
                                                fontSize = 12.sp
                                            )
                                        }
                                    }

                                    // شارة الوصول المتوقع
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = EmeraldPrimary.copy(alpha = 0.2f)
                                    ) {
                                        Text(
                                            text = "خلال ٤ دقائق",
                                            color = EmeraldPrimary,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // أزرار الإجراءات: اتصال، شات، بلاغ، إنهاء
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // زر الاتصال الهاتفي
                                    Button(
                                        onClick = {
                                            val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                                                data = Uri.parse("tel:${ride.assignedCaptainPhone}")
                                            }
                                            context.startActivity(dialIntent)
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(Icons.Default.Call, contentDescription = "اتصال", modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("اتصال", fontSize = 12.sp)
                                    }

                                    // زر الشات المباشر
                                    Button(
                                        onClick = { onNavigateToChat(ride.requestId) },
                                        colors = ButtonDefaults.buttonColors(containerColor = NavyCard),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = "شات", modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("شات حي", fontSize = 12.sp)
                                    }

                                    // زر حظر وإبلاغ (بلوك)
                                    OutlinedButton(
                                        onClick = { showReportDialog = true },
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444)),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Icon(Icons.Default.Block, contentDescription = "بلوك", modifier = Modifier.size(16.dp))
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // زر إنهاء الرحلة ودفع الأجرة
                                Button(
                                    onClick = {
                                        val result = AppRepository.completeRide()
                                        showRewardAlert = result.second
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF047857)),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("تم الوصول وإنهاء الرحلة (دفع ${ride.negotiatedPrice.toInt()} ج.م)")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // نافذة تنبيه الزائر للتسجيل
    if (showGuestDialog) {
        AlertDialog(
            onDismissRequest = { showGuestDialog = false },
            icon = { Icon(Icons.Default.Login, contentDescription = null, tint = EmeraldPrimary) },
            title = { Text("تسجيل الدخول مطلوب", fontWeight = FontWeight.Bold, textAlign = TextAlign.Center) },
            text = {
                Text(
                    "يرجى تسجيل الدخول أولاً للاستفادة من الخدمات وتأكيد طلبات التوصيل والنقل في يلا مشوار.",
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showGuestDialog = false
                        onNavigateToLogin()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                ) {
                    Text("تسجيل الدخول الآن")
                }
            },
            dismissButton = {
                TextButton(onClick = { showGuestDialog = false }) {
                    Text("متابعة التصفح")
                }
            }
        )
    }

    // نافذة تبديل الأدوار للتجربة السلسة
    if (showRoleSwitchDialog) {
        AlertDialog(
            onDismissRequest = { showRoleSwitchDialog = false },
            title = { Text("اختر الحساب للتجربة", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("يمكنك التبديل بين مختلف الأدوار لاختبار كافة وظائف يلا مشوار:")
                    Spacer(modifier = Modifier.height(10.dp))
                    RoleOptionItem("👤 عميل (محمد علي - ٥٨ رحلة)", currentUser.role == "USER") {
                        AppRepository.switchRole("USER")
                        showRoleSwitchDialog = false
                    }
                    RoleOptionItem("🛺 كابتن معتمد (أحمد سامي)", currentUser.role == "CAPTAIN") {
                        AppRepository.switchRole("CAPTAIN")
                        showRoleSwitchDialog = false
                    }
                    RoleOptionItem("🛡️ مدير النظام (Admin)", currentUser.role == "ADMIN") {
                        AppRepository.switchRole("ADMIN")
                        showRoleSwitchDialog = false
                    }
                    RoleOptionItem("🌐 وضع الزائر (Guest)", currentUser.role == "GUEST") {
                        AppRepository.switchRole("GUEST")
                        showRoleSwitchDialog = false
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    RoleOptionItem("📝 تسجيل كابتن جديد (رفع المستندات)", false) {
                        showRoleSwitchDialog = false
                        onNavigateToCaptainRegister()
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showRoleSwitchDialog = false }) {
                    Text("إغلاق")
                }
            }
        )
    }

    // نافذة إبلاغ وحظر الكابتن (بلوك 3 مرات = حظر 24 ساعة)
    if (showReportDialog) {
        AlertDialog(
            onDismissRequest = { showReportDialog = false },
            icon = { Icon(Icons.Default.Block, contentDescription = null, tint = Color.Red) },
            title = { Text("إبلاغ وحظر الكابتن", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "هل ترغب في تسجيل بلاغ وبلوك ضد الكابتن؟\nوفقاً لنظام يلا مشوار: عند تكرار البلوك 3 مرات، سيتم حظر الكابتن تلقائياً لمدة 24 ساعة ومنعه من استقبال رحلات."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val capId = activeRide?.assignedCaptainId ?: "cap_001"
                        val msg = CaptainManager.reportCaptain(capId)
                        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                        showReportDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("تأكيد البلوك")
                }
            },
            dismissButton = {
                TextButton(onClick = { showReportDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }

    // نافذة تفاوض السعر البديل
    if (showCounterOfferDialog) {
        AlertDialog(
            onDismissRequest = { showCounterOfferDialog = false },
            title = { Text("تقديم عرض سعر تفاوضي", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("أدخل السعر الذي يناسبك للرحلة:")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = counterPriceInput,
                        onValueChange = { counterPriceInput = it },
                        label = { Text("السعر بالجنيه المصري") }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val price = counterPriceInput.toDoubleOrNull() ?: 160.0
                        AppRepository.clientCounterOffer(price)
                        showCounterOfferDialog = false
                        Toast.makeText(context, "تم إرسال عرضك الجديد للكابتن", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                ) {
                    Text("إرسال العرض")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCounterOfferDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }

    // نافذة اختيار الموقع من القائمة الجاهزة
    showLocationPicker?.let { target ->
        AlertDialog(
            onDismissRequest = { showLocationPicker = null },
            title = { Text(if (target == "PICKUP") "اختر نقطة الانطلاق" else "اختر الوجهة", fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    AppRepository.locationPresets
                        .filter { it.city == selectedCity || selectedCity == "المحلة الكبرى" }
                        .forEach { preset ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        if (target == "PICKUP") pickupName = "${preset.city} - ${preset.name}"
                                        else destinationName = "${preset.city} - ${preset.name}"
                                        showLocationPicker = null
                                    }
                                    .padding(vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(text = preset.name, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                    Text(text = preset.description, fontSize = 11.sp, color = Color.Gray)
                                }
                            }
                            HorizontalDivider()
                        }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLocationPicker = null }) {
                    Text("إلغاء")
                }
            }
        )
    }

    // نافذة تنبيه المكافآت عند إنهاء الرحلة
    showRewardAlert?.let { alertMsg ->
        AlertDialog(
            onDismissRequest = { showRewardAlert = null },
            icon = { Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(36.dp)) },
            title = { Text("تم إنهاء الرحلة بنجاح", fontWeight = FontWeight.Bold, textAlign = TextAlign.Center) },
            text = { Text(alertMsg, textAlign = TextAlign.Center, fontSize = 14.sp) },
            confirmButton = {
                Button(
                    onClick = { showRewardAlert = null },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                ) {
                    Text("ممتاز")
                }
            }
        )
    }
}

@Composable
fun VehicleCategoryCard(
    title: String,
    subtitle: String,
    iconEmoji: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = if (isSelected) EmeraldPrimary else Color(0xFFF3F4F6),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, EmeraldDark) else null,
        modifier = modifier
            .clickable(onClick = onClick)
            .height(82.dp)
    ) {
        Column(
            modifier = Modifier.padding(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = iconEmoji, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) CleanWhite else Color(0xFF1F2937),
                textAlign = TextAlign.Center
            )
            Text(
                text = subtitle,
                fontSize = 9.sp,
                color = if (isSelected) CleanWhite.copy(alpha = 0.9f) else Color(0xFF6B7280),
                textAlign = TextAlign.Center,
                lineHeight = 10.sp
            )
        }
    }
}

@Composable
fun RoleOptionItem(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .clip(CircleShape)
                .border(2.dp, if (isSelected) EmeraldPrimary else Color.Gray, CircleShape)
                .background(if (isSelected) EmeraldPrimary else Color.Transparent)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = text,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) EmeraldDark else NavyDark,
            fontSize = 13.sp
        )
    }
}
