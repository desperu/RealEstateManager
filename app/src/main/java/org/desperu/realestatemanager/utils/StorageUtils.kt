package org.desperu.realestatemanager.utils

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.os.Environment
import androidx.core.content.FileProvider.getUriForFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.desperu.realestatemanager.BuildConfig
import java.io.*

/**
 * Class witch provide read access for and write action in storage.
 */
object StorageUtils {

    /**
     * Provide a file access, create if not already exist, also get the file access.
     * @param destination the root destination folder.
     * @param folderName the last parent folder name witch contain the file.
     * @param fileName the of the file witch contain data.
     * @return the file object witch provide access to the file.
     */
    private suspend fun createOrGetFile(destination: File, folderName: String, fileName: String): File = withContext(Dispatchers.IO){
        val folder = File(destination, folderName)
        return@withContext File(folder, fileName)
    }

    /**
     * Get the file access from storage.
     * @param rootDestination the root destination folder.
     * @param folderName the last parent folder name witch contain the file.
     * @param fileName the of the file witch contain data.
     * @return the file object witch provide access to the file.
     */
    internal suspend fun getFileFromStorage(rootDestination: File, folderName: String, fileName: String): File {
        return createOrGetFile(rootDestination, folderName, fileName)
    }

    /**
     * Delete the file in storage.
     * @param rootDestination the root destination folder.
     * @param folderName the last parent folder name witch contain the file.
     * @param fileName the of the file witch contain data.
     * @return true if the file is deleted, false otherwise.
     */
    internal suspend fun deleteFileInStorage(rootDestination: File, folderName: String, fileName: String): Boolean = withContext(Dispatchers.IO) {
        val file: File = createOrGetFile(rootDestination, folderName, fileName)
        return@withContext file.delete()
    }

    /**
     * Get the text that the file contain.
     * @param rootDestination the root destination folder.
     * @param folderName the last parent folder name witch contain the file.
     * @param fileName the of the file witch contain data.
     * @return the text contained in the file,, null if an error happened.
     */
    internal suspend fun getTextFromStorage(rootDestination: File, folderName: String, fileName: String): String? {
        val file: File = createOrGetFile(rootDestination, folderName, fileName)
        return readOnFile(file)
    }

    /**
     * Set the given text in the storage.
     * @param rootDestination the root destination folder.
     * @param folderName the last parent folder name witch contain the file.
     * @param fileName the of the file witch contain data.
     * @param text the text to set in the file
     * @return true if the text was properly write, false otherwise.
     */
    internal suspend fun setTextInStorage(rootDestination: File, folderName: String, fileName: String, text: String): Boolean {
        val file: File = createOrGetFile(rootDestination, folderName, fileName)
        return writeOnFile(text, file)
    }

    /**
     * Set the given bitmap in the storage, convert the bitmap to JPEG and to byte to save it in a file.
     * @param context the context from this function is called.
     * @param rootDestination the root destination folder.
     * @param folderName the last parent folder name witch contain the file.
     * @param fileName the name of the file witch will contain data.
     * @param bitmap the bitmap to set in the file.
     * @return the uri of the created file, null if an error happened.
     */
    internal suspend fun setBitmapInStorage(context: Context, rootDestination: File, folderName: String, fileName: String, bitmap: Bitmap): String? {
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val file: File = createOrGetFile(rootDestination, folderName, fileName)
        return writeImageOnFile(context, bytes, file)
    }

    // ----------------------------------
    // EXTERNAL STORAGE
    // ----------------------------------

    /**
     * Check the external storage write access (user storage).
     * @return true if can write, false otherwise.
     */
    internal fun isExternalStorageWritable(): Boolean {
        val state: String = Environment.getExternalStorageState()
        return Environment.MEDIA_MOUNTED == state
    }

    /**
     * Check the external storage read access (user storage).
     * @return true if can read, false otherwise.
     */
    internal fun isExternalStorageReadable(): Boolean {
        val state: String = Environment.getExternalStorageState()
        return Environment.MEDIA_MOUNTED == state || Environment.MEDIA_MOUNTED_READ_ONLY == state
    }

    // ----------------------------------
    // READ & WRITE ON STORAGE
    // ----------------------------------

    /**
     * Read on file, and get text contained in the file.
     * @param file the file object witch provide access to the file.
     * @return the text contained in the file, null if an error happened.
     */
    private suspend fun readOnFile(file: File): String? = withContext(Dispatchers.IO) {
        var result: String? = null
        if (file.exists()) {
            val br: BufferedReader
            try {
                br = BufferedReader(FileReader(file))
                br.use { br1 ->
                    val sb = StringBuilder()
                    var line: String? = br1.readLine()
                    while (line != null) {
                        sb.append(line)
                        sb.append("\n")
                        line = br1.readLine()
                    }
                    result = sb.toString()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return@withContext result
    }

    /**
     * Write on file the given text.
     * @param text the given text to write on the file.
     * @param file the file access to write on.
     * @return true if the text was properly write, false otherwise.
     */
    private suspend fun writeOnFile(text: String, file: File): Boolean = withContext(Dispatchers.IO) {
        var isWrite = false
        try {
            file.parentFile!!.mkdirs()
            val fos = FileOutputStream(file)
            val w: Writer = BufferedWriter(OutputStreamWriter(fos))
            try {
                w.write(text)
                w.flush()
                fos.fd.sync()
            } finally {
                w.close()
                isWrite = true
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return@withContext isWrite
    }

    /**
     * Write the given bytes, witch came from converted bitmap, on file.
     * @param context the context fom this function is called.
     * @param bytes the bytes to write on file, witch came from converted bitmap.
     * @param file the file object witch provide access to the file to write on.
     * @return the content uri for the created bitmap file, null if an error happened.
     */
    private suspend fun writeImageOnFile(context: Context, bytes: ByteArrayOutputStream, file: File): String? = withContext(Dispatchers.IO) {
        var stringUri: String? = null
        try {
            file.parentFile?.mkdirs()
            val fos = FileOutputStream(file)
            try {
                fos.write(bytes.toByteArray())
                fos.fd.sync()
            } finally {
                fos.close()
                MediaScannerConnection.scanFile(context.applicationContext, arrayOf(file.path), arrayOf("image/jpeg"), null)
                stringUri = getUriForFile(context.applicationContext, BuildConfig.APPLICATION_ID + ".fileprovider", file).toString()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return@withContext stringUri
    }
}