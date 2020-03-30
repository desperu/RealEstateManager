package org.desperu.realestatemanager.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.desperu.realestatemanager.base.BaseViewModel
import org.desperu.realestatemanager.model.Estate
import org.desperu.realestatemanager.model.Image

class EstateViewModel(private val givenEstate: Estate): BaseViewModel() {

    // FOR DATA
    private val estate = MutableLiveData<Estate>()
    private val primaryImage = MutableLiveData<Image>()

    init {
        setEstate()
    }

    // -------------
    // SET GIVEN ESTATE
    // -------------

    /**
     * Set given estate for the view model.
     */
    private fun setEstate() {
        estate.value = givenEstate
        if (!givenEstate.imageList.isNullOrEmpty()) {
            for (image in givenEstate.imageList)
                if (image.isPrimary) primaryImage.value = image
            if (primaryImage.value == null)
                primaryImage.value = givenEstate.imageList[0]
        } else
            primaryImage.value = Image()
    }

    // --- GETTERS ---

    val getEstate: LiveData<Estate> = estate

    val getPrimaryImage: LiveData<Image> = primaryImage
}