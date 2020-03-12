package org.desperu.realestatemanager.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.desperu.realestatemanager.base.BaseViewModel
import org.desperu.realestatemanager.database.dao.EstateDao
import org.desperu.realestatemanager.model.Estate

class EstateListViewModel(private val estateDao: EstateDao): BaseViewModel() {

    // FOR DATA
    private val estate = MutableLiveData<List<Estate>>()

    init {
        estate.value = estateDao.getAll
    }

    // -------------
    // FOR ESTATE
    // -------------

    fun getEstate(estateId: Long): LiveData<Estate> = estateDao.getEstate(estateId)

    val getAll: MutableLiveData<List<Estate>> = estate

    fun insertEstate(estate: Estate) {
        estateDao.insertEstate(estate)
    }

    fun updateEstate(estate: Estate) {
        estateDao.insertEstate(estate)
    }

    fun deleteEstate(estateId: Long) {
        estateDao.deleteEstate(estateId)
    }
}