package org.desperu.realestatemanager.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import org.desperu.realestatemanager.model.Address

@Dao
interface AddressDao {

    @Query("SELECT * FROM address WHERE estateId = :estateId")
    fun getAddress(estateId: Long): LiveData<Address>

    @Insert
    fun insertAddress(address: Address)

    @Update
    fun updateAddress(address: Address)

    @Query("DELETE FROM address WHERE estateId = :estateId")
    fun deleteAddress(estateId: Long)
}