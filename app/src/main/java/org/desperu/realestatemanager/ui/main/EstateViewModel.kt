package org.desperu.realestatemanager.ui.main

import androidx.lifecycle.MutableLiveData
import org.desperu.realestatemanager.base.BaseViewModel
import org.desperu.realestatemanager.model.Address
import org.desperu.realestatemanager.model.Estate
import org.desperu.realestatemanager.model.Image

class EstateViewModel(private val givenEstate: Estate): BaseViewModel() {

    // FOR DATA
    val estate = MutableLiveData<Estate>()
//    val images = MutableLiveData<List<Image>>()
//    val address = MutableLiveData<Address>()
    val primaryImage = MutableLiveData<Image>()

    init {
        setEstate(givenEstate)
    }

    fun bind(estate: Estate) {
        this.estate.value = estate
//        this.images.value = estate.images
//        this.address.value = estate.address
        this.primaryImage.value = estate.images[0] // TODO use boolean master photo
    }

    // -------------
    // SET ESTATE
    // -------------

    private fun setEstate(estate: Estate) {
        this.estate.value = estate
//        this.images.value = estate.images
//        this.address.value = estate.address
        this.primaryImage.value = estate.images[0] // TODO use boolean master photo
    }

    // --- GETTERS ---

//    val getEstate: LiveData<Estate> = this.estate // TODO getter or public field?

//    val getImages: LiveData<List<Image>> = images

//    val getPrimaryImage: LiveData<Image> = primaryImage

    // --- MANAGE ---

//    fun insertEstate(estate: Estate) {
//        estateDataRepository.createEstate(estate)
//    }
//
//    fun updateEstate(estate: Estate) {
//        estateDataRepository.updateEstate(estate)
//    }
//
//    fun deleteEstate(estateId: Long) {
//        estateDataRepository.deleteEstate(estateId)
//    }
}