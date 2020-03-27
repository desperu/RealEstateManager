package org.desperu.realestatemanager.repositories

import androidx.lifecycle.LiveData
import org.desperu.realestatemanager.database.dao.ImageDao
import org.desperu.realestatemanager.model.Image

class ImageDataRepository(private val imageDao: ImageDao) {

    // --- GET ---
    fun getImage(imageId: Long) = imageDao.getImage(imageId)

    // --- GET LIST ---
    fun getImageList(estateId: Long): LiveData<List<Image>> = imageDao.getImageList(estateId)

    // --- CREATE ---
    fun createImage(image: Image) { imageDao.insertImage(image) }

    // --- CREATE LIST ---
    fun createImageList(imageList: List<Image>) { for (image in imageList) imageDao.insertImage(image) }

    // --- UPDATE ---
    fun updateImage(image: Image) { imageDao.updateImage(image) }

    // --- UPDATE ---
    fun updateImageList(imageList: List<Image>) { for (image in imageList) imageDao.updateImage(image) }

    // --- DELETE ---
    fun deleteImage(imageId: Long) { imageDao.deleteImage(imageId) }
}