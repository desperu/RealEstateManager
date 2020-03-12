package org.desperu.realestatemanager.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.desperu.realestatemanager.base.BaseViewModel
import org.desperu.realestatemanager.model.Estate
import org.desperu.realestatemanager.repositories.EstateDataRepository

class EstateListViewModel(private val estateDataRepository: EstateDataRepository): BaseViewModel() {

    // FOR DATA
    private val estate = MutableLiveData<List<Estate>>()

    init {
        estate.value = estateDataRepository.getAll.value
    }

    // -------------
    // FOR ESTATE
    // -------------

    fun getEstate(estateId: Long): LiveData<Estate> = estateDataRepository.getEstate(estateId)

    val getAll: MutableLiveData<List<Estate>> = estate

    fun insertEstate(estate: Estate) {
        estateDataRepository.createEstate(estate)
    }

    fun updateEstate(estate: Estate) {
        estateDataRepository.updateEstate(estate)
    }

    fun deleteEstate(estateId: Long) {
        estateDataRepository.deleteEstate(estateId)
    }
}