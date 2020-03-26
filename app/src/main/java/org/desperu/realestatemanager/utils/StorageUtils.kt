package org.desperu.realestatemanager.utils

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.os.Environment
import android.widget.Toast
import androidx.core.content.FileProvider.getUriForFile
import org.desperu.realestatemanager.BuildConfig
import org.desperu.realestatemanager.R
import java.io.*

object StorageUtils { // TODO to clean unused functions

    private fun createOrGetFile(destination: File, fileName: String, folderName: String): File {
        val folder = File(destination, folderName)
        return File(folder, fileName)
    }

    fun getTextFromStorage(rootDestination: File, context: Context, fileName: String, folderName: String): String? {
        val file: File = createOrGetFile(rootDestination, fileName, folderName)
        return readOnFile(context, file)
    }

    fun setTextInStorage(rootDestination: File, context: Context, fileName: String, folderName: String, text: String) {
        val file: File = createOrGetFile(rootDestination, fileName, folderName)
        writeOnFile(context, text, file)
    }

    fun setImageInStorage(rootDestination: File, context: Context, fileName: String, folderName: String, bitmap: Bitmap): String {
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val file: File = createOrGetFile(rootDestination, fileName, folderName)
        return writeOnFile(context, bytes, file)
    }

    fun getFileFromStorage(rootDestination: File, context: Context?, fileName: String, folderName: String): File? {
        return createOrGetFile(rootDestination, fileName, folderName)
    }

    // ----------------------------------
    // EXTERNAL STORAGE
    // ----------------------------------

    fun isExternalStorageWritable(): Boolean {
        val state: String = Environment.getExternalStorageState()
        return Environment.MEDIA_MOUNTED == state
    }

    fun isExternalStorageReadable(): Boolean {
        val state: String = Environment.getExternalStorageState()
        return Environment.MEDIA_MOUNTED == state || Environment.MEDIA_MOUNTED_READ_ONLY == state
    }

    // ----------------------------------
    // READ & WRITE ON STORAGE
    // ----------------------------------

    private fun readOnFile(context: Context, file: File): String {
        var result = String()
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
                Toast.makeText(context, context.getString(R.string.storage_utils_error_happened), Toast.LENGTH_LONG).show()
            }
        }
        return result
    }

    private fun writeOnFile(context: Context, text: String, file: File) {
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
                Toast.makeText(context, context.getString(R.string.storage_utils_saved), Toast.LENGTH_LONG).show()
            }
        } catch (e: IOException) {
            Toast.makeText(context, context.getString(R.string.storage_utils_error_happened), Toast.LENGTH_LONG).show()
        }
    }

    private fun writeOnFile(context: Context, byte: ByteArrayOutputStream, file: File): String {
        var stringUri = String()
        try {
            file.parentFile!!.mkdirs()
            val fos = FileOutputStream(file)
            try {
                fos.write(byte.toByteArray())
                MediaScannerConnection.scanFile(context, arrayOf(file.path), arrayOf("image/jpeg"), null)
                fos.fd.sync()
            } finally {
                fos.close()
                stringUri = getUriForFile(context.applicationContext, BuildConfig.APPLICATION_ID + ".fileprovider", file).toString()
                Toast.makeText(context, context.getString(R.string.storage_utils_saved), Toast.LENGTH_LONG).show()
            }
        } catch (e: IOException) {
            Toast.makeText(context, context.getString(R.string.storage_utils_error_happened), Toast.LENGTH_LONG).show()
        }
        return  stringUri
    }
}