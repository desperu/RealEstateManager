package org.desperu.realestatemanager.model

import android.content.ContentValues
import android.os.Build
import org.desperu.realestatemanager.utils.EQUALS
import org.desperu.realestatemanager.utils.NOT_EQUALS
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Simple model class test, for Address data class that's check setter, getter and default parameters.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O])
class AddressTest {

    private val estateId = 0L
    private val streetNumber = 323
    private val streetName = "Mountainview Dr. Brooklyn"
    private val flatBuilding = "flat 45"
    private val postalCode = 11204
    private val city = "New York"
    private val country = "United States"
    private val latitude = 25.148569
    private val longitude = 102.659874

    @Test
    fun given_emptyAddress_When_createAddress_Then_checkDefaultValues() {
        val address = Address()

        assertEquals(address.estateId, 0L)
        assertEquals(address.streetNumber, 0)
        assertEquals(address.streetName, "")
        assertEquals(address.flatBuilding, "")
        assertEquals(address.postalCode, 0)
        assertEquals(address.city, "")
        assertEquals(address.country, "")
        assertEquals(address.latitude, 0.0, 0.0)
        assertEquals(address.longitude, 0.0, 0.0)
    }

    @Test
    fun given_address_When_createAddress_Then_checkValues() {
        val address = Address(estateId, streetNumber, streetName, flatBuilding,
                postalCode, city, country, latitude, longitude)

        assertEquals(address.estateId, estateId)
        assertEquals(address.streetNumber, streetNumber)
        assertEquals(address.streetName, streetName)
        assertEquals(address.flatBuilding, flatBuilding)
        assertEquals(address.postalCode, postalCode)
        assertEquals(address.city, city)
        assertEquals(address.country, country)
        assertEquals(address.latitude, latitude, 0.0)
        assertEquals(address.longitude, longitude, 0.0)
    }

    @Test
    fun given_emptyAddress_When_setAddressValues_Then_checkValues() {
        val address = Address()

        address.estateId = estateId
        address.streetNumber = streetNumber
        address.streetName = streetName
        address.flatBuilding = flatBuilding
        address.postalCode = postalCode
        address.city = city
        address.country = country
        address.latitude = latitude
        address.longitude = longitude

        assertEquals(address.estateId, estateId)
        assertEquals(address.streetNumber, streetNumber)
        assertEquals(address.streetName, streetName)
        assertEquals(address.flatBuilding, flatBuilding)
        assertEquals(address.postalCode, postalCode)
        assertEquals(address.city, city)
        assertEquals(address.country, country)
        assertEquals(address.latitude, latitude, 0.0)
        assertEquals(address.longitude, longitude, 0.0)
    }

    @Test
    fun given_address_When_compareTo_Then_checkEquals() {
        val address = Address()

        assertEquals(EQUALS, address.compareTo(address))
    }

    @Test
    fun given_originalAddress_When_compareTo_Then_checkNotEquals() {
        val originalAddress = Address()

        val finalAddress = originalAddress.copy()
        finalAddress.streetNumber = 23

        assertEquals(NOT_EQUALS, originalAddress.compareTo(finalAddress))
    }

    @Test
    fun given_contentValues_When_fromContentValues_Then_checkAddressFields() {
        val address = Address(estateId, streetNumber, streetName, flatBuilding,
                postalCode, city, country, latitude, longitude)

        val contentValues = ContentValues()
        Assert.assertNotNull(contentValues)

        contentValues.put("estateId", estateId)
        contentValues.put("streetNumber", streetNumber)
        contentValues.put("streetName", streetName)
        contentValues.put("flatBuilding", flatBuilding)
        contentValues.put("postalCode", postalCode)
        contentValues.put("city", city)
        contentValues.put("country", country)
        contentValues.put("latitude", latitude)
        contentValues.put("longitude", longitude)

        val output = Address().fromContentValues(contentValues)

        assertEquals(address, output)
    }
}