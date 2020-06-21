package org.desperu.realestatemanager.database.dao

import android.database.Cursor
import androidx.room.*
import org.desperu.realestatemanager.model.Estate

/**
 * The database access object for estate.
 */
@Dao
interface EstateDao {

    /**
     * Returns the estate from database ordered for given estate id.
     * @param estateId the estate id to get the corresponding estate from database.
     * @return the cursor access for the corresponding estate.
     */
    @Query("SELECT * FROM Estate WHERE id = :estateId")
    fun getEstateWithCursor(estateId: Long): Cursor?

    /**
     * Returns the estate from database ordered for given estate id.
     * @param estateId the estate id to get the corresponding estate from database.
     * @return the corresponding estate.
     */
    @Query("SELECT * FROM Estate WHERE id = :estateId")
    suspend fun getEstate(estateId: Long): Estate

    /**
     * Returns the estate list from database ordered from the most recent to the oldest.
     * @return the estate list from database ordered from the most recent to the oldest.
     */
    @Transaction
    @Query("SELECT * FROM Estate ORDER BY createdTime DESC")
    suspend fun getAll(): List<Estate>

    /**
     * Inserts the given estate in database.
     * @param estate the estate to insert in database.
     * @return the row id for the inserted estate.
     */
    @Insert
    suspend fun insertEstate(estate: Estate): Long

    /**
     * Update the given estate in database.
     * @param estate the estate to update in database.
     * @return the number of row affected.
     */
    @Update
    suspend fun updateEstate(estate: Estate): Int

    /**
     * Delete the estate in database for the given estate id.
     * @param estateId the estate id to delete in database.
     * @return the number of row affected.
     */
    @Query("DELETE FROM Estate WHERE id = :estateId")
    suspend fun deleteEstate(estateId: Long): Int
}