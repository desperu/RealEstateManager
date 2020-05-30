package org.desperu.realestatemanager.model

import android.content.ContentValues
import org.desperu.realestatemanager.utils.EQUALS
import org.desperu.realestatemanager.utils.NOT_EQUALS
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Simple model class test, for Image data class that's check setter, getter and default parameters.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28], manifest=Config.NONE)
class ImageTest {

    private val id = 1L
    private val estateId = 1L
    private val imageUri = "an uri"
    private val isPrimary = true
    private val description = "bathroom"
    private val rotation = 90F

    @Test
    fun given_emptyImage_When_createImage_Then_checkDefaultValues() {
        val image = Image()

        assertEquals(image.id, 0L)
        assertEquals(image.estateId, 0L)
        assertEquals(image.imageUri, "")
        assertEquals(image.isPrimary, false)
        assertEquals(image.description, "")
        assertEquals(image.rotation, 0F)
    }

    @Test
    fun given_image_When_createImage_Then_checkValues() {
        val image = Image(id, estateId, imageUri, isPrimary, description, rotation)

        assertEquals(image.id, id)
        assertEquals(image.estateId, estateId)
        assertEquals(image.imageUri, imageUri)
        assertEquals(image.isPrimary, isPrimary)
        assertEquals(image.description, description)
        assertEquals(image.rotation, rotation)
    }

    @Test
    fun given_emptyImage_When_setImageValues_Then_checkValues() {
        val image = Image()

        image.estateId = estateId
        image.imageUri = imageUri
        image.isPrimary = isPrimary
        image.description = description
        image.rotation = rotation

        assertEquals(image.estateId, estateId)
        assertEquals(image.imageUri, imageUri)
        assertEquals(image.isPrimary, isPrimary)
        assertEquals(image.description, description)
        assertEquals(image.rotation, rotation)
    }

    @Test
    fun given_image_When_compareTo_Then_checkEquals() {
        val image = Image()

        assertEquals(EQUALS, image.compareTo(image))
    }

    @Test
    fun given_originalImage_When_compareTo_Then_checkNotEquals() {
        val originalImage = Image()

        val finalImage = originalImage.copy()
        finalImage.estateId = 2L

        assertEquals(NOT_EQUALS, originalImage.compareTo(finalImage))
    }

    @Test
    fun given_contentValues_When_fromContentValues_Then_checkAddressFields() {
        val expected = Image(0, estateId, imageUri, isPrimary, description, rotation)

        val contentValues = ContentValues()
        Assert.assertNotNull(contentValues)

        contentValues.put("estateId", estateId)
        contentValues.put("imageUri", imageUri)
        contentValues.put("isPrimary", isPrimary)
        contentValues.put("description", description)
        contentValues.put("rotation", rotation)

        val output = Image().fromContentValues(contentValues)

        assertEquals(expected, output)
    }
}