package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.PermIdentity
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppRepository
import com.example.ui.theme.CleanWhite
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.NavyDark
import com.example.ui.theme.OffWhiteBg

@Composable
fun CaptainRegisterScreen(
    onRegisterSuccess: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var captainName by remember { mutableStateOf("") }
    var captainPhone by remember { mutableStateOf("") }
    var selectedVehicleType by remember { mutableStateOf("TUKTUK") } // "TUKTUK", "SCOOTER", "MALAKI"
    var vehicleModel by remember { mutableStateOf("") }
    var vehiclePlate by remember { mutableStateOf("") }

    // محاكاة رفع الوثائق
    var idFrontUploaded by remember { mutableStateOf(false) }
    var idBackUploaded by remember { mutableStateOf(false) }
    var licenseUploaded by remember { mutableStateOf(false) }
    var carPhotoUploaded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(OffWhiteBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // شريط العنوان
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "رجوع",
                        tint = NavyDark
                    )
                }
                Text(
                    text = "انضم ككابتن في يلا مشوار",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyDark
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "ابدأ العمل وحقق دخلاً ممتازاً مع بوناص أسبوعي 100 ج.م عند إتمام 100 رحلة!",
                fontSize = 13.sp,
                color = Color(0xFF4B5563),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(18.dp))

            // كارت البيانات الشخصية والمركبة
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CleanWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "البيانات الشخصية والمركبة",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = NavyDark
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = captainName,
                        onValueChange = { captainName = it },
                        label = { Text("الاسم رباعي (كما في البطاقة)") },
                        leadingIcon = { Icon(Icons.Default.PermIdentity, contentDescription = null, tint = EmeraldPrimary) },
                        modifier = Modifier.fillMaxWidth().testTag("etCaptainName"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = captainPhone,
                        onValueChange = { captainPhone = it },
                        label = { Text("رقم الهاتف للتواصل") },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = EmeraldPrimary) },
                        modifier = Modifier.fillMaxWidth().testTag("etCaptainPhone"),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "نوع المركبة:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = NavyDark
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        VehicleTypeSelectorChip(
                            label = "توكتوك 🛺",
                            isSelected = selectedVehicleType == "TUKTUK",
                            modifier = Modifier.weight(1f)
                        ) { selectedVehicleType = "TUKTUK" }

                        VehicleTypeSelectorChip(
                            label = "اسكوتر 🛵",
                            isSelected = selectedVehicleType == "SCOOTER",
                            modifier = Modifier.weight(1f)
                        ) { selectedVehicleType = "SCOOTER" }

                        VehicleTypeSelectorChip(
                            label = "ملاكي 🚗",
                            isSelected = selectedVehicleType == "MALAKI",
                            modifier = Modifier.weight(1f)
                        ) { selectedVehicleType = "MALAKI" }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = vehicleModel,
                        onValueChange = { vehicleModel = it },
                        label = { Text("موديل وسنة الصنع (مثال: توكتوك بيادجو 2023)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = vehiclePlate,
                        onValueChange = { vehiclePlate = it },
                        label = { Text("رقم اللوحة المعدنية (مثال: ٤٨٢١ ط ع س)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // كارت رفع الأوراق والمستندات المطلوبة (البطاقة، الرخصة، السيارة)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CleanWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "المستندات المطلوبة للمراجعة",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = NavyDark
                    )
                    Text(
                        text = "يرجى تصوير أو إرفاق المستندات واضحة للموافقة السريعة:",
                        fontSize = 12.sp,
                        color = Color(0xFF6B7280)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    DocumentUploadRow(
                        title = "بطاقة الرقم القومي (الوجه الأمامي)",
                        isUploaded = idFrontUploaded
                    ) {
                        idFrontUploaded = !idFrontUploaded
                        Toast.makeText(context, if (idFrontUploaded) "تم إرفاق صورة وجه البطاقة" else "تم حذف الصورة", Toast.LENGTH_SHORT).show()
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    DocumentUploadRow(
                        title = "بطاقة الرقم القومي (الوجه الخلفي)",
                        isUploaded = idBackUploaded
                    ) {
                        idBackUploaded = !idBackUploaded
                        Toast.makeText(context, if (idBackUploaded) "تم إرفاق صورة ظهر البطاقة" else "تم حذف الصورة", Toast.LENGTH_SHORT).show()
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    DocumentUploadRow(
                        title = "رخصة القيادة المهنية أو الخاصة سارية",
                        isUploaded = licenseUploaded
                    ) {
                        licenseUploaded = !licenseUploaded
                        Toast.makeText(context, if (licenseUploaded) "تم إرفاق صورة رخصة القيادة" else "تم حذف الصورة", Toast.LENGTH_SHORT).show()
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    DocumentUploadRow(
                        title = "صورة المركبة (توكتوك / اسكوتر / سيارة)",
                        isUploaded = carPhotoUploaded
                    ) {
                        carPhotoUploaded = !carPhotoUploaded
                        Toast.makeText(context, if (carPhotoUploaded) "تم إرفاق صورة المركبة" else "تم حذف الصورة", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // زر تقديم الطلب
            Button(
                onClick = {
                    if (captainName.isNotBlank() && captainPhone.isNotBlank()) {
                        AppRepository.registerNewCaptain(
                            name = captainName,
                            phone = captainPhone,
                            vehicleType = selectedVehicleType,
                            model = if (vehicleModel.isNotBlank()) vehicleModel else "مركبة حديثة",
                            plate = if (vehiclePlate.isNotBlank()) vehiclePlate else "١٢٣٤ أ ب ج"
                        )
                        Toast.makeText(
                            context,
                            "تم إرسال أوراقك بنجاح وجاري مراجعتها من إدارة يلا مشوار!",
                            Toast.LENGTH_LONG
                        ).show()
                        onRegisterSuccess()
                    } else {
                        Toast.makeText(context, "يرجى كتابة الاسم ورقم الهاتف على الأقل", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("btnSubmitCaptainRegister"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
            ) {
                Text(
                    text = "إرسال طلب الانضمام للإدارة",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = CleanWhite
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun VehicleTypeSelectorChip(
    label: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) EmeraldPrimary else OffWhiteBg,
        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, EmeraldDark) else null,
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier.padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) CleanWhite else NavyDark
            )
        }
    }
}

@Composable
fun DocumentUploadRow(
    title: String,
    isUploaded: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isUploaded) EmeraldPrimary.copy(alpha = 0.1f) else OffWhiteBg)
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Icon(
                imageVector = if (isUploaded) Icons.Default.CheckCircle else Icons.Default.CameraAlt,
                contentDescription = null,
                tint = if (isUploaded) EmeraldDark else Color(0xFF6B7280),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = if (isUploaded) FontWeight.Bold else FontWeight.Medium,
                color = if (isUploaded) EmeraldDark else NavyDark
            )
        }

        Surface(
            shape = RoundedCornerShape(8.dp),
            color = if (isUploaded) EmeraldDark else Color(0xFFE2E8F0)
        ) {
            Text(
                text = if (isUploaded) "تم الإرفاق ✓" else "إرفاق صورة",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isUploaded) CleanWhite else Color(0xFF334155),
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}
