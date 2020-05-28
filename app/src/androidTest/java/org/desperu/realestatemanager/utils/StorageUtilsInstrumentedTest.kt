package org.desperu.realestatemanager.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.runBlocking
import org.desperu.realestatemanager.test.R
import org.desperu.realestatemanager.utils.StorageUtils.deleteFileInStorage
import org.desperu.realestatemanager.utils.StorageUtils.getFileFromStorage
import org.desperu.realestatemanager.utils.StorageUtils.getTextFromStorage
import org.desperu.realestatemanager.utils.StorageUtils.isExternalStorageReadable
import org.desperu.realestatemanager.utils.StorageUtils.isExternalStorageWritable
import org.desperu.realestatemanager.utils.StorageUtils.setBitmapInStorage
import org.desperu.realestatemanager.utils.StorageUtils.setTextInStorage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File


@RunWith(AndroidJUnit4::class)
class StorageUtilsInstrumentedTest {

    private lateinit var context: Context
    private lateinit var rootDestination: File
    private val fileName = "fileName"
    private val text = "Some text to save in storage"

    @Before
    fun before() {
        context = InstrumentationRegistry.getInstrumentation().context
        // Need read and write external storage access granted
        rootDestination = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
    }

    @Test
    fun checkExternalStorageReadAndWriteAccess() {

        // Check read external storage access
        assertTrue(isExternalStorageReadable())

        // Check write external storage access
        assertTrue(isExternalStorageWritable())
    }

    @Test
    fun setTextGetFileAndRetrievedTextFromStorage() = runBlocking {

        // Set text in file
        val isWrite = setTextInStorage(rootDestination, FOLDER_NAME, fileName, text)

        // Check that the text was properly write
        assertTrue(isWrite)

        // Retrieve file access
        val outputFile = getFileFromStorage(rootDestination, FOLDER_NAME, fileName)

        // Check output file is correct
        assertTrue(outputFile.isFile)

        // Get inserted text from storage
        val output = getTextFromStorage(rootDestination, FOLDER_NAME, fileName)

        // Check that the retrieved text equal original
        assertEquals(text + "\n", output)
    }

    @Test
    fun setImageRetrieveImageAndDeleteFromStorage() = runBlocking {
        val expected = "content://org.desperu.realestatemanager.fileprovider/EstateImages/Android/data/org.desperu.realestatemanager.test/files/Pictures/EstateImages/$fileName"

        // Create a bitmap for test
        val bitmap: Bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.for_test_ic_baseline_add_circle_black_24)

        // Set image in storage
        val outputUri = setBitmapInStorage(context, rootDestination, FOLDER_NAME, fileName, bitmap)

        // Check that the uri match expected
        assertEquals(expected, outputUri)

        // Delete file in storage
        val isDeleted = deleteFileInStorage(rootDestination, FOLDER_NAME, fileName)

        // Check that the file was properly deleted
        assertTrue(isDeleted)
    }
}