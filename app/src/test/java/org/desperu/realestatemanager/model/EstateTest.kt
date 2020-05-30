package org.desperu.realestatemanager.model

import android.content.ContentValues
import org.desperu.realestatemanager.utils.EQUALS
import org.desperu.realestatemanager.utils.NOT_EQUALS
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Simple model class test, for Estate data class that's check setter, getter and default parameters.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28], manifest= Config.NONE)
class EstateTest {

    private val id = 1L
    private val type = "Flat"
    private val price = 1000000L
    private val surfaceArea = 200
    private val roomNumber = 10
    private val description = "A beautiful Flat"
    private val interestPlace = "Park"
    private val state = "For Sale"
    private val saleDate = "15/04/2020"
    private val soldDate = ""
    private val realEstateAgent = "Desperu"
    private val createdTime = 1246464684L
    private val imageList = mutableListOf(Image(), Image())
    private val address = Address()

    @Test
    fun given_emptyEstate_When_createEstate_Then_checkDefaultValues() {
        val estate = Estate()

        assertEquals(estate.id, 0L)
        assertEquals(estate.type, "")
        assertEquals(estate.price, 0L)
        assertEquals(estate.surfaceArea, 0)
        assertEquals(estate.roomNumber, 0)
        assertEquals(estate.description, "")
        assertEquals(estate.interestPlaces, "")
        assertEquals(estate.state, "")
        assertEquals(estate.saleDate, "")
        assertEquals(estate.soldDate, "")
        assertEquals(estate.realEstateAgent, "")
        assertEquals(estate.createdTime, 0L)
        assertEquals(estate.imageList, mutableListOf<Image>())
        assertEquals(estate.address, Address())
    }

    @Test
    fun given_estate_When_createEstate_Then_checkValues() {
        val estate = Estate(id, type, price, surfaceArea, roomNumber, description, interestPlace,
                state, saleDate, soldDate, realEstateAgent, createdTime, imageList, address)

        assertEquals(estate.id, id)
        assertEquals(estate.type, type)
        assertEquals(estate.price, price)
        assertEquals(estate.surfaceArea, surfaceArea)
        assertEquals(estate.roomNumber, roomNumber)
        assertEquals(estate.description, description)
        assertEquals(estate.interestPlaces, interestPlace)
        assertEquals(estate.state, state)
        assertEquals(estate.saleDate, saleDate)
        assertEquals(estate.soldDate, soldDate)
        assertEquals(estate.realEstateAgent, realEstateAgent)
        assertEquals(estate.createdTime, createdTime)
        assertEquals(estate.imageList, imageList)
        assertEquals(estate.address, address)
    }

    @Test
    fun given_emptyEstate_When_setEstateValues_Then_checkValues() {
        val estate = Estate()

        estate.id = id
        estate.type = type
        estate.price = price
        estate.surfaceArea =surfaceArea
        estate.roomNumber = roomNumber
        estate.description = description
        estate.interestPlaces = interestPlace
        estate.state = state
        estate.saleDate = saleDate
        estate.soldDate = soldDate
        estate.realEstateAgent = realEstateAgent
        estate.createdTime = createdTime
        estate.imageList = imageList
        estate.address = address

        assertEquals(estate.id, id)
        assertEquals(estate.type, type)
        assertEquals(estate.price, price)
        assertEquals(estate.surfaceArea, surfaceArea)
        assertEquals(estate.roomNumber, roomNumber)
        assertEquals(estate.description, description)
        assertEquals(estate.interestPlaces, interestPlace)
        assertEquals(estate.state, state)
        assertEquals(estate.saleDate, saleDate)
        assertEquals(estate.soldDate, soldDate)
        assertEquals(estate.realEstateAgent, realEstateAgent)
        assertEquals(estate.createdTime, createdTime)
        assertEquals(estate.imageList, imageList)
        assertEquals(estate.address, address)
    }

    @Test
    fun given_estate_When_compareTo_Then_checkEquals() {
        val estate = Estate()
        estate.imageList = imageList
        estate.address = address

        assertEquals(EQUALS, estate.compareTo(estate))
    }

    @Test
    fun given_originalEstate_When_compareTo_Then_checkNotEquals() {
        val originalEstate = Estate()
        originalEstate.imageList = imageList
        originalEstate.address = address

        // Change an estate field value, not in child object.
        val finalEstate = originalEstate.copy()
        finalEstate.price = 1200000L

        assertEquals(NOT_EQUALS, originalEstate.compareTo(finalEstate))

        // Change an image field value in estate image list.
        val finalEstate2 = originalEstate.copy()
        finalEstate2.imageList = originalEstate.imageList.map { it.copy() }.toMutableList()
        finalEstate2.imageList[0].isPrimary = true

        assertEquals(NOT_EQUALS, originalEstate.compareTo(finalEstate2))

        // Change an address field value in estate address.
        val finalEstate3 = originalEstate.copy()
        finalEstate3.address = originalEstate.address.copy()
        finalEstate3.address.streetNumber = 23

        assertEquals(NOT_EQUALS, originalEstate.compareTo(finalEstate3))
    }

    @Test
    fun given_contentValues_When_fromContentValues_Then_checkEstateFields() {
        val estate = Estate(id, type, price, surfaceArea, roomNumber, description, interestPlace,
                state, saleDate, soldDate, realEstateAgent, createdTime, mutableListOf(), address)

        val contentValues = ContentValues()

        assertNotNull(contentValues)

        contentValues.put("id", id)
        contentValues.put("type", type)
        contentValues.put("price", price)
        contentValues.put("surfaceArea", surfaceArea)
        contentValues.put("roomNumber", roomNumber)
        contentValues.put("description", description)
        contentValues.put("interestPlaces", interestPlace)
        contentValues.put("state", state)
        contentValues.put("saleDate", saleDate)
        contentValues.put("soldDate", soldDate)
        contentValues.put("realEstateAgent", realEstateAgent)
        contentValues.put("createdTime", createdTime)

        val output = Estate().fromContentValues(contentValues)

        assertEquals(estate, output)
    }
}