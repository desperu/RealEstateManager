package org.desperu.realestatemanager.ui.main.estateDetail

import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import org.desperu.realestatemanager.R
import org.desperu.realestatemanager.model.Estate
import org.desperu.realestatemanager.model.Image
import org.desperu.realestatemanager.view.RecyclerViewAdapter

/**
 * View Model witch provide data for estate detail.
 *
 * @param router the images router interface witch provide user redirection.
 *
 * @constructor Instantiates a new EstateDetailViewModel.
 *
 * @property router the estate router interface witch provide user redirection to set.
 */
class EstateDetailViewModel(private val router: ImagesRouter): ViewModel() {

    // FOR DATA
    private val imageListAdapter = RecyclerViewAdapter(R.layout.item_image)
    private val estate = ObservableField<Estate>()

    // -------------
    // SET ESTATE
    // -------------

    /**
     * Set given estate and the estate's image list for the view model.
     * @param givenEstate the given estate to set.
     */
    internal fun setEstate(givenEstate: Estate) {
        estate.set(givenEstate)
        setImageList(givenEstate)
    }

    /**
     * Update estate data.
     * @param newEstate the new estate to set.
     */
    internal fun updateEstate(newEstate: Estate?) {
        if (newEstate?.id == estate.get()?.id) {
            estate.set(newEstate)
            newEstate?.let { setImageList(it) }
        }
    }

    // -------------
    // UPDATE UI
    // -------------

    /**
     * Push estate's image list to recycler view adapter.
     * @param estate the estate's image list to set.
     */
    @Suppress("unchecked_cast")
    private fun setImageList(estate: Estate) {
        val imageVMList = estate.imageList.map { ImageViewModel(it, this) }.toMutableList()
        if (imageVMList.isEmpty()) imageVMList.add(ImageViewModel(Image(), this))
        imageListAdapter.updateList(imageVMList as MutableList<Any>)
        imageListAdapter.notifyDataSetChanged()
    }

    // -------------
    // REDIRECTION
    // -------------

    /**
     * Redirect the user to the show images activity, with the clicked image position.
     * @param image the clicked image.
     */
    internal fun onImageClick(image: Image) {
        estate.get()?.imageList?.let {
            router.openShowImages(it as ArrayList<Image>, it.indexOf(image))
        }
    }

    // --- GETTERS ---

    val getImageListAdapter = imageListAdapter

    val getEstate = estate
}