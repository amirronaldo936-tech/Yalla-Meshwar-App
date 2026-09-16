package com.yalla.meshwar

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.yalla.meshwar.utils.FareCalculator
import com.yalla.meshwar.utils.NotificationHelper

open class MainActivity : AppCompatActivity() {

    private lateinit var servicesContainer: LinearLayout
    private lateinit var btnTuktuk: Button
    private lateinit var btnScooter: Button
    private lateinit var btnMalaki: Button
    private lateinit var btnConfirm: Button
    private var selectedVehicle: String = "MALAKI"
    private var selectedService: String = "مشوار"

    // مسجل طلب الأذونات المتعددة
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val notificationGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions[Manifest.permission.POST_NOTIFICATIONS] ?: false
        } else true

        if (fineLocationGranted) {
            Toast.makeText(this, "تم تفعيل إذن الموقع بنجاح 📍", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "يرجى الموافقة على إذن الموقع لتحديد نقطة الانطلاق", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. التحقق الفوري عند فتح البرنامج إذا كان المستخدم مسجلاً أم لا
        val currentUser = try {
            FirebaseAuth.getInstance().currentUser
        } catch (e: Exception) {
            null
        }
        val isGuest = intent.getBooleanExtra("IS_GUEST", false)

        if (currentUser == null && !isGuest) {
            try {
                val loginIntent = Intent(this, Class.forName("com.yalla.meshwar.LoginActivity"))
                startActivity(loginIntent)
                finish()
                return
            } catch (e: Exception) {
                try {
                    val loginIntent = Intent(this, Class.forName("com.example.LoginActivity"))
                    startActivity(loginIntent)
                    finish()
                    return
                } catch (ignored: Exception) {}
            }
        }

        setContentView(R.layout.activity_main)

        // 1. إنشاء قناة الإشعارات
        NotificationHelper.createNotificationChannel(this)

        // 2. طلب الأذونات عند الفتح
        checkAndRequestPermissions()

        servicesContainer = findViewById(R.id.servicesContainer)
        btnTuktuk = findViewById(R.id.btnTuktuk)
        btnScooter = findViewById(R.id.btnScooter)
        btnMalaki = findViewById(R.id.btnMalaki)
        btnConfirm = findViewById(R.id.btnConfirm)

        // التهيئة الافتراضية عند الفتح (خدمات الملاكي)
        selectVehicle("MALAKI")

        btnTuktuk.setOnClickListener { selectVehicle("TUKTUK") }
        btnScooter.setOnClickListener { selectVehicle("SCOOTER") }
        btnMalaki.setOnClickListener { selectVehicle("MALAKI") }

        val chkQuietRide = findViewById<CheckBox>(R.id.chkQuietRide)

        val btnSettings = findViewById<ImageButton>(R.id.btnSettings)
        btnSettings.setOnClickListener {
            val options = arrayOf("🛡️ إرشادات الأمان وقوانين التطبيق", "🚪 تسجيل الخروج")
            
            val builder = AlertDialog.Builder(this)
            builder.setTitle("الإعدادات")
            builder.setItems(options) { dialog, which ->
                when (which) {
                    0 -> showSafetyDialog() // عرض نافذة إرشادات الأمان
                    1 -> logoutUser()       // تسجيل الخروج
                }
            }
            builder.show()
        }

        btnConfirm.setOnClickListener {
            val quietModeText = if (chkQuietRide.isChecked) " (رحلة هادئة 🤫)" else ""
            val sampleDistance = 5.0
            val estimatedFare = FareCalculator.calculateCheapestFare(sampleDistance, selectedVehicle)
            val (captainEarnings, appCommission) = FareCalculator.getFareBreakdown(estimatedFare)
            Toast.makeText(
                this,
                "تم تأكيد $selectedVehicle ($selectedService)$quietModeText\nالأجرة المقدرة: $estimatedFare ج.م (تضمن أرخص سعر بنسبة 12% عمولة فقط)",
                Toast.LENGTH_LONG
            ).show()
        }

        val btnShareRide = findViewById<Button>(R.id.btnShareRide)
        val btnSOS = findViewById<Button>(R.id.btnSOS)

        // 1. مشاركة الرحلة الحية مع الأهل
        btnShareRide.setOnClickListener {
            val currentRideId = "RIDE_12345" // معرف الرحلة الحالية
            val captainName = "أحمد المحمدي" // اسم الكابتن
            val vehicleNumber = "س ج ب 1234" // رقم المركبة

            // رابط تتبع الخريطة الحي
            val trackingLink = "https://yallameshwar.app/track?rideId=$currentRideId"

            val shareMessage = """
                🚨 أهلاً، أنا في رحلة الآن عبر تطبيق "يلا مشوار".

                👤 اسم الكابتن: $captainName
                🚗 رقم المركبة: $vehicleNumber
                📍 رابط التتبع المباشر للرحلة:
                $trackingLink

                اطمئنوا عليّ!
            """.trimIndent()

            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, shareMessage)
                type = "text/plain"
            }

            val shareIntent = Intent.createChooser(sendIntent, "مشاركة تفاصيل الرحلة عبر:")
            startActivity(shareIntent)
        }

        // 2. زر الطوارئ والنجدة السريع (SOS)
        btnSOS.setOnClickListener {
            // الاتصال الفوري بشرطة النجدة (122) أو رقم الدعم الفني للتطبيق
            val emergencyNumber = "122"
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$emergencyNumber")
            }
            startActivity(intent)
            Toast.makeText(this, "جاري تحويلك لشرطة النجدة للطوارئ", Toast.LENGTH_LONG).show()
        }
    }

    private fun selectVehicle(type: String) {
        selectedVehicle = type
        // إعادة ضبط ألوان الأزرار الرئيسية
        resetVehicleButtonStyles()

        when (type) {
            "TUKTUK" -> {
                btnTuktuk.setBackgroundColor(Color.parseColor("#10B981"))
                btnTuktuk.setTextColor(Color.WHITE)
                updateServiceBoxes(arrayOf("مشوار", "طلبات"))
            }
            "SCOOTER" -> {
                btnScooter.setBackgroundColor(Color.parseColor("#10B981"))
                btnScooter.setTextColor(Color.WHITE)
                updateServiceBoxes(arrayOf("مشوار", "طلبات"))
            }
            "MALAKI" -> {
                btnMalaki.setBackgroundColor(Color.parseColor("#10B981"))
                btnMalaki.setTextColor(Color.WHITE)
                updateServiceBoxes(arrayOf("مشوار", "سفر", "أفراح", "مطار"))
            }
        }
    }

    private fun resetVehicleButtonStyles() {
        val defaultBg = Color.parseColor("#F1F5F9")
        val defaultText = Color.parseColor("#0F172A")

        btnTuktuk.setBackgroundColor(defaultBg)
        btnTuktuk.setTextColor(defaultText)
        btnScooter.setBackgroundColor(defaultBg)
        btnScooter.setTextColor(defaultText)
        btnMalaki.setBackgroundColor(defaultBg)
        btnMalaki.setTextColor(defaultText)
    }

    // إنشاء وتغيير المربعات ديناميكياً بوضوح وأناقة
    private fun updateServiceBoxes(services: Array<String>) {
        servicesContainer.removeAllViews()
        selectedService = services[0]

        for (i in services.indices) {
            val serviceName = services[i]
            val button = Button(this)

            val params = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                110
            )
            params.setMargins(8, 0, 8, 0)
            button.layoutParams = params

            button.text = serviceName
            button.textSize = 14f
            button.isAllCaps = false

            // التمييز البصري للمربع المحدد
            if (i == 0) {
                button.setBackgroundColor(Color.parseColor("#10B981"))
                button.setTextColor(Color.WHITE)
            } else {
                button.setBackgroundColor(Color.parseColor("#E2E8F0"))
                button.setTextColor(Color.parseColor("#0F172A"))
            }

            button.setOnClickListener {
                for (j in 0 until servicesContainer.childCount) {
                    val child = servicesContainer.getChildAt(j) as Button
                    child.setBackgroundColor(Color.parseColor("#E2E8F0"))
                    child.setTextColor(Color.parseColor("#0F172A"))
                }
                button.setBackgroundColor(Color.parseColor("#10B981"))
                button.setTextColor(Color.WHITE)
                selectedService = serviceName
                Toast.makeText(this, "تم اختيار خدمة: $selectedService", Toast.LENGTH_SHORT).show()
            }

            servicesContainer.addView(button)
        }
    }

    fun addCaptainToFavorites(userId: String, captainId: String) {
        val favRef = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("FavoriteCaptains")
        favRef.child(captainId).setValue(true)
    }

    fun checkHighDemandZone(lat: Double, lng: Double): Boolean {
        // التحقق من كثافة الطلبات في المنطقة لإرسال تنبيه للكابتن
        return true
    }

    // دالة عرض نافذة التوعية بالأمان
    private fun showSafetyDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_safety_guidelines, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        dialogView.findViewById<Button>(R.id.btnCloseSafety).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    // دالة تسجيل الخروج والتحويل لشاشة الدخول
    private fun logoutUser() {
        FirebaseAuth.getInstance().signOut()
        try {
            val intent = Intent(this, Class.forName("com.yalla.meshwar.LoginActivity"))
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        } catch (e: Exception) {
            try {
                val intent = Intent(this, Class.forName("com.example.LoginActivity"))
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            } catch (ignored: Exception) {
                finish()
            }
        }
    }

    private fun checkAndRequestPermissions() {
        val permissionsToRequest = mutableListOf<String>()

        // فحص إذن الموقع
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
            permissionsToRequest.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }

        // فحص إذن الإشعارات لأندرويد 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        // إرسال طلب الأذونات دفعة واحدة
        if (permissionsToRequest.isNotEmpty()) {
            requestPermissionLauncher.launch(permissionsToRequest.toTypedArray())
        }
    }
}
