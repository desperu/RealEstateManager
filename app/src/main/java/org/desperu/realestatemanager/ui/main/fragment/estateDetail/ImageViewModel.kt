package org.desperu.realestatemanager.ui.main.fragment.estateDetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.desperu.realestatemanager.model.Image

/**
 * View Model witch provide data for image item.
 *
 * @param givenImage the given image data for this view model.
 * @param estateDetailVM the instance of the parent view model.
 *
 * @constructor Instantiates a new ImageViewModel.
 *
 * @property givenImage the given image data for this view model to set.
 * @property estateDetailVM the instance of the parent view model to set.
 */
class ImageViewModel(private val givenImage: Image,
                     private val estateDetailVM: EstateDetailViewModel
): ViewModel() {

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

    // -------------
    // ACTION
    // -------------

    /**
     * On image click, redirect to the show images activity thought interface images router.
     */
    fun onImageClick() = image.value?.let { estateDetailVM.onImageClick(it) }

    // --- GETTERS ---

    val getImage: LiveData<Image> = image
}