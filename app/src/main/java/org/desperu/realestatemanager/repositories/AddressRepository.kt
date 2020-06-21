package org.desperu.realestatemanager.repositories

import android.database.Cursor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.desperu.realestatemanager.database.dao.AddressDao
import org.desperu.realestatemanager.model.Address

/**
 * Repository interface to get address data from database.
 */
interface AddressRepository {

    /**
     * Returns the address from database ordered for given estate id.
     * @param estateId the estate id to get the corresponding address from database.
     * @return the cursor access for the corresponding address.
     */
    fun getAddressWithCursor(estateId: Long): Cursor?

    /**
     * Returns the address for the given estate id.
     * @param estateId the estate id for this address.
     * @return the address for the given estate id.
     */
    // --- GET ---
    suspend fun getAddress(estateId: Long): Address

    /**
     * Create the given address in database.
     * @param address the address to create in database.
     * @return the row id of the created address in database.
     */
    // --- CREATE ---
    suspend fun createAddress(address: Address): Long

    /**
     * Update the given address in database.
     * @param address the address to update in database.
     * @return the number of rows affected.
     */
    // --- UPDATE ---
    suspend fun updateAddress(address: Address): Int

    /**
     * Delete address in database for the given estate id.
     * @param estateId the id of the estate to delete corresponding address in database.
     * @return the number of rows affected.
     */
    // --- DELETE ---
    suspend fun deleteAddress(estateId: Long): Int
}

/**
 * Implementation of the AddressRepository interface.
 *
 * @property addressDao the database access object for address.
 *
 * @constructor Instantiates a new AddressRepositoryImpl.
 *
 * @param addressDao the database access object for address to set.
 */
class AddressRepositoryImpl(private val addressDao: AddressDao): AddressRepository {

    /**
     * Returns the address from database ordered for given estate id.
     * @param estateId the estate id to get the corresponding address from database.
     * @return the cursor access for the corresponding address.
     */
    override fun getAddressWithCursor(estateId: Long): Cursor? =
        addressDao.getAddressWithCursor(estateId)

    /**
     * Returns the address from the database.
     * @return the address for the given estate id.
     */
    // --- GET ---
    override suspend fun getAddress(estateId: Long): Address = withContext(Dispatchers.IO) {
        addressDao.getAddress(estateId) ?: Address()
    }

    /**
     * Create the given address in database.
     * @param address the address to create in database.
     * @return the row id of the created address in database.
     */
    // --- CREATE ---
    override suspend fun createAddress(address: Address): Long = withContext(Dispatchers.IO) {
        addressDao.insertAddress(address)
    }

    /**
     * Update the given address in database.
     * @param address the address to update in database.
     * @return the number of rows affected.
     */
    // --- UPDATE ---
    override suspend fun updateAddress(address: Address): Int = withContext(Dispatchers.IO) {
        addressDao.updateAddress(address)
    }

    /**
     * Delete address in database for the given estate id.
     * @param estateId the id of the estate to delete corresponding address in database.
     * @return the number of rows affected.
     */
    // --- DELETE ---
    override suspend fun deleteAddress(estateId: Long): Int = withContext(Dispatchers.IO) {
        addressDao.deleteAddress(estateId)
    }
}