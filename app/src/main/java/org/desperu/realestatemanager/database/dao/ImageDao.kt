package org.desperu.realestatemanager.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import org.desperu.realestatemanager.model.Image

@Dao
interface ImageDao {

    @Query("SELECT * FROM Image WHERE id = :imageId")
    fun getImage(imageId: Long): LiveData<Image>

    @Query("SELECT * FROM Image WHERE estateId = :estateId")
    fun getImageList(estateId: Long): LiveData<List<Image>>

    @Insert
    fun insertImage(image: Image)

    @Update
    fun updateImage(image: Image)

    @Query("DELETE FROM Image WHERE id = :imageId")
    fun deleteImage(imageId: Long)
}