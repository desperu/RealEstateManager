package org.desperu.realestatemanager.repositories

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.desperu.realestatemanager.database.dao.ImageDao
import org.desperu.realestatemanager.model.Image

/**
 * Repository interface to get image data from database.
 */
interface ImageRepository {

    /**
     * Returns the image for the given image id.
     * @param imageId the image id to get corresponding image.
     * @return the image for the given image id.
     * */
    // --- GET ---
    suspend fun getImage(imageId: Long): Image

    /**
     * Returns the image list for the given estate id.
     * @param estateId the estate id to get corresponding image list.
     * @return the image list for the given estate id.
     * */
    // --- GET LIST ---
    suspend fun getEstateImages(estateId: Long): List<Image>

    /**
     * Create the given images in database.
     * @param image the images to create in database.
     * @return the rows ids list of the created images in database.
     */
    // --- CREATE ---
    suspend fun createImage(vararg image: Image): List<Long>

    /**
     * Update the given images in database.
     * @param image the images to update in database.
     * @return the number of rows affected.
     */
    // --- UPDATE ---
    suspend fun updateImage(vararg image: Image): Int

    /**
     * Delete image in database for the given image id.
     * @param imageId the id of the image to delete in database.
     * @return the number of rows affected.
     */
    // --- DELETE ---
    suspend fun deleteImage(imageId: Long): Int

    /**
     * Delete all image in database for the given estate id.
     * @param estateId the estate id to delete corresponding images in database.
     * @return the number of rows affected.
     */
    // --- DELETE LIST ---
    suspend fun deleteEstateImages(estateId: Long): Int
}

/**
 * Implementation of the ImageRepository interface.
 *
 * @property imageDao the database access object for image.
 *
 * @constructor Instantiates a new AddressRepositoryImpl.
 *
 * @param imageDao the database access object for image to set.
 */
class ImageRepositoryImpl(private val imageDao: ImageDao): ImageRepository {

    /**
     * Returns the image from the database.
     * @param imageId the id of the image to get from database.
     * @return the image for the given estate id.
     */
    // --- GET ---
    override suspend fun getImage(imageId: Long) = withContext(Dispatchers.IO) {
        imageDao.getImage(imageId)
    }

    /**
     * Returns the image list for the given estate id from the database.
     * @param estateId the estate id to get corresponding images from database.
     * @return the image list for the given estate id.
     */
    // --- GET LIST ---
    override suspend fun getEstateImages(estateId: Long): List<Image> = withContext(Dispatchers.IO) {
        imageDao.getImageList(estateId)
    }

    /**
     * Create the given images in database.
     * @param image the images to create in database.
     * @return the rows ids list for the created images in database.
     */
    // --- CREATE ---
    override suspend fun createImage(vararg image: Image): List<Long> = withContext(Dispatchers.IO) {
        imageDao.insertImage(*image)
    }

    /**
     * Update the given images in database.
     * @param image the images to create in database.
     * @return the number of rows affected.
     */
    // --- UPDATE ---
    override suspend fun updateImage(vararg image: Image): Int = withContext(Dispatchers.IO) {
        imageDao.updateImage(*image)
    }

    /**
     * Delete image in database for the given image id.
     * @param imageId the id of the image to delete in database.
     * @return the number of rows affected.
     */
    // --- DELETE ---
    override suspend fun deleteImage(imageId: Long): Int = withContext(Dispatchers.IO) {
        imageDao.deleteImage(imageId)
    }

    /**
     * Delete all image in database for the given estate id.
     * @param estateId the estate id to delete corresponding images in database.
     * @return the number of rows affected.
     */
    // --- DELETE LIST ---
    override suspend fun deleteEstateImages(estateId: Long): Int = withContext(Dispatchers.IO) {
        imageDao.deleteEstateImages(estateId)
    }
}