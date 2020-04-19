package org.desperu.realestatemanager.database.dao

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.desperu.realestatemanager.database.EstateDatabase
import org.desperu.realestatemanager.model.Estate
import org.desperu.realestatemanager.model.Image
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test


class ImageDaoInstrumentedTest {

    private lateinit var mDatabase: EstateDatabase
    // Create an ImageList for Db test
    private var estateId = 1L
    private var imageList = listOf<Image>()
    private lateinit var image: Image

    @get:Rule
    val instantTaskExecutorRule: InstantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    @Throws(Exception::class)
    @ExperimentalCoroutinesApi
    fun initDbAndData() {
        // init Db for test
        mDatabase = DaoTestHelper().initDb()

        // Set Estate for foreign keys matches
        runBlockingTest { estateId = mDatabase.estateDao().insertEstate(Estate()) }

        // Set image data for Db test
        imageList = DaoTestHelper().getImageList(estateId)
        image = DaoTestHelper().getImage(estateId)
    }

    @After
    @Throws(Exception::class)
    fun closeDb() { mDatabase.close() }

    @Test
    @ExperimentalCoroutinesApi
    fun insertAndGetImage() = runBlockingTest {
        // Given an Image that has been inserted into the DB
        val rowId = mDatabase.imageDao().insertImage(image)

        // Check that the row id and the image id match
        assertEquals(rowId[0], image.id)

        // When getting the Image via the DAO
        val imageDb = mDatabase.imageDao().getImage(rowId[0])

        // Then the retrieved Image match the original image object
        assertEquals(image, imageDb)

        // Clean up coroutines
        cleanupTestCoroutines()
    }

    @Test
    @ExperimentalCoroutinesApi
    fun insertAndGetImageList() = runBlockingTest {
        // Given an ImageList that has been inserted into the DB
        val rowId = mDatabase.imageDao().insertImage(*imageList.toTypedArray())

        // Check that the rows ids and the images ids matches
        assertEquals(rowId[0], imageList[0].id)
        assertEquals(rowId[1], imageList[1].id)

        // When getting the ImageList via the DAO
        val imageListDb = mDatabase.imageDao().getImageList(estateId)

        // Then the retrieved ImageList match the original imageList object
        assertEquals(imageList, imageListDb)

        // Clean up coroutines
        cleanupTestCoroutines()
    }

    @Test
    @ExperimentalCoroutinesApi
    fun updateAndGetImage() = runBlockingTest {
        // Insert an image in database for the test
        mDatabase.imageDao().insertImage(image)

        // Change some data to update them in database
        image.description = "description"

        // Given an Image that has been updated into the DB
        val rowAffected = mDatabase.imageDao().updateImage(image)

        // Check that's there only one row affected when updating
        assertEquals(1, rowAffected)

        // When getting the Image via the DAO
        val imageDb = mDatabase.imageDao().getImage(image.id)

        // Then the retrieved Image match the original image object
        assertEquals(image, imageDb)

        // Clean up coroutines
        cleanupTestCoroutines()
    }

    @Test
    @ExperimentalCoroutinesApi
    fun updateAndGetImageList() = runBlockingTest {
        // Insert an imageList in database for the test
        mDatabase.imageDao().insertImage(*imageList.toTypedArray())

        // Change some data to update them in database
        imageList[0].description = "description"
        imageList[1].description = "description2"

        // Given an ImageList that has been updated into the DB
        val rowAffected = mDatabase.imageDao().updateImage(*imageList.toTypedArray())

        // Check that's there only two row affected when updating
        assertEquals(2, rowAffected)

        // When getting the Image List via the DAO
        val imageListDb = mDatabase.imageDao().getImageList(estateId)

        // Then the retrieved Image List match the original image list object
        assertEquals(imageList, imageListDb)

        // Clean up coroutines
        cleanupTestCoroutines()
    }

    @Test
    @ExperimentalCoroutinesApi
    fun deleteImageAndCheckDb() = runBlockingTest {
        // Insert an image in database for the test
        mDatabase.imageDao().insertImage(image)

        // Given an Image that has been inserted into the DB
        val rowAffected = mDatabase.imageDao().deleteImage(image.id)

        // Check that's there only row affected when deleting
        assertEquals(1, rowAffected)

        // When getting the Image via the DAO
        val imageDb = mDatabase.imageDao().getImage(image.id)

        // Then the retrieved Image is empty
        assertNull(imageDb)

        // Clean up coroutines
        cleanupTestCoroutines()
    }

    @Test
    @ExperimentalCoroutinesApi
    fun deleteImageListAndCheckDb() = runBlockingTest {
        // Insert an imageList in database for the test
        mDatabase.imageDao().insertImage(*imageList.toTypedArray())

        // Given an Image List that has been inserted into the DB
        val rowAffected = mDatabase.imageDao().deleteEstateImages(estateId)

        // Check that's there only two rows affected when deleting
        assertEquals(2, rowAffected)

        // When getting the Image List via the DAO
        val imageListDb = mDatabase.imageDao().getImageList(estateId)

        // Then the retrieved Image List is empty
        assertTrue(imageListDb.isEmpty())

        // Clean up coroutines
        cleanupTestCoroutines()
    }
}