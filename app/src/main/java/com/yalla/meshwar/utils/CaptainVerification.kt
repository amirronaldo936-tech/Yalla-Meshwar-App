package com.yalla.meshwar.utils

import android.view.View
import android.widget.ImageView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

object CaptainVerification {

    /**
     * التحقق مما إذا كان الكابتن موثقاً ومعتمداً رسمياً من الإدارة
     */
    fun isCaptainVerified(isApprovedByAdmin: Boolean): Boolean {
        return isApprovedByAdmin
    }

    /**
     * تفعيل أو إخفاء درع التوثيق الأخضر (ic_verified_shield) على صورة/أيقونة الكابتن
     */
    fun applyVerificationBadge(badgeView: ImageView, isApproved: Boolean) {
        if (isApproved) {
            badgeView.visibility = View.VISIBLE
        } else {
            badgeView.visibility = View.GONE
        }
    }

    /**
     * فحص حالة توثيق الكابتن لحظياً من Firebase Realtime Database
     */
    fun checkCaptainVerificationInFirebase(captainId: String, onResult: (Boolean) -> Unit) {
        val captainRef = FirebaseDatabase.getInstance().getReference("Users").child(captainId)
        captainRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val isApproved = snapshot.child("isApprovedByAdmin").getValue(Boolean::class.java) ?: false
                onResult(isApproved)
            }

            override fun onCancelled(error: DatabaseError) {
                onResult(false)
            }
        })
    }
}
