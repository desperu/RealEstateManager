package org.desperu.realestatemanager.service

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.runBlocking
import org.desperu.realestatemanager.model.Address
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GeocoderServiceInstrumentedTest {

    // Context of the app under test.
    private val appContext = InstrumentationRegistry.getInstrumentation().context

    @Test
    fun given_address_When_getLatLngFromAddress_Then_checkResult() = runBlocking {
        val address = Address(streetNumber = 323, streetName = "Mountainview Dr. Brooklyn", postalCode = 11204, city = "New York", country = "United States")
        val expected = listOf(40.603792299999995, -74.1253035)

        val output = GeocoderServiceImpl(appContext).getLatLngFromAddress(address)

        assertEquals(expected, output)
    }

    @Test
    fun given_emptyAddress_When_getLatLngFromAddress_Then_checkEmptyResult() = runBlocking {
        val address = Address()

        val output = GeocoderServiceImpl(appContext).getLatLngFromAddress(address)

        assertTrue(output.isEmpty())
    }
}