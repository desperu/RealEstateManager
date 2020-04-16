package org.desperu.realestatemanager.extension

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.provider.MediaStore
import android.util.Log


/**
 * Convert uri to bitmap, with the properly function,
 * depending of SDK version, return null if an error happened.
 * @param contentResolver the content resolver to provide storage access.
 * @return the created bitmap, null if an error happened.
 */
@Suppress("DEPRECATION")
fun Uri.toBitmap(contentResolver: ContentResolver): Bitmap? = try {
    if (android.os.Build.VERSION.SDK_INT >= 29)
        // To handle deprecation use
        ImageDecoder.decodeBitmap(ImageDecoder.createSource(contentResolver, this))
    else
        // Use older version
        MediaStore.Images.Media.getBitmap(contentResolver, this)
} catch (e: Exception) {
    Log.w(javaClass.toString(), e.message!!) // TODO to check
    null
}