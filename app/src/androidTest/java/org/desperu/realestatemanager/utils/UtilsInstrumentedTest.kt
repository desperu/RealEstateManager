package org.desperu.realestatemanager.utils

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import junit.framework.TestCase.assertTrue
import org.desperu.realestatemanager.utils.Utils.isInternetAvailable
import org.desperu.realestatemanager.utils.Utils.isLocationEnabled
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UtilsInstrumentedTest {

    // Context of the app under test.
    private val context = InstrumentationRegistry.getInstrumentation().context

    @Test
    @Throws(Exception::class)
    fun checkInternetConnexion() {
        val output = isInternetAvailable(context)
        assertTrue(output)
    }

    @Test
    @Throws(Exception::class)
    fun checkLocationStatus() {
        val output = isLocationEnabled(context)
        assertTrue(output)
    }
}