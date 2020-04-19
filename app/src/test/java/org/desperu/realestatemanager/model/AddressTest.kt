package org.desperu.realestatemanager.model

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Simple model class test, for Address data class that's check setter, getter and default parameters.
 */
class AddressTest {

    private val estateId = 0L
    private val streetNumber = 323
    private val streetName = "Mountainview Dr. Brooklyn"
    private val flatBuilding = "flat 45"
    private val postalCode = 11204
    private val city = "New York"
    private val country = "United States"

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
    }

    @Test
    fun given_address_When_createAddress_Then_checkValues() {
        val address = Address(estateId, streetNumber, streetName, flatBuilding, postalCode, city, country)

        assertEquals(address.estateId, estateId)
        assertEquals(address.streetNumber, streetNumber)
        assertEquals(address.streetName, streetName)
        assertEquals(address.flatBuilding, flatBuilding)
        assertEquals(address.postalCode, postalCode)
        assertEquals(address.city, city)
        assertEquals(address.country, country)
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

        assertEquals(address.estateId, estateId)
        assertEquals(address.streetNumber, streetNumber)
        assertEquals(address.streetName, streetName)
        assertEquals(address.flatBuilding, flatBuilding)
        assertEquals(address.postalCode, postalCode)
        assertEquals(address.city, city)
        assertEquals(address.country, country)
    }
}