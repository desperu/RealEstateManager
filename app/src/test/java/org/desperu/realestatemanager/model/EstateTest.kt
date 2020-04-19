package org.desperu.realestatemanager.model

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Simple model class test, for Estate data class that's check setter, getter and default parameters.
 */
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
    private val imageList = mutableListOf(Image(), Image())
    private val address = Address()

    @Test
    fun given_emptyEstate_When_createEstate_Then_checkDefaultValues() {
        val estate = Estate()

        assertEquals(estate.id, 0)
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
        assertEquals(estate.imageList, mutableListOf<Image>())
        assertEquals(estate.address, Address())
    }

    @Test
    fun given_estate_When_createEstate_Then_checkValues() {
        val estate = Estate(id, type, price, surfaceArea, roomNumber, description,
                interestPlace, state, saleDate, soldDate, realEstateAgent, imageList, address)

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
        assertEquals(estate.imageList, imageList)
        assertEquals(estate.address, address)
    }
}