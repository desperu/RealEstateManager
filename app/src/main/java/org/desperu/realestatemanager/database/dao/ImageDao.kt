package org.desperu.realestatemanager.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single
import org.desperu.realestatemanager.model.Image

@Dao
interface ImageDao {

    @Query("SELECT * FROM Image WHERE id = :imageId")
    fun getImage(imageId: Long): Flowable<Image>

    @Query("SELECT * FROM Image WHERE estateId = :estateId")
    fun getImageList(estateId: Long): Flowable<List<Image>>

    @Insert
    fun insertImage(image: Image): Maybe<Long>

    @Update
    fun updateImage(image: Image): Single<Int>

    @Query("DELETE FROM Image WHERE id = :imageId")
    fun deleteImage(imageId: Long): Single<Int>

    @Query("DELETE FROM Image WHERE estateId = :estateId")
    fun deleteEstateImages(estateId: Long): Single<Int>
}