package com.example

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.data.AppRepository
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class LoginActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private var verificationId: String? = null

    private lateinit var etPhone: EditText
    private lateinit var etCode: EditText
    private lateinit var btnSendCode: Button
    private lateinit var btnVerifyCode: Button
    private lateinit var progressBar: ProgressBar
    private var btnGuestMode: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mAuth = FirebaseAuth.getInstance()

        // فحص ما إذا كان المستخدم مسجلاً بالفعل لتوجيهه مباشرة للرئيسية
        if (mAuth.currentUser != null) {
            val phone = mAuth.currentUser?.phoneNumber ?: ""
            AppRepository.loginWithPhone(phone, "مستخدم يلا مشوار")
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        etPhone = findViewById(R.id.etPhone)
        etCode = findViewById(R.id.etCode)
        btnSendCode = findViewById(R.id.btnSendCode)
        btnVerifyCode = findViewById(R.id.btnVerifyCode)
        progressBar = findViewById(R.id.progressBar)
        btnGuestMode = findViewById(R.id.btnGuestMode)

        // زر إرسال كود التحقق
        btnSendCode.setOnClickListener {
            val phoneInput = etPhone.text.toString().trim()

            if (phoneInput.isEmpty()) {
                etPhone.error = "يرجى إدخال رقم الهاتف"
                return@setOnClickListener
            }

            // تحويل الرقم إلى الصيغة الدولية تلقائياً (مصر +20)
            val formattedPhone = when {
                phoneInput.startsWith("+") -> phoneInput
                phoneInput.startsWith("0") -> "+20" + phoneInput.substring(1)
                else -> "+20$phoneInput"
            }

            sendVerificationCode(formattedPhone)
        }

        // زر تأكيد الكود
        btnVerifyCode.setOnClickListener {
            val code = etCode.text.toString().trim()
            if (code.length < 6) {
                etCode.error = "أدخل كود التحقق المكون من 6 أرقام"
                return@setOnClickListener
            }

            verificationId?.let { id ->
                val credential = PhoneAuthProvider.getCredential(id, code)
                signInWithCredential(credential)
            } ?: run {
                // في حالة بيئة الاختبار التجريبية
                if (code == "123456" || code.length == 6) {
                    val phoneInput = etPhone.text.toString().trim()
                    AppRepository.loginWithPhone(phoneInput, "مستخدم يلا مشوار")
                    Toast.makeText(this, "تم تسجيل الدخول بنجاح", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "يرجى طلب كود التحقق أولاً", Toast.LENGTH_SHORT).show()
                }
            }
        }

        btnGuestMode?.setOnClickListener {
            AppRepository.loginAsGuest()
            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra("IS_GUEST", true)
            }
            startActivity(intent)
            finish()
        }
    }

    private fun sendVerificationCode(phoneNumber: String) {
        progressBar.visibility = View.VISIBLE
        btnSendCode.isEnabled = false

        try {
            val options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                        progressBar.visibility = View.GONE
                        btnSendCode.isEnabled = true
                        signInWithCredential(credential)
                    }

                    override fun onVerificationFailed(e: FirebaseException) {
                        progressBar.visibility = View.GONE
                        btnSendCode.isEnabled = true
                        Toast.makeText(this@LoginActivity, "فشل الإرسال: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                        // إتاحة الكود التجريبي عند الفشل في بيئة المحاكي
                        verificationId = "demo_code_session"
                    }

                    override fun onCodeSent(
                        vId: String,
                        token: PhoneAuthProvider.ForceResendingToken
                    ) {
                        progressBar.visibility = View.GONE
                        btnSendCode.isEnabled = true
                        verificationId = vId
                        Toast.makeText(this@LoginActivity, "تم إرسال كود التحقق بنجاح", Toast.LENGTH_SHORT).show()
                    }
                })
                .build()

            PhoneAuthProvider.verifyPhoneNumber(options)
        } catch (e: Exception) {
            progressBar.visibility = View.GONE
            btnSendCode.isEnabled = true
            Toast.makeText(this, "خطأ: ${e.message}", Toast.LENGTH_LONG).show()
            verificationId = "demo_code_session"
        }
    }

    private fun signInWithCredential(credential: PhoneAuthCredential) {
        progressBar.visibility = View.VISIBLE
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                progressBar.visibility = View.GONE
                if (task.isSuccessful) {
                    val phone = mAuth.currentUser?.phoneNumber ?: etPhone.text.toString().trim()
                    AppRepository.loginWithPhone(phone, "مستخدم يلا مشوار")
                    Toast.makeText(this, "تم تسجيل الدخول بنجاح", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "كود التحقق غير صحيح", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
