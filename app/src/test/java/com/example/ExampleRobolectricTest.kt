package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.AppRepository
import com.example.utils.CaptainManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("يلا مشوار", appName)
    }

    @Test
    fun `test weekly reward logic for customer 59 rides`() {
        // العميل عند 59 رحلة يحصل على رحلة مجانية
        val statusBefore = CaptainManager.checkWeeklyRewards("user_101", isCaptain = false)
        assertEquals(58, statusBefore.currentCount)
        assertEquals(59, statusBefore.targetCount)
        assertEquals(false, statusBefore.hasReward)

        // إنهاء رحلة ترفع الرصيد إلى 59
        AppRepository.createRideRequest(
            serviceType = "مشوار",
            vehicleCategory = "توكتوك",
            pickupName = "المحلة",
            destinationName = "طنطا",
            pickupLat = 30.9,
            pickupLng = 31.1,
            destLat = 30.7,
            destLng = 31.0,
            priceOffer = 30.0,
            paymentMethod = "CASH"
        )
        val result = AppRepository.completeRide()
        assertTrue(result.first)

        val statusAfter = CaptainManager.checkWeeklyRewards("user_101", isCaptain = false)
        assertEquals(59, statusAfter.currentCount)
        assertEquals(true, statusAfter.hasReward)
    }

    @Test
    fun `test captain auto ban on 3 blocks`() {
        // حظر الكابتن تلقائياً عند وصول البلاغات إلى 3
        val capId = "cap_001"
        CaptainManager.reportCaptain(capId) // block 1
        CaptainManager.reportCaptain(capId) // block 2
        val msg = CaptainManager.reportCaptain(capId) // block 3 -> triggers 24h ban!

        assertTrue(msg.contains("تم حظره تلقائياً"))
        val captain = AppRepository.captains.value.first { it.uid == capId }
        assertTrue(captain.isBanned)
        assertEquals(3, captain.blockCount)
    }

    @Test
    fun `test egyptian phone number international formatting`() {
        val formatPhone = { input: String ->
            when {
                input.startsWith("+") -> input
                input.startsWith("0") -> "+20" + input.substring(1)
                else -> "+20$input"
            }
        }
        assertEquals("+201093283811", formatPhone("01093283811"))
        assertEquals("+201093283811", formatPhone("+201093283811"))
        assertEquals("+201112223334", formatPhone("1112223334"))
    }
}
