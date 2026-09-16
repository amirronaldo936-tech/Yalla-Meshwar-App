package com.example.utils

import com.example.data.AppRepository
import com.example.data.RewardStatus

object CaptainManager {

    // تسجيل بلوك للكابتن وحظره تلقائياً عند التكرار 3 مرات
    fun reportCaptain(captainId: String): String {
        return AppRepository.reportCaptain(captainId)
    }

    // تحقق من مكافأة العميل (59 رحلة = رحلة مجانية) ومكافأة الكابتن (100 رحلة = 100 ج بوناص)
    fun checkWeeklyRewards(userId: String, isCaptain: Boolean): RewardStatus {
        return AppRepository.checkWeeklyRewards(userId, isCaptain)
    }

    // فك حظر الكابتن بواسطة الإدارة
    fun unbanCaptain(captainId: String) {
        AppRepository.unbanCaptain(captainId)
    }

    // تحديث موافقة الإدارة على أوراق الكابتن
    fun updateCaptainStatus(captainId: String, approved: Boolean): String {
        return AppRepository.updateCaptainApproval(captainId, approved)
    }
}
