package com.yalla.meshwar.utils

import com.example.data.RewardStatus

object CaptainManager {
    fun reportCaptain(captainId: String): String {
        return com.example.utils.CaptainManager.reportCaptain(captainId)
    }

    fun checkWeeklyRewards(userId: String, isCaptain: Boolean): RewardStatus {
        return com.example.utils.CaptainManager.checkWeeklyRewards(userId, isCaptain)
    }

    fun unbanCaptain(captainId: String) {
        com.example.utils.CaptainManager.unbanCaptain(captainId)
    }

    fun updateCaptainStatus(captainId: String, approved: Boolean): String {
        return com.example.utils.CaptainManager.updateCaptainStatus(captainId, approved)
    }
}
