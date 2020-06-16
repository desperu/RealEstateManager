package org.desperu.realestatemanager.ui.manageEstate.fragment

import android.view.View
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.android.synthetic.main.item_manage_image.view.*
import org.desperu.realestatemanager.model.Image

/**
 * View Model witch provide data for image item.
 *
 * @param givenImage the given image data for this view model.
 * @param manageEstateVM the instance of parent view model.
 *
 * @constructor Instantiates a new ManageImageViewModel.
 *
 * @property givenImage the given image data for this view model to set.
 * @property manageEstateVM the instance of parent view model to set.
 */
class ManageImageViewModel(private val givenImage: Image,
                           private val manageEstateVM: ManageEstateViewModel
): ViewModel() {

    // FOR DATA
    val image = MutableLiveData<Image>()
    private val showPrimary = ObservableBoolean()

    init {
        setGivenImage()
        setPrimaryVisibility()
    }

    // -------------
    // SET IMAGE
    // -------------

    /**
     * Set view model with given image.
     */
    private fun setGivenImage() { image.value = givenImage }

    /**
     * Set visibility for primary marker.
     */
    internal fun setPrimaryVisibility() = showPrimary.set(image.value?.isPrimary!!)

    // -------------
    // LISTENER
    // -------------

    /**
     * Button on click listener, execute corresponding action, depends of view tag.
     */
    private val buttonListener = View.OnClickListener { v ->
        when (v.tag) {
            // Turn image to right or left.
            "turnRight", "turnLeft" -> turnImage(v)

            // Set or remove as primary.
            "primary" -> {
                image.value?.isPrimary = image.value?.isPrimary?.not()!!
                setPrimaryVisibility()
                manageEstateVM.managePrimaryImage(this)
            }

            // Delete this image.
            "delete" -> manageEstateVM.removeImage(this)
        }
    }

    // -------------
    // UTILS
    // -------------

    /**
     * Turn image to right or left, and save rotation in image object.
     * @param clickedButton the clicked button to determinate orientation rotation, right or left.
     */
    private fun turnImage(clickedButton: View) {
        val imageView = (clickedButton.parent.parent as View).item_image_photo
        var rotation = imageView.rotation + if (clickedButton.tag == "turnRight") 90F else 270F
        while (rotation >= 360) rotation -= 360
        imageView.rotation = rotation
        image.value?.rotation = rotation
    }

    // --- GETTERS ---

    val getShowPrimary = showPrimary

    val getButtonListener = buttonListener
}