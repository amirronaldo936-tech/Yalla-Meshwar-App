package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppRepository
import com.example.models.User
import com.example.ui.theme.CleanWhite
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.NavyCard
import com.example.ui.theme.NavyDark
import com.example.ui.theme.OffWhiteBg
import com.example.utils.CaptainManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val captains by AppRepository.captains.collectAsState()
    val rideRequests by AppRepository.rideRequests.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) } // 0: طلبات الانضمام, 1: قائمة الكباتن والحظر, 2: الإحصائيات

    val pendingCaptains = captains.filter { !it.isApprovedByAdmin }
    val approvedCaptains = captains.filter { it.isApprovedByAdmin }
    val bannedCaptains = captains.filter { it.isBanned }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(OffWhiteBg)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // شريط العنوان العلوي
            Surface(
                color = NavyDark,
                shadowElevation = 6.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(top = 40.dp, bottom = 12.dp, start = 16.dp, end = 16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onBack) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "رجوع",
                                tint = CleanWhite
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            Icons.Default.AdminPanelSettings,
                            contentDescription = null,
                            tint = GoldAccent,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "لوحة تحكم إدارة يلا مشوار",
                            color = CleanWhite,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // إحصائيات سريعة في الهيدر
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        QuickStatBadge("كباتن معتمدين", "${approvedCaptains.size}", EmeraldPrimary)
                        QuickStatBadge("طلبات معلقة", "${pendingCaptains.size}", GoldAccent)
                        QuickStatBadge("كباتن محظورين", "${bannedCaptains.size}", Color(0xFFEF4444))
                        QuickStatBadge("إجمالي الطلبات", "${rideRequests.size}", CleanWhite)
                    }
                }
            }

            // تبويبات الإدارة
            PrimaryTabRow(
                selectedTabIndex = selectedTab,
                containerColor = CleanWhite
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            "مراجعة الأوراق (${pendingCaptains.size})",
                            fontSize = 12.sp,
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            "الكباتن والحظر (${approvedCaptains.size})",
                            fontSize = 12.sp,
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = {
                        Text(
                            "طلبات الرحلات (${rideRequests.size})",
                            fontSize = 12.sp,
                            fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }

            // محتوى التبويب
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                when (selectedTab) {
                    0 -> {
                        // تبويب مراجعة أوراق الكباتن (الموافقات والرفض كما في AdminDashboardActivity)
                        if (pendingCaptains.isEmpty()) {
                            EmptyStateNotice("لا توجد طلبات كباتن معلقة حالياً", "جميع الكباتن المتقدمين تمت مراجعة أوراقهم")
                        } else {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                items(pendingCaptains) { captain ->
                                    PendingCaptainCard(
                                        captain = captain,
                                        onApprove = {
                                            val msg = CaptainManager.updateCaptainStatus(captain.uid, true)
                                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                        },
                                        onReject = {
                                            val msg = CaptainManager.updateCaptainStatus(captain.uid, false)
                                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                        }
                                    )
                                }
                            }
                        }
                    }

                    1 -> {
                        // تبويب الكباتن المسجلين والبلوك والحظر
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            items(captains) { captain ->
                                CaptainAdminItemCard(
                                    captain = captain,
                                    onUnban = {
                                        CaptainManager.unbanCaptain(captain.uid)
                                        Toast.makeText(context, "تم فك الحظر عن الكابتن ${captain.name}", Toast.LENGTH_SHORT).show()
                                    },
                                    onSimulateBlock = {
                                        val msg = CaptainManager.reportCaptain(captain.uid)
                                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }
                        }
                    }

                    2 -> {
                        // تبويب سجل طلبات الرحلات
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            items(rideRequests) { req ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = CleanWhite)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "${req.userName} • ${req.serviceType}",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp
                                            )
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = when (req.status) {
                                                    "PENDING" -> GoldAccent.copy(alpha = 0.2f)
                                                    "ACCEPTED" -> EmeraldPrimary.copy(alpha = 0.2f)
                                                    else -> Color(0xFFE2E8F0)
                                                }
                                            ) {
                                                Text(
                                                    text = req.status,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = when (req.status) {
                                                        "PENDING" -> GoldAccent
                                                        "ACCEPTED" -> EmeraldDark
                                                        else -> Color(0xFF475569)
                                                    },
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(text = "من: ${req.pickupName}", fontSize = 12.sp, color = Color(0xFF4B5563))
                                        Text(text = "إلى: ${req.destinationName}", fontSize = 12.sp, color = Color(0xFF4B5563))
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "المركبة: ${req.vehicleCategory} • العرض: ${req.initialPriceOffer.toInt()} ج.م • الدفع: ${req.paymentMethod}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = EmeraldDark
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PendingCaptainCard(
    captain: User,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = CleanWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(GoldAccent.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = when (captain.vehicleType) {
                            "TUKTUK" -> "🛺"
                            "SCOOTER" -> "🛵"
                            else -> "🚗"
                        },
                        fontSize = 22.sp
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = captain.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = NavyDark
                    )
                    Text(
                        text = "${captain.phone} • ${captain.vehicleModel}",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )
                    Text(
                        text = "لوحة: ${captain.vehiclePlateNumber}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = EmeraldDark
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // المستندات المرفقة للمراجعة
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = OffWhiteBg,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(text = "المستندات المرفقة:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF334155))
                    Text(text = "✓ بطاقة الرقم القومي (وجه وظهر سارية)", fontSize = 11.sp, color = Color(0xFF475569))
                    Text(text = "✓ رخصة القيادة المهنية والخاصة", fontSize = 11.sp, color = Color(0xFF475569))
                    Text(text = "✓ فحص وصورة المركبة مطابقة للمواصفات", fontSize = 11.sp, color = Color(0xFF475569))
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // أزرار الموافقة والرفض (كما في AdminDashboardActivity)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onApprove,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("btnApproveCaptain"),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.ThumbUp, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("موافقة وتفعيل", fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onReject,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("btnRejectCaptain"),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.ThumbDown, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("رفض الأوراق", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun CaptainAdminItemCard(
    captain: User,
    onUnban: () -> Unit,
    onSimulateBlock: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CleanWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = when (captain.vehicleType) {
                            "TUKTUK" -> "🛺"
                            "SCOOTER" -> "🛵"
                            else -> "🚗"
                        },
                        fontSize = 20.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = captain.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "⭐ ${captain.rating}", fontSize = 11.sp, color = GoldAccent, fontWeight = FontWeight.Bold)
                        }
                        Text(text = "${captain.phone} • ${captain.vehicleModel}", fontSize = 11.sp, color = Color.Gray)
                    }
                }

                // حالة الحظر أو التفعيل
                if (captain.isBanned) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFFEE2E2)
                    ) {
                        Text(
                            text = "محظور 24 ساعة 🚫",
                            color = Color(0xFFDC2626),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                } else {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = EmeraldPrimary.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "نشط ومعتمد ✓",
                            color = EmeraldDark,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // تفاصيل البلوك والمكافأة الأسبوعية
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "عدد البلاغات: ${captain.blockCount} / 3",
                    fontSize = 12.sp,
                    color = if (captain.blockCount >= 3) Color.Red else Color(0xFF4B5563),
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "رحلات الأسبوع: ${captain.completedRidesWeek} / 100",
                    fontSize = 12.sp,
                    color = EmeraldDark,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // أزرار فك الحظر أو تجربة البلوك
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                if (captain.isBanned) {
                    Button(
                        onClick = onUnban,
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.LockOpen, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("فك الحظر فوراً", fontSize = 12.sp)
                    }
                } else {
                    OutlinedButton(
                        onClick = onSimulateBlock,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Block, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("تسجيل بلوك (+1)", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun QuickStatBadge(title: String, count: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = count, color = color, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Text(text = title, color = Color(0xFFCBD5E1), fontSize = 10.sp)
    }
}

@Composable
fun EmptyStateNotice(title: String, subtitle: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "📋", fontSize = 48.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = NavyDark)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = subtitle, fontSize = 12.sp, color = Color.Gray)
        }
    }
}
