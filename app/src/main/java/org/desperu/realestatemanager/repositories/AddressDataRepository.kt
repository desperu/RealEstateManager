package org.desperu.realestatemanager.repositories

import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single
import org.desperu.realestatemanager.database.dao.AddressDao
import org.desperu.realestatemanager.model.Address

class AddressDataRepository(private val addressDao: AddressDao) {

    // --- GET ---
    fun getAddress(estateId: Long): Flowable<Address> = addressDao.getAddress(estateId)

    // --- CREATE ---
    fun createAddress(address: Address): Maybe<Long> { return addressDao.insertAddress(address) }

    // --- UPDATE ---
    fun updateAddress(address: Address): Single<Int> { return addressDao.updateAddress(address) }

    // --- DELETE ---
    fun deleteAddress(estateId: Long): Single<Int> { return addressDao.deleteAddress(estateId) }
}