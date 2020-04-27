package org.desperu.realestatemanager.ui

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.android.synthetic.main.item_image.view.*
import org.desperu.realestatemanager.model.Image
import org.desperu.realestatemanager.ui.manageEstate.ManageEstateViewModel

class ImageViewModel(private val givenImage: Image): ViewModel() {

    // FOR DATA
    val image = MutableLiveData<Image>()
    private val primaryVisibility = MutableLiveData<Int>()
    private lateinit var manageEstateViewModel: ManageEstateViewModel

    /**
     * Second constructor to set manage estate view model and allow actions in image list.
     * @param givenImage the given image object to set this view model.
     * @param manageEstateViewModel the instance of parent view model.
     */
    constructor(givenImage: Image, manageEstateViewModel: ManageEstateViewModel): this(givenImage) {
        this.manageEstateViewModel = manageEstateViewModel
    }

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
     * Set mutable visibility for primary marker.
     */
    internal fun setPrimaryVisibility() {
        primaryVisibility.value = if (image.value?.isPrimary!!) View.VISIBLE else View.GONE
    }

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
                manageEstateViewModel.managePrimaryImage(this)
            }

            // Delete this image.
            "delete" -> manageEstateViewModel.removeImage(this)
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
        val rotation = imageView.rotation + if (clickedButton.tag == "turnRight") 90F else -90F
        imageView.rotation = rotation
        image.value?.rotation = rotation
    }

    // --- GETTERS ---

    val getMutableVisibility = primaryVisibility

    val getButtonListener = buttonListener
}