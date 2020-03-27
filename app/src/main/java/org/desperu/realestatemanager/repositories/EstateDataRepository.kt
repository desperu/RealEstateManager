package org.desperu.realestatemanager.repositories

import androidx.lifecycle.LiveData
import org.desperu.realestatemanager.database.dao.EstateDao
import org.desperu.realestatemanager.model.Estate

class EstateDataRepository(private val estateDao: EstateDao) {

    // --- GET ---
    fun getEstate(estateId: Long): LiveData<Estate> = estateDao.getEstate(estateId)

    // --- GET ALL ---
    val getAll = estateDao.getAll

    // --- CREATE ---
    fun createEstate(estate: Estate): Long { return estateDao.insertEstate(estate) }

    // --- UPDATE ---
    fun updateEstate(estate: Estate) { estateDao.updateEstate(estate) }

    // --- DELETE ---
    fun deleteEstate(estateId: Long) { estateDao.deleteEstate(estateId) }
}