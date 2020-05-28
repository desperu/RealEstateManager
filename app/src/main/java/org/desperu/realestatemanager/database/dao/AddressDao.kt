package org.desperu.realestatemanager.database.dao

import android.database.Cursor
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import org.desperu.realestatemanager.model.Address

/**
 * The database access object for address.
 */
@Dao
interface AddressDao {

    /**
     * Returns the address from database ordered for given estate id.
     * @param estateId the estate id to get the corresponding address from database.
     * @return the cursor access for the corresponding address.
     */
    @Query("SELECT * FROM Address WHERE estateId = :estateId")
    fun getAddressWithCursor(estateId: Long): Cursor?

    /**
     * Returns the address from database ordered for the given estate id.
     * @param estateId the estate id to get the corresponding address from database.
     * @return the corresponding address.
     */
    @Query("SELECT * FROM Address WHERE estateId = :estateId")
    suspend fun getAddress(estateId: Long): Address?

    /**
     * Inserts the given address in database.
     * @param address the address to insert in database.
     * @return the row id for the inserted address.
     */
    @Insert
    suspend fun insertAddress(address: Address): Long

    /**
     * Update the given address in database.
     * @param address the address to update in database.
     * @return the number of row affected.
     */
    @Update
    suspend fun updateAddress(address: Address): Int

    /**
     * Delete the address in database for the given image id.
     * @param estateId the estate id to delete the corresponding address in database.
     * @return the number of row affected.
     */
    @Query("DELETE FROM Address WHERE estateId = :estateId")
    suspend fun deleteAddress(estateId: Long): Int
}