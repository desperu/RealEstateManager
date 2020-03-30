package org.desperu.realestatemanager.repositories

import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single
import org.desperu.realestatemanager.database.dao.ImageDao
import org.desperu.realestatemanager.model.Image

class ImageDataRepository(private val imageDao: ImageDao) {

    // --- GET ---
    fun getImage(imageId: Long) = imageDao.getImage(imageId)

    // --- GET LIST ---
    fun getImageList(estateId: Long): Flowable<List<Image>> = imageDao.getImageList(estateId)

    // --- CREATE ---
    fun createImage(image: Image): Maybe<Long> { return imageDao.insertImage(image) }

    // --- CREATE LIST ---
    fun createImageList(imageList: List<Image>): ArrayList<Long> {
        val rowsIds = ArrayList<Long>()
        for (image in imageList)
            rowsIds.add(imageDao.insertImage(image).blockingGet())
        return rowsIds
    }

    // --- UPDATE ---
    fun updateImage(image: Image): Single<Int> { return imageDao.updateImage(image) }

    // --- UPDATE LIST ---
    fun updateImageList(imageList: List<Image>): Int {
        var updatedRows = 0
        for (image in imageList)
            updatedRows += imageDao.updateImage(image).blockingGet()
        return updatedRows
    }

    // --- DELETE ---
    fun deleteImage(imageId: Long): Single<Int> { return imageDao.deleteImage(imageId) }

    // --- DELETE LIST ---
    fun deleteEstateImages(estateId: Long): Single<Int> { return imageDao.deleteEstateImages(estateId) }
}