package org.desperu.realestatemanager.model

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Simple model class test, for Image data class that's check setter, getter and default parameters.
 */
class ImageTest {

    private val id = 0L
    private val estateId = 0L
    private val imageUri = "an uri"
    private val isPrimary = true
    private val description = "bathroom"

    @Test
    fun given_emptyImage_When_createImage_Then_checkDefaultValues() {
        val image = Image()

        assertEquals(image.id, 0L)
        assertEquals(image.estateId, 0L)
        assertEquals(image.imageUri, "")
        assertEquals(image.isPrimary, false)
        assertEquals(image.description, "")
    }

    @Test
    fun given_image_When_createImage_Then_checkValues() {
        val image = Image(id, estateId, imageUri, isPrimary, description)

        assertEquals(image.id, id)
        assertEquals(image.estateId, estateId)
        assertEquals(image.imageUri, imageUri)
        assertEquals(image.isPrimary, isPrimary)
        assertEquals(image.description, description)
    }

    @Test
    fun given_emptyImage_When_setImageValues_Then_checkValues() {
        val image = Image()

        image.estateId = estateId
        image.imageUri = imageUri
        image.isPrimary = isPrimary
        image.description = description

        assertEquals(image.estateId, estateId)
        assertEquals(image.imageUri, imageUri)
        assertEquals(image.isPrimary, isPrimary)
        assertEquals(image.description, description)
    }
}