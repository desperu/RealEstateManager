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
 * @param givenEstate the given estate data for this view model.
 *
 * @constructor Instantiates a new EstateDetailViewModel.
 *
 * @property givenEstate the given estate data for this view model to set.
 */
class EstateDetailViewModel(private val givenEstate: Estate): ViewModel() {

    // FOR DATA
    private val imageListAdapter = RecyclerViewAdapter(R.layout.item_image)
    private val estate = ObservableField<Estate>()

    init {
        setEstate()
        setImageList(givenEstate)
    }

    // -------------
    // SET ESTATE
    // -------------

    /**
     * Set given estate for the view model.
     */
    private fun setEstate() { estate.set(givenEstate) }

    /**
     * Update estate data.
     * @param newEstate the new estate to set.
     */
    internal fun updateEstate(newEstate: Estate?) {
        if (newEstate?.id == givenEstate.id) {
            estate.set(newEstate)
            setImageList(newEstate)
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
        val imageVMList = estate.imageList.map { ImageViewModel(it) }.toMutableList()
        if (imageVMList.isEmpty()) imageVMList.add(ImageViewModel(Image()))
        imageListAdapter.updateList(imageVMList as MutableList<Any>)
        imageListAdapter.notifyDataSetChanged()
    }

    // --- GETTERS ---

    val getImageListAdapter = imageListAdapter

    val getEstate = estate
}