package org.desperu.realestatemanager.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import org.desperu.realestatemanager.model.Estate

@Dao
interface EstateDao {

    @Query("SELECT * FROM Estate WHERE id = :estateId")
    fun getEstate(estateId: Long): LiveData<Estate>

    @get:Query("SELECT * FROM Estate")
    val getAll: List<Estate>

    @Insert
    fun insertEstate(estate: Estate)

    @Update
    fun updateEstate(estate: Estate)

    @Query("DELETE FROM Estate WHERE id = :estateId")
    fun deleteEstate(estateId: Long)
}