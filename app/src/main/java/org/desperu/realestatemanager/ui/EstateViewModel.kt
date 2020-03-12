package org.desperu.realestatemanager.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.desperu.realestatemanager.base.BaseViewModel
import org.desperu.realestatemanager.model.Estate
import org.desperu.realestatemanager.model.Image
import org.desperu.realestatemanager.repositories.EstateDataRepository
import org.desperu.realestatemanager.repositories.ImageDataRepository

class EstateViewModel(private val estateDataRepository: EstateDataRepository,
                      private val imageDataRepository: ImageDataRepository,
                      private val estateId: Long): BaseViewModel() {

    // FOR DATA
    private val estate = MutableLiveData<Estate>()
    private val images = MutableLiveData<List<Image>>()

    init {
        estate.value = estateDataRepository.getEstate(estateId).value
        images.value = imageDataRepository.getImages(estateId).value
    }

    // -------------
    // FOR ESTATE
    // -------------

    var getEstate: LiveData<Estate> = estate

    var getImages: LiveData<List<Image>> = images

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