package org.desperu.realestatemanager.repositories

import androidx.lifecycle.LiveData
import org.desperu.realestatemanager.database.dao.ImageDao
import org.desperu.realestatemanager.model.Image

class ImageDataRepository(private val imageDao: ImageDao) {

    // --- GET ---
    fun getImage(imageId: Long) = imageDao.getImage(imageId)

    // --- GET LIST---
    fun getImages(estateId: Long): LiveData<List<Image>> = imageDao.getImages(estateId)

    // --- CREATE ---
    fun createEstate(image: Image) { imageDao.insertImage(image) }

    // --- UPDATE ---
    fun updateEstate(image: Image) { imageDao.updateImage(image) }

    // --- DELETE ---
    fun deleteEstate(imageId: Long) { imageDao.deleteImage(imageId) }
}