package org.desperu.realestatemanager.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.desperu.realestatemanager.base.BaseViewModel
import org.desperu.realestatemanager.model.Image

class ImageViewModel(private val givenImage: Image): BaseViewModel() {

    // FOR DATA
    private val image = MutableLiveData<Image>()

    init {
        setImage()
    }

    // -------------
    // SET IMAGE
    // -------------

    private fun setImage() { image.value = givenImage }

    // --- GETTERS ---

    val getImage: LiveData<Image> = image // TODO getter or public field?
}