package org.desperu.realestatemanager.ui

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.android.synthetic.main.item_image.view.*
import org.desperu.realestatemanager.model.Image

class ImageViewModel(private val givenImage: Image): ViewModel() {

    // FOR DATA
    val image = MutableLiveData<Image>()
    private val mutableVisibility = MutableLiveData<Int>()

    init {
        setGivenImage()
    }

    // -------------
    // SET IMAGE
    // -------------

    /**
     * Set view model with given image.
     */
    private fun setGivenImage() {
        image.value = givenImage
        mutableVisibility.value = setMutableVisibility()
    }

    /**
     * Set mutable visibility for primary marker.
     */
    private fun setMutableVisibility(): Int = if (image.value?.isPrimary!!) View.VISIBLE else View.GONE

    // -------------
    // LISTENER
    // -------------

    /**
     * Button on click listener.
     */
    val buttonListener = View.OnClickListener { v ->
        when (v?.tag) {
            "turnRight" -> v.rootView.item_image_photo.rotation = v.rootView.item_image_photo.rotation + 90F // Turn image, save new image delete old...
            "primary" -> { image.value?.isPrimary = image.value?.isPrimary?.not()!!; mutableVisibility.value = setMutableVisibility() }
            "delete" -> "to delete"
            "turnLeft" -> v.rootView.item_image_photo.rotation = v.rootView.item_image_photo.rotation - 90F // Turn image, save new image delete old...
        }
    }

    // --- GETTERS ---

    val getMutableVisibility = mutableVisibility
}