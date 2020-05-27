package org.desperu.realestatemanager.extension

import android.graphics.Bitmap
import android.graphics.Matrix

/**
 * Rotate a bitmap with given degrees.
 * @param degrees the degree value to rotate the bitmap.
 * @return the bitmap with rotation.
 */
fun Bitmap.rotate(degrees: Float): Bitmap {
    val matrix = Matrix().apply { postRotate(degrees) }
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}