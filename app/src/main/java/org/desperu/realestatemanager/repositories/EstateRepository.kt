package org.desperu.realestatemanager.repositories

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.desperu.realestatemanager.database.dao.EstateDao
import org.desperu.realestatemanager.model.Estate

/**
 * Repository interface to get estate data from database.
 */
interface EstateRepository {

    /**
     * Returns the estate for the given estate id.
     * @param estateId the estate id for this estate.
     * @return the estate for the given estate id.
     */
    // --- GET ---
    suspend fun getEstate(estateId: Long): Estate

    /**
     * Returns all estates from database.
     * @return all estates from database.
     */
    // --- GET ALL ---
    suspend fun getAll(): List<Estate>

    /**
     * Create the given estate in database.
     * @param estate the estate to create in database.
     * @return the row id of the created estate in database.
     */
    // --- CREATE ---
    suspend fun createEstate(estate: Estate): Long

    /**
     * Update the given estate in database.
     * @param estate the estate to update in database.
     * @return the number of rows affected.
     */
    // --- UPDATE ---
    suspend fun updateEstate(estate: Estate): Int

    /**
     * Delete estate in database for the given estate id.
     * @param estateId the id of the estate to delete in database.
     * @return the number of rows affected.
     */
    // --- DELETE ---
    suspend fun deleteEstate(estateId: Long): Int
}

/**
 * Implementation of the EstateRepository interface.
 *
 * @property estateDao the database access object for estate.
 *
 * @constructor Instantiates a new AddressRepositoryImpl.
 *
 * @param estateDao the database access object for estate to set.
 */
class EstateRepositoryImpl(private val estateDao: EstateDao): EstateRepository {

    /**
     * Returns the estate for the given estate id from database.
     * @param estateId the id of the estate to get from database.
     * @return the estate for the given estate id.
     */
    // --- GET ---
    override suspend fun getEstate(estateId: Long): Estate = withContext(Dispatchers.IO) {
        estateDao.getEstate(estateId)
    }

    /**
     * Returns all estates from database.
     * @return all estates from database.
     */
    // --- GET ALL ---
    override suspend fun getAll() = withContext(Dispatchers.IO) { estateDao.getAll() }

    /**
     * Create the given estate in database.
     * @param estate the estate to create in database.
     * @return the row id of the created estate in database.
     */
    // --- CREATE ---
    override suspend fun createEstate(estate: Estate): Long = withContext(Dispatchers.IO) {
        estateDao.insertEstate(estate)
    }

    /**
     * Update the given estate in database.
     * @param estate the estate to update in database.
     * @return the number of rows affected.
     */
    // --- UPDATE ---
    override suspend fun updateEstate(estate: Estate): Int = withContext(Dispatchers.IO) {
        estateDao.updateEstate(estate)
    }

    /**
     * Delete estate in database for the given estate id.
     * @param estateId the id of the estate to delete in database.
     * @return the number of rows affected.
     */
    // --- DELETE ---
    override suspend fun deleteEstate(estateId: Long): Int = withContext(Dispatchers.IO) {
        estateDao.deleteEstate(estateId)
    }
}