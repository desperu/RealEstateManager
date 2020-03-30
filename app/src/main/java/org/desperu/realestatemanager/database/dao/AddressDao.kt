package org.desperu.realestatemanager.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single
import org.desperu.realestatemanager.model.Address

@Dao
interface AddressDao {

    @Query("SELECT * FROM address WHERE estateId = :estateId")
    fun getAddress(estateId: Long): Flowable<Address>

    @Insert
    fun insertAddress(address: Address): Maybe<Long>

    @Update
    fun updateAddress(address: Address): Single<Int>

    @Query("DELETE FROM address WHERE estateId = :estateId")
    fun deleteAddress(estateId: Long): Single<Int>
}