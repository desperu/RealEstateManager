package org.desperu.realestatemanager.repositories

import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single
import org.desperu.realestatemanager.database.dao.EstateDao
import org.desperu.realestatemanager.model.Estate

class EstateDataRepository(private val estateDao: EstateDao) {

    // --- GET ---
    fun getEstate(estateId: Long): Flowable<Estate> = estateDao.getEstate(estateId)

    // --- GET ALL ---
    val getAll = estateDao.getAll

    // --- CREATE ---
    fun createEstate(estate: Estate): Maybe<Long> { return estateDao.insertEstate(estate) }

    // --- UPDATE ---
    fun updateEstate(estate: Estate): Single<Int> { return estateDao.updateEstate(estate) }

    // --- DELETE ---
    fun deleteEstate(estateId: Long): Single<Int> { return estateDao.deleteEstate(estateId) }
}