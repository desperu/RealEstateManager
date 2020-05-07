package org.desperu.realestatemanager.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import org.desperu.realestatemanager.model.Image

/**
 * The database access object for images.
 */
@Dao
interface ImageDao {

    /**
     * Returns the image from database ordered for given image id.
     * @param imageId the image id to get the corresponding image from database.
     * @return the corresponding image.
     */
    @Query("SELECT * FROM Image WHERE id = :imageId")
    suspend fun getImage(imageId: Long): Image

    /**
     * Returns the image list from database ordered for the given estate id,
     * ordered with the primary in first.
     * @param estateId the estate id to get the corresponding image list from database.
     * @return the estate's image list.
     */
    @Query("SELECT * FROM Image WHERE estateId = :estateId ORDER BY isPrimary DESC")
    suspend fun getImageList(estateId: Long): List<Image>

    /**
     * Inserts the given image in database.
     * @param image the image to insert in database.
     * @return the rows ids list for the inserted images.
     */
    @Insert
    suspend fun insertImage(vararg image: Image): List<Long>

    /**
     * Update the given image in database.
     * @param image the image to update in database.
     * @return the number of rows affected.
     */
    @Update
    suspend fun updateImage(vararg image: Image): Int

    /**
     * Delete the image in database for the given image id.
     * @param imageId the image id to delete in database.
     * @return the number of row affected.
     */
    @Query("DELETE FROM Image WHERE id = :imageId")
    suspend fun deleteImage(imageId: Long): Int

    /**
     * Delete all images in database for the given estate id.
     * @param estateId the estate id to delete all corresponding images in database.
     * @return the number of row affected.
     */
    @Query("DELETE FROM Image WHERE estateId = :estateId")
    suspend fun deleteEstateImages(estateId: Long): Int
}