package org.desperu.realestatemanager.ui.main.estateDetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.desperu.realestatemanager.model.Image

/**
 * View Model witch provide data for image item.
 *
 * @param givenImage the given image data for this view model.
 *
 * @constructor Instantiates a new ImageViewModel.
 *
 * @property givenImage the given image data for this view model to set.
 */
class ImageViewModel(private val givenImage: Image): ViewModel() {

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
    // LISTENER
    // -------------

//    /**
//     * Item on click listener.
//     */
//    val itemClick = View.OnClickListener {
//        estate.value?.let { estate ->  router.openEstateDetail(estate) }
//    }

    // --- GETTERS ---

    val getImage: LiveData<Image> = image
}