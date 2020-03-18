package org.desperu.realestatemanager.repositories

import androidx.lifecycle.LiveData
import org.desperu.realestatemanager.database.dao.AddressDao
import org.desperu.realestatemanager.model.Address

class AddressDataRepository(private val addressDao: AddressDao) {

    // --- GET ---
    fun getAddress(estateId: Long): LiveData<Address> = addressDao.getAddress(estateId)

    // --- CREATE ---
    fun createAddress(address: Address) { addressDao.insertAddress(address) }

    // --- UPDATE ---
    fun updateAddress(address: Address) { addressDao.updateAddress(address) }

    // --- DELETE ---
    fun deleteAddress(estateId: Long) { addressDao.deleteAddress(estateId) }
}