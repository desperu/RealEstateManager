package org.desperu.realestatemanager.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.desperu.realestatemanager.model.Image

class ImageViewModel(private val givenImage: Image): ViewModel() {

    // FOR DATA
    val image = MutableLiveData<Image>()

    init {
        setGivenImage()
    }

    // -------------
    // SET IMAGE
    // -------------

    private fun setGivenImage() { image.value = givenImage }
}