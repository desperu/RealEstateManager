package org.desperu.realestatemanager.utils

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import junit.framework.TestCase.assertTrue
import org.desperu.realestatemanager.utils.Utils.isInternetAvailable
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UtilsInstrumentedTest {

    @Test
    @Throws(Exception::class)
    fun checkInternetConnexion() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().context
        val output = isInternetAvailable(appContext)
        assertTrue(output)
    }
}