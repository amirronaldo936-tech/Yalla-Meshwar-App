package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.NavyDark

data class MapVehicle(
    val id: String,
    val type: String, // "توكتوك", "اسكوتر", "ملاكي"
    val xRatio: Float,
    val yRatio: Float,
    val captainName: String,
    val plateNumber: String,
    val rating: Double
)

@Composable
fun InteractiveEgyptianMap(
    selectedCity: String,
    pickupName: String,
    destinationName: String,
    selectedCategory: String,
    isRideActive: Boolean = false,
    modifier: Modifier = Modifier,
    onMapClicked: (Float, Float) -> Unit = { _, _ -> }
) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    var selectedVehicle by remember { mutableStateOf<MapVehicle?>(null) }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseRadius by infiniteTransition.animateFloat(
        initialValue = 8f,
        targetValue = 24f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseRadius"
    )

    val driverMoveProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "driverMoveProgress"
    )

    val vehicles = remember(selectedCity, selectedCategory) {
        listOf(
            MapVehicle("V1", "توكتوك", 0.32f, 0.45f, "كابتن أحمد", "٤٨٢١ ط ع س", 4.9),
            MapVehicle("V2", "توكتوك", 0.65f, 0.38f, "كابتن صابر", "١٩٠٢ ط ر ف", 4.7),
            MapVehicle("V3", "اسكوتر", 0.48f, 0.28f, "كابتن محمود", "١٩٥٢ ج هـ د", 4.8),
            MapVehicle("V4", "اسكوتر", 0.72f, 0.62f, "كابتن وائل", "٣١٤٢ م ك ل", 4.9),
            MapVehicle("V5", "ملاكي", 0.40f, 0.68f, "كابتن تامر", "٧٣١٤ س ب ر", 4.95),
            MapVehicle("V6", "ملاكي", 0.82f, 0.35f, "كابتن حسام", "٥٨٤١ أ ب ج", 4.85)
        )
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFE2E8F0))
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scale = (scale * zoom).coerceIn(0.7f, 2.5f)
                    offsetX += pan.x
                    offsetY += pan.y
                }
            }
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    onMapClicked(offset.x, offset.y)
                }
            }
    ) {
        val width = constraints.maxWidth.toFloat()
        val height = constraints.maxHeight.toFloat()

        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = width / 2 + offsetX
            val cy = height / 2 + offsetY

            // خلفية خريطة المدينة
            drawRect(color = Color(0xFFF1F5F9))

            // رسم مسطح مائي / ترعة المحلة الكبرى أو فرع النيل
            val waterPath = Path().apply {
                moveTo(0f, height * 0.15f)
                cubicTo(
                    width * 0.25f, height * 0.22f,
                    width * 0.70f, height * 0.08f,
                    width, height * 0.25f
                )
                lineTo(width, height * 0.32f)
                cubicTo(
                    width * 0.65f, height * 0.18f,
                    width * 0.30f, height * 0.30f,
                    0f, height * 0.22f
                )
                close()
            }
            drawPath(waterPath, color = Color(0xFF93C5FD))

            // رسم شبكة الشوارع الرئيسية (الشارع الرئيسي / شارع البحر / طريق طنطا المحلة)
            val roadColor = Color(0xFFFFFFFF)
            val roadBorder = Color(0xFFCBD5E1)

            // شارع رئيسي أفقي
            drawLine(
                color = roadBorder,
                start = Offset(0f, height * 0.50f),
                end = Offset(width, height * 0.50f),
                strokeWidth = 32f * scale
            )
            drawLine(
                color = roadColor,
                start = Offset(0f, height * 0.50f),
                end = Offset(width, height * 0.50f),
                strokeWidth = 26f * scale
            )

            // شارع رئيسي رأسي
            drawLine(
                color = roadBorder,
                start = Offset(width * 0.50f, 0f),
                end = Offset(width * 0.50f, height),
                strokeWidth = 28f * scale
            )
            drawLine(
                color = roadColor,
                start = Offset(width * 0.50f, 0f),
                end = Offset(width * 0.50f, height),
                strokeWidth = 22f * scale
            )

            // شوارع فرعية ودائرية
            drawLine(
                color = roadColor,
                start = Offset(width * 0.15f, height * 0.20f),
                end = Offset(width * 0.85f, height * 0.80f),
                strokeWidth = 16f * scale
            )
            drawLine(
                color = roadColor,
                start = Offset(width * 0.85f, height * 0.20f),
                end = Offset(width * 0.20f, height * 0.85f),
                strokeWidth = 16f * scale
            )

            // نقطة الانطلاق (Pickup)
            val pickupPos = Offset(width * 0.35f + offsetX * 0.2f, height * 0.42f + offsetY * 0.2f)
            // نقطة الوصول (Destination)
            val destPos = Offset(width * 0.68f + offsetX * 0.2f, height * 0.62f + offsetY * 0.2f)

            // خط مسار الرحلة المقترح (Dashed Route)
            val routePath = Path().apply {
                moveTo(pickupPos.x, pickupPos.y)
                quadraticTo(
                    width * 0.50f, height * 0.48f,
                    destPos.x, destPos.y
                )
            }

            // رسم المسار
            drawPath(
                path = routePath,
                color = EmeraldPrimary,
                style = Stroke(
                    width = 8f * scale,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 12f), 0f)
                )
            )

            // نبض موقع المستخدم / نقطة الانطلاق
            drawCircle(
                color = EmeraldPrimary.copy(alpha = 0.25f),
                radius = pulseRadius * scale,
                center = pickupPos
            )
            drawCircle(
                color = EmeraldDark,
                radius = 12f * scale,
                center = pickupPos
            )
            drawCircle(
                color = Color.White,
                radius = 5f * scale,
                center = pickupPos
            )

            // نقطة الوجهة (أحمر)
            drawCircle(
                color = Color(0xFFDC2626),
                radius = 12f * scale,
                center = destPos
            )
            drawCircle(
                color = Color.White,
                radius = 4f * scale,
                center = destPos
            )

            // كابتن متحرك على المسار إذا كانت الرحلة نشطة
            if (isRideActive) {
                val driverPos = Offset(
                    pickupPos.x + (destPos.x - pickupPos.x) * driverMoveProgress,
                    pickupPos.y + (destPos.y - pickupPos.y) * driverMoveProgress
                )
                drawCircle(
                    color = GoldAccent,
                    radius = 16f * scale,
                    center = driverPos
                )
                drawCircle(
                    color = NavyDark,
                    radius = 7f * scale,
                    center = driverPos
                )
            }

            // رسم المركبات القريبة على الخريطة
            vehicles.forEach { v ->
                val vx = (width * v.xRatio) + (offsetX * 0.1f)
                val vy = (height * v.yRatio) + (offsetY * 0.1f)
                val isMatchingCategory = (selectedCategory.contains("توكتوك") && v.type == "توكتوك") ||
                        (selectedCategory.contains("اسكوتر") && v.type == "اسكوتر") ||
                        (selectedCategory.contains("ملاكي") && v.type == "ملاكي")

                val markerColor = when (v.type) {
                    "توكتوك" -> Color(0xFFEA580C)
                    "اسكوتر" -> Color(0xFF0284C7)
                    else -> Color(0xFF059669)
                }

                val size = if (isMatchingCategory) 14f * scale else 10f * scale

                drawCircle(
                    color = markerColor,
                    radius = size,
                    center = Offset(vx, vy)
                )
                drawCircle(
                    color = Color.White,
                    radius = size * 0.4f,
                    center = Offset(vx, vy)
                )
            }
        }

        // أزرار التحكم بالخريطة (تكبير، تصغير، إعادة ضبط الموقع)
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.White,
                shadowElevation = 6.dp
            ) {
                Column {
                    IconButton(
                        onClick = { scale = (scale + 0.25f).coerceAtMost(2.5f) },
                        modifier = Modifier.size(42.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "تكبير الخريطة", tint = NavyDark)
                    }
                    Box(modifier = Modifier.width(32.dp).height(1.dp).background(Color(0xFFE2E8F0)))
                    IconButton(
                        onClick = { scale = (scale - 0.25f).coerceAtLeast(0.7f) },
                        modifier = Modifier.size(42.dp)
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "تصغير الخريطة", tint = NavyDark)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                shape = CircleShape,
                color = EmeraldPrimary,
                shadowElevation = 6.dp
            ) {
                IconButton(
                    onClick = {
                        scale = 1f
                        offsetX = 0f
                        offsetY = 0f
                    },
                    modifier = Modifier.size(42.dp)
                ) {
                    Icon(Icons.Default.MyLocation, contentDescription = "موقعي الحالي", tint = Color.White)
                }
            }
        }

        // شارة معلومات المدينة الحالية والمسافة التقديرية
        Card(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(EmeraldPrimary)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = selectedCity,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = NavyDark
                    )
                    Text(
                        text = "المسافة: ~4.5 كم (12 دقيقة)",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }
        }

        // كارت معلومات المركبة القريبة عند النقر عليها
        selectedVehicle?.let { v ->
            Card(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(20.dp)
                    .fillMaxWidth(0.85f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = NavyDark),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = when (v.type) {
                                "توكتوك" -> "🛺"
                                "اسكوتر" -> "🛵"
                                else -> "🚗"
                            },
                            fontSize = 24.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = v.captainName, color = Color.White, fontWeight = FontWeight.Bold)
                            Text(text = "${v.type} • لوحة: ${v.plateNumber}", color = Color(0xFF94A3B8), fontSize = 12.sp)
                        }
                        Text(text = "⭐ ${v.rating}", color = GoldAccent, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
