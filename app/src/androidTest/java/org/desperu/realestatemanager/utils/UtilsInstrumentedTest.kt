package org.desperu.realestatemanager.utils

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.desperu.realestatemanager.model.Address
import org.desperu.realestatemanager.utils.Utils.getLatLngFromAddress
import org.desperu.realestatemanager.utils.Utils.isInternetAvailable
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UtilsInstrumentedTest {

    // Context of the app under test.
    private val appContext = InstrumentationRegistry.getInstrumentation().context

    @Test
    @Throws(Exception::class)
    fun checkInternetConnexion() {
        val output = isInternetAvailable(appContext)
        assertTrue(output)
    }

    @Test
    fun given_address_When_getFromLocationName_Then_checkResult() {
        val address = Address(streetNumber = 323, streetName = "Mountainview Dr. Brooklyn", postalCode = 11204, city = "New York", country = "United States")
        val expected = listOf(40.603792299999995, -74.1253035)

        val output = getLatLngFromAddress(appContext, address)

        assertEquals(expected, output)
    }
}