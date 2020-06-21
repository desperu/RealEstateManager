package org.desperu.realestatemanager.provider

import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class RealEstateContentProviderTest {

    // FOR DATA
    private lateinit var mContentResolver: ContentResolver

    // DATA SET FOR TEST
    private val id: Long = 100L

    @Before
    fun setUp() {
        // Set content resolver
        mContentResolver = InstrumentationRegistry.getInstrumentation().context.contentResolver
    }

    // -----------------
    // FOR ESTATE
    // -----------------

    @Test
    fun getEstateWhenNoEstateInserted() {
        val cursor: Cursor? = mContentResolver.query(ContentUris.withAppendedId(RealEstateContentProvider.URI_ESTATE, 1000), null, null, null, null)
        assertThat(cursor, notNullValue())
        assertThat(cursor?.count, `is`(0))
        cursor?.close()
    }

    @Test
    fun insertAndGetEstate() {
        // BEFORE : Adding demo item
        val estateUri: Uri? = mContentResolver.insert(RealEstateContentProvider.URI_ESTATE, generateEstate())

        // TEST
        val cursor: Cursor? = estateUri?.let { mContentResolver.query(it, null, null, null, null) }

        assertThat(cursor, notNullValue())
        assertThat(cursor?.count, `is`(1))
        assertThat(cursor?.moveToFirst(), `is`(true))

        assertThat(cursor?.getString(cursor.getColumnIndexOrThrow("type")), `is`("Flat"))
        assertThat(cursor?.getLong(cursor.getColumnIndexOrThrow("price")), `is`(2000000L))
        assertThat(cursor?.getInt(cursor.getColumnIndexOrThrow("surfaceArea")), `is` (150))
        assertThat(cursor?.getInt(cursor.getColumnIndexOrThrow("roomNumber")), `is` (10))
        assertThat(cursor?.getString(cursor.getColumnIndexOrThrow("description")), `is` ("a Description"))
        assertThat(cursor?.getString(cursor.getColumnIndexOrThrow("interestPlaces")), `is` ("School"))
        assertThat(cursor?.getString(cursor.getColumnIndexOrThrow("state")), `is` ("For Sale"))
        assertThat(cursor?.getString(cursor.getColumnIndexOrThrow("saleDate")), `is` ("20/05/2020"))
        assertThat(cursor?.getString(cursor.getColumnIndexOrThrow("soldDate")), `is` (""))
        assertThat(cursor?.getString(cursor.getColumnIndexOrThrow("realEstateAgent")), `is` (""))
        assertThat(cursor?.getLong(cursor.getColumnIndexOrThrow("createdTime")), `is` (123415312L))

        // Delete created estate after test
        estateUri?.let { mContentResolver.delete(it, null, null) }
    }

    @Test
    fun getEstateType() {
        val estateUri: Uri? = mContentResolver.insert(RealEstateContentProvider.URI_ESTATE, generateEstate())

        val output = estateUri?.let { mContentResolver.getType(it) }

        assertThat(output, `is`("vnd.android.cursor.item/org.desperu.realestatemanager.provider.Estate"))

        // Delete created estate after test
        estateUri?.let { mContentResolver.delete(it, null, null) }
    }

    @Test
    fun insertAndDeleteEstate() {
        // BEFORE : Adding demo item
        val estateUri: Uri? = mContentResolver.insert(RealEstateContentProvider.URI_ESTATE, generateEstate())

        // TEST
        val deleted: Int? = estateUri?.let { mContentResolver.delete(it, null, null) }

        assertThat(deleted, `is`(1))
    }

    @Test
    fun insertAndUpdateEstate() {
        // BEFORE : Adding demo item
        val contentValues = generateEstate()
        val estateUri: Uri? = mContentResolver.insert(RealEstateContentProvider.URI_ESTATE, contentValues)
        val estateId = estateUri?.let { ContentUris.parseId(it) }

        // UPDATE DATA
        contentValues?.put("id", estateId)
        contentValues?.remove("type")
        contentValues?.put("type", "House")
        contentValues?.remove("price")
        contentValues?.put("price", 1000000L)
        contentValues?.remove("surfaceArea")
        contentValues?.put("surfaceArea", 100)

        val updated: Int? = estateUri?.let { mContentResolver.update(it, contentValues, null, null) }
        assertThat(updated, `is`(1))

        // TEST
        val cursor: Cursor? = estateUri?.let { mContentResolver.query(it, null, null, null, null) }

        assertThat(cursor, notNullValue())
        assertThat(cursor?.count, `is`(1))
        assertThat(cursor?.moveToFirst(), `is`(true))

        assertThat(cursor?.getString(cursor.getColumnIndexOrThrow("type")), `is`("House"))
        assertThat(cursor?.getLong(cursor.getColumnIndexOrThrow("price")), `is`(1000000L))
        assertThat(cursor?.getInt(cursor.getColumnIndexOrThrow("surfaceArea")), `is` (100))

        // Delete created estate after test
        estateUri?.let { mContentResolver.delete(it, null, null) }
    }

    private fun generateEstate(): ContentValues? {
        val contentValues = ContentValues()

        contentValues.put("type", "Flat")
        contentValues.put("price", 2000000L)
        contentValues.put("surfaceArea", 150)
        contentValues.put("roomNumber", 10)
        contentValues.put("description", "a Description")
        contentValues.put("interestPlaces", "School")
        contentValues.put("state", "For Sale")
        contentValues.put("saleDate", "20/05/2020")
        contentValues.put("soldDate", "")
        contentValues.put("realEstateAgent", "")
        contentValues.put("createdTime", 123415312L)

        return contentValues
    }

    // -----------------
    // FOR IMAGE
    // -----------------

    @Test
    fun getImageWhenNoImageInserted() {
        val cursor: Cursor? = mContentResolver.query(ContentUris.withAppendedId(RealEstateContentProvider.URI_IMAGE, id), null, null, null, null)
        assertThat(cursor, notNullValue())
        assertThat(cursor?.count, `is`(0))
        cursor?.close()
    }

    @Test
    fun insertAndGetImage() {
        // BEFORE : For Foreign key
        val estateUri: Uri? = mContentResolver.insert(RealEstateContentProvider.URI_ESTATE, generateEstate())
        val estateId = estateUri?.let { ContentUris.parseId(it) }

        // BEFORE : Adding demo item
        val imageUri: Uri? = mContentResolver.insert(RealEstateContentProvider.URI_IMAGE, generateImage(estateId))

        // TEST
        val cursor: Cursor? = imageUri?.let { mContentResolver.query(it, null, null, null, null) }

        assertThat(cursor, notNullValue())
        assertThat(cursor?.count, `is`(1))
        assertThat(cursor?.moveToFirst(), `is`(true))

        assertThat(cursor?.getLong(cursor.getColumnIndexOrThrow("estateId")), `is`(estateId))
        assertThat(cursor?.getString(cursor.getColumnIndexOrThrow("imageUri")), `is`("an uri"))
        assertThat(cursor?.getInt(cursor.getColumnIndexOrThrow("isPrimary")), `is`(1))
        assertThat(cursor?.getString(cursor.getColumnIndexOrThrow("description")), `is`("Bathroom"))
        assertThat(cursor?.getFloat(cursor.getColumnIndexOrThrow("rotation")), `is`(90F))

        // Delete created image and estate after test
        imageUri?.let { mContentResolver.delete(it, null, null) }
        estateUri?.let { mContentResolver.delete(it, null, null) }
    }

    @Test
    fun getImageType() {
        // BEFORE : For Foreign key
        val estateUri: Uri? = mContentResolver.insert(RealEstateContentProvider.URI_ESTATE, generateEstate())
        val estateId = estateUri?.let { ContentUris.parseId(it) }

        val imageUri: Uri? = mContentResolver.insert(RealEstateContentProvider.URI_IMAGE, generateImage(estateId))

        val output = imageUri?.let { mContentResolver.getType(it) }

        assertThat(output, `is`("vnd.android.cursor.item/org.desperu.realestatemanager.provider.Image"))

        // Delete created image and estate after test
        imageUri?.let { mContentResolver.delete(it, null, null) }
        estateUri?.let { mContentResolver.delete(it, null, null) }
    }

    @Test
    fun insertAndDeleteImage() {
        // BEFORE : For Foreign key
        val estateUri: Uri? = mContentResolver.insert(RealEstateContentProvider.URI_ESTATE, generateEstate())
        val estateId = estateUri?.let { ContentUris.parseId(it) }

        // BEFORE : Adding demo item
        val imageUri: Uri? = mContentResolver.insert(RealEstateContentProvider.URI_IMAGE, generateImage(estateId))

        // TEST
        val deleted: Int? = imageUri?.let { mContentResolver.delete(it, null, null) }

        assertThat(deleted, `is`(1))

        // Delete created estate after test
        estateUri?.let { mContentResolver.delete(it, null, null) }
    }

    @Test
    fun insertAndUpdateImage() {
        // BEFORE : For Foreign key
        val estateUri: Uri? = mContentResolver.insert(RealEstateContentProvider.URI_ESTATE, generateEstate())
        val estateId = estateUri?.let { ContentUris.parseId(it) }

        // BEFORE : Adding demo item
        val contentValues = generateImage(estateId)
        val imageUri: Uri? = mContentResolver.insert(RealEstateContentProvider.URI_IMAGE, contentValues)
        val imageId = imageUri?.let { ContentUris.parseId(it) }

        // UPDATE DATA
        contentValues?.put("id", imageId)
        contentValues?.remove("estateId")
        contentValues?.put("estateId", estateId)
        contentValues?.remove("imageUri")
        contentValues?.put("imageUri", "another uri")
        contentValues?.remove("isPrimary")
        contentValues?.put("isPrimary", false)

        val updated: Int? = imageUri?.let { mContentResolver.update(it, contentValues, null, null) }
        assertThat(updated, `is`(1))

        // TEST
        val cursor: Cursor? = imageUri?.let { mContentResolver.query(it, null, null, null, null) }

        assertThat(cursor, notNullValue())
        assertThat(cursor?.count, `is`(1))
        assertThat(cursor?.moveToFirst(), `is`(true))

        assertThat(cursor?.getLong(cursor.getColumnIndexOrThrow("estateId")), `is`(estateId))
        assertThat(cursor?.getString(cursor.getColumnIndexOrThrow("imageUri")), `is`("another uri"))
        assertThat(cursor?.getInt(cursor.getColumnIndexOrThrow("isPrimary")), `is`(0))

        // Delete created image and estate after test
        imageUri?.let { mContentResolver.delete(it, null, null) }
        estateUri?.let { mContentResolver.delete(it, null, null) }
    }

    private fun generateImage(estateId: Long?): ContentValues? {
        val contentValues = ContentValues()

        contentValues.put("estateId", estateId)
        contentValues.put("imageUri", "an uri")
        contentValues.put("isPrimary", true)
        contentValues.put("description", "Bathroom")
        contentValues.put("rotation", 90F)

        return contentValues
    }

    // -----------------
    // FOR ADDRESS
    // -----------------

    @Test
    fun getAddressWhenNoAddressInserted() {
        val cursor: Cursor? = mContentResolver.query(ContentUris.withAppendedId(RealEstateContentProvider.URI_ADDRESS, id), null, null, null, null)
        assertThat(cursor, notNullValue())
        assertThat(cursor?.count, `is`(0))
        cursor?.close()
    }

    @Test
    fun insertAndGetAddress() {
        // BEFORE : For Foreign key
        val estateUri: Uri? = mContentResolver.insert(RealEstateContentProvider.URI_ESTATE, generateEstate())
        val estateId = estateUri?.let { ContentUris.parseId(it) }

        // BEFORE : Adding demo item
        val addressUri: Uri? = mContentResolver.insert(RealEstateContentProvider.URI_ADDRESS, generateAddress(estateId))

        // TEST
        val cursor: Cursor? = addressUri?.let { mContentResolver.query(it, null, null, null, null) }

        assertThat(cursor, notNullValue())
        assertThat(cursor?.count, `is`(1))
        assertThat(cursor?.moveToFirst(), `is`(true))

        assertThat(cursor?.getLong(cursor.getColumnIndexOrThrow("estateId")), `is`(estateId))
        assertThat(cursor?.getInt(cursor.getColumnIndexOrThrow("streetNumber")), `is`(45))
        assertThat(cursor?.getString(cursor.getColumnIndexOrThrow("streetName")), `is`("a street name"))
        assertThat(cursor?.getString(cursor.getColumnIndexOrThrow("flatBuilding")), `is`("some flat buildings info"))
        assertThat(cursor?.getInt(cursor.getColumnIndexOrThrow("postalCode")), `is`(11204))
        assertThat(cursor?.getString(cursor.getColumnIndexOrThrow("city")), `is`("New York"))
        assertThat(cursor?.getString(cursor.getColumnIndexOrThrow("country")), `is`("United States"))
        assertThat(cursor?.getDouble(cursor.getColumnIndexOrThrow("latitude")), `is`(45.235689))
        assertThat(cursor?.getDouble(cursor.getColumnIndexOrThrow("longitude")), `is`(102.255478))

        // Delete created address and estate after test
        addressUri?.let { mContentResolver.delete(it, null, null) }
        estateUri?.let { mContentResolver.delete(it, null, null) }
    }

    @Test
    fun getAddressType() {
        // BEFORE : For Foreign key
        val estateUri: Uri? = mContentResolver.insert(RealEstateContentProvider.URI_ESTATE, generateEstate())
        val estateId = estateUri?.let { ContentUris.parseId(it) }

        val addressUri: Uri? = mContentResolver.insert(RealEstateContentProvider.URI_ADDRESS, generateAddress(estateId))
        val output = addressUri?.let { mContentResolver.getType(it) }

        assertThat(output, `is`("vnd.android.cursor.item/org.desperu.realestatemanager.provider.Address"))

        // Delete created address and estate after test
        addressUri?.let { mContentResolver.delete(it, null, null) }
        estateUri?.let { mContentResolver.delete(it, null, null) }
    }

    @Test
    fun insertAndDeleteAddress() {
        // BEFORE : For Foreign key
        val estateUri: Uri? = mContentResolver.insert(RealEstateContentProvider.URI_ESTATE, generateEstate())
        val estateId = estateUri?.let { ContentUris.parseId(it) }

        // BEFORE : Adding demo item
        val addressUri: Uri? = mContentResolver.insert(RealEstateContentProvider.URI_ADDRESS, generateAddress(estateId))

        // TEST
        val deleted: Int? = addressUri?.let { mContentResolver.delete(it, null, null) }

        assertThat(deleted, `is`(1))

        // Delete created estate after test
        estateUri?.let { mContentResolver.delete(it, null, null) }
    }

    @Test
    fun insertAndUpdateAddress() {
        // BEFORE : For Foreign key
        val estateUri: Uri? = mContentResolver.insert(RealEstateContentProvider.URI_ESTATE, generateEstate())
        val estateId = estateUri?.let { ContentUris.parseId(it) }

        // BEFORE : Adding demo item
        val contentValues = generateAddress(estateId)
        val addressUri: Uri? = mContentResolver.insert(RealEstateContentProvider.URI_ADDRESS, contentValues)

        // UPDATE DATA
        contentValues?.remove("streetNumber")
        contentValues?.put("streetNumber", 23)
        contentValues?.remove("streetName")
        contentValues?.put("streetName", "another street name")
        contentValues?.remove("flatBuilding")
        contentValues?.put("flatBuilding", "some other info")

        val updated: Int? = addressUri?.let { mContentResolver.update(it, contentValues, null, null) }
        assertThat(updated, `is`(1))

        // TEST
        val cursor: Cursor? = addressUri?.let { mContentResolver.query(it, null, null, null, null) }

        assertThat(cursor, notNullValue())
        assertThat(cursor?.count, `is`(1))
        assertThat(cursor?.moveToFirst(), `is`(true))

        assertThat(cursor?.getInt(cursor.getColumnIndexOrThrow("streetNumber")), `is`(23))
        assertThat(cursor?.getString(cursor.getColumnIndexOrThrow("streetName")), `is`("another street name"))
        assertThat(cursor?.getString(cursor.getColumnIndexOrThrow("flatBuilding")), `is`("some other info"))

        // Delete created address and estate after test
        addressUri?.let { mContentResolver.delete(it, null, null) }
        estateUri?.let { mContentResolver.delete(it, null, null) }
    }

    private fun generateAddress(estateId: Long?): ContentValues? {
        val contentValues = ContentValues()

        contentValues.put("estateId", estateId)
        contentValues.put("streetNumber", 45)
        contentValues.put("streetName", "a street name")
        contentValues.put("flatBuilding", "some flat buildings info")
        contentValues.put("postalCode", 11204)
        contentValues.put("city", "New York")
        contentValues.put("country", "United States")
        contentValues.put("latitude", 45.235689)
        contentValues.put("longitude", 102.255478)

        return contentValues
    }
}