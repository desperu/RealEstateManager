package org.desperu.realestatemanager.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single
import org.desperu.realestatemanager.model.Estate

@Dao
interface EstateDao {

    @Query("SELECT * FROM Estate WHERE id = :estateId")
    fun getEstate(estateId: Long): Flowable<Estate>

    @get:Query("SELECT * FROM Estate")
    val getAll: Flowable<List<Estate>>

    @Insert
    fun insertEstate(estate: Estate): Maybe<Long>

    @Update
    fun updateEstate(estate: Estate): Single<Int>

    @Query("DELETE FROM Estate WHERE id = :estateId")
    fun deleteEstate(estateId: Long): Single<Int>
}