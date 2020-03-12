package org.desperu.realestatemanager.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.desperu.realestatemanager.base.BaseViewModel
import org.desperu.realestatemanager.database.dao.EstateDao
import org.desperu.realestatemanager.database.dao.ImageDao
import org.desperu.realestatemanager.model.Estate
import org.desperu.realestatemanager.model.Image

class EstateViewModel(private val estateDao: EstateDao,
                      private val imageDao: ImageDao,
                      private val estateId: Long): BaseViewModel() {

    // FOR DATA
    private val estate = MutableLiveData<Estate>()
    private val images = MutableLiveData<List<Image>>()

    init {
        estate.value = estateDao.getEstate(estateId).value
        images.value = imageDao.getImages(estateId).value
    }

    // -------------
    // FOR ESTATE
    // -------------

    var getEstate: LiveData<Estate> = estate

    var getImages: LiveData<List<Image>> = images

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