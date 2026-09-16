package com.yalla.meshwar.utils

import android.widget.TextView
import com.google.firebase.database.*

object CaptainEarningsManager {

    // 1. إضافة أرباح الرحلة المكتملة للكابتن
    fun completeRideAndAddEarnings(captainId: String, rideFare: Double) {
        val captainRef = FirebaseDatabase.getInstance().getReference("Users").child(captainId)

        captainRef.runTransaction(object : Transaction.Handler {
            override fun doTransaction(mutableData: MutableData): Transaction.Result {
                var currentEarnings = mutableData.child("totalEarnings").getValue(Double::class.java) ?: 0.0
                var currentRides = mutableData.child("totalRidesCount").getValue(Int::class.java) ?: 0
                var weeklyRides = mutableData.child("completedRidesWeek").getValue(Int::class.java) ?: 0

                // تحديث القيم
                currentEarnings += rideFare
                currentRides += 1
                weeklyRides += 1

                mutableData.child("totalEarnings").value = currentEarnings
                mutableData.child("totalRidesCount").value = currentRides
                mutableData.child("completedRidesWeek").value = weeklyRides

                return Transaction.success(mutableData)
            }

            override fun onComplete(error: DatabaseError?, committed: Boolean, snapshot: DataSnapshot?) {
                if (committed) {
                    // فحص إذا كان الكابتن يستحق بوناص 100 ج (عند إتمام 100 رحلة في الأسبوع)
                    CaptainManager.checkWeeklyRewards(captainId, isCaptain = true)
                }
            }
        })
    }

    // 2. الاستماع وإظهار إحصائيات الكابتن لحظياً على الشاشة
    fun listenToCaptainStats(captainId: String, tvEarnings: TextView, tvRides: TextView) {
        val ref = FirebaseDatabase.getInstance().getReference("Users").child(captainId)

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val earnings = snapshot.child("totalEarnings").getValue(Double::class.java) ?: 0.0
                    val rides = snapshot.child("totalRidesCount").getValue(Int::class.java) ?: 0

                    tvEarnings.text = "$earnings ج.م"
                    tvRides.text = "$rides رحلة"
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}
