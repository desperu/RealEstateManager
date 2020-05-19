package org.desperu.realestatemanager.ui.showImages

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.desperu.realestatemanager.model.Image


/**
 * View Model witch provide data for show image fragment.
 *
 * @param givenImage the given image data for this view model.
 *
 * @constructor Instantiates a new ShowImageViewModel.
 *
 * @property givenImage the given image data for this view model to set.
 */
class ShowImageViewModel(private val givenImage: Image): ViewModel() {

    // FOR DATA
    private val image = MutableLiveData<Image>()

    init {
        setGivenImage()
    }

    // -------------
    // SET IMAGE
    // -------------

    /**
     * Set view model with given image.
     */
    private fun setGivenImage() { image.value = givenImage }

    // --- GETTERS ---

    val getImage: LiveData<Image> = image
}