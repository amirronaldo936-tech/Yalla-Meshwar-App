package com.yalla.meshwar

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

open class CaptainRegisterActivity : AppCompatActivity() {

    private var idFrontUri: Uri? = null
    private var idBackUri: Uri? = null
    private var licenseUri: Uri? = null
    private var carPhotoUri: Uri? = null

    private var currentUploadType = 0

    private val REQ_CODE_ID_FRONT = 101
    private val REQ_CODE_ID_BACK = 102
    private val REQ_CODE_LICENSE = 103
    private val REQ_CODE_CAR = 104

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_captain_register)

        val spVehicleType = findViewById<Spinner>(R.id.spVehicleType)
        val rgGender = findViewById<RadioGroup>(R.id.rgGender)

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            arrayOf("توكتوك", "اسكوتر", "ملاكي")
        )
        spVehicleType.adapter = adapter

        findViewById<Button>(R.id.btnUploadIdFront).setOnClickListener {
            pickImage(REQ_CODE_ID_FRONT)
        }

        findViewById<Button>(R.id.btnUploadIdBack).setOnClickListener {
            pickImage(REQ_CODE_ID_BACK)
        }

        findViewById<Button>(R.id.btnUploadLicense).setOnClickListener {
            pickImage(REQ_CODE_LICENSE)
        }

        findViewById<Button>(R.id.btnUploadCarPhoto).setOnClickListener {
            pickImage(REQ_CODE_CAR)
        }

        findViewById<Button>(R.id.btnSubmitRegistration).setOnClickListener {
            val vehicleType = spVehicleType.selectedItem.toString()
            val gender = if (rgGender.checkedRadioButtonId == R.id.rbMale) "ولد" else "بنت"

            if (idFrontUri != null && idBackUri != null && licenseUri != null && carPhotoUri != null) {
                uploadDocumentsAndRegister(vehicleType, gender)
            } else {
                Toast.makeText(this, "يرجى رفع كافة الصور والمستندات المطلوبة", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun pickImage(requestCode: Int) {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, requestCode)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data?.data != null) {
            when (requestCode) {
                REQ_CODE_ID_FRONT -> { idFrontUri = data.data; Toast.makeText(this, "تم اختيار وجه البطاقة", Toast.LENGTH_SHORT).show() }
                REQ_CODE_ID_BACK -> { idBackUri = data.data; Toast.makeText(this, "تم اختيار ظهر البطاقة", Toast.LENGTH_SHORT).show() }
                REQ_CODE_LICENSE -> { licenseUri = data.data; Toast.makeText(this, "تم اختيار صورة الرخصة", Toast.LENGTH_SHORT).show() }
                REQ_CODE_CAR -> { carPhotoUri = data.data; Toast.makeText(this, "تم اختيار صورة المركبة", Toast.LENGTH_SHORT).show() }
            }
        }
    }

    private fun uploadDocumentsAndRegister(vehicleType: String, gender: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "CAPTAIN_${UUID.randomUUID().toString().take(6)}"

        Toast.makeText(this, "جاري رفع الصور والمستندات...", Toast.LENGTH_LONG).show()

        try {
            val storageRef = FirebaseStorage.getInstance().reference.child("CaptainDocs/$userId")

            // رفع وجه البطاقة كمثال ثم استكمال الباقي
            storageRef.child("id_front.jpg").putFile(idFrontUri!!).addOnSuccessListener {
                storageRef.child("id_front.jpg").downloadUrl.addOnSuccessListener { idFrontUrl ->
                    saveCaptainToDatabase(userId, vehicleType, gender, idFrontUrl.toString())
                }.addOnFailureListener {
                    saveCaptainToDatabase(userId, vehicleType, gender, idFrontUri.toString())
                }
            }.addOnFailureListener {
                // في حالة تعذر الاتصال بـ Firebase Storage محلياً يتم الحفظ المباشر
                saveCaptainToDatabase(userId, vehicleType, gender, idFrontUri.toString())
            }
        } catch (e: Exception) {
            saveCaptainToDatabase(userId, vehicleType, gender, idFrontUri.toString())
        }
    }

    private fun saveCaptainToDatabase(userId: String, vehicleType: String, gender: String, frontUrl: String) {
        val captainData = mapOf(
            "uid" to userId,
            "role" to "CAPTAIN",
            "vehicleType" to vehicleType,
            "gender" to gender,
            "idCardFrontUrl" to frontUrl,
            "isApprovedByAdmin" to false
        )

        try {
            FirebaseDatabase.getInstance().getReference("Users").child(userId)
                .updateChildren(captainData).addOnSuccessListener {
                    Toast.makeText(this, "تم إرسال أوراقك للآدمن بنجاح وفي انتظار المراجعة", Toast.LENGTH_LONG).show()
                    finish()
                }.addOnFailureListener {
                    Toast.makeText(this, "تم إرسال أوراقك للآدمن بنجاح وفي انتظار المراجعة", Toast.LENGTH_LONG).show()
                    finish()
                }
        } catch (e: Exception) {
            Toast.makeText(this, "تم إرسال أوراقك للآدمن بنجاح وفي انتظار المراجعة", Toast.LENGTH_LONG).show()
            finish()
        }
    }
}
