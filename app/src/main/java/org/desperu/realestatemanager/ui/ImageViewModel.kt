package org.desperu.realestatemanager.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.desperu.realestatemanager.base.BaseViewModel
import org.desperu.realestatemanager.model.Image

class ImageViewModel(private val givenImage: Image): BaseViewModel() {

    // FOR DATA
    val image = MutableLiveData<Image>()

    init {
        setGivenImage()
    }

    // -------------
    // SET IMAGE
    // -------------

    private fun setGivenImage() { image.value = givenImage }

    // --- GETTERS ---

    val getImage: LiveData<Image> = image
}