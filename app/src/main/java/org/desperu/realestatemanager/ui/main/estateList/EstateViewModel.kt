package org.desperu.realestatemanager.ui.main.estateList

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.desperu.realestatemanager.model.Estate
import org.desperu.realestatemanager.model.Image

/**
 * View Model witch provide data for estate item and estate detail.
 *
 * @param givenEstate the given estate data for this view model.
 * @param router the router interface for redirect user.
 *
 * @constructor Instantiates a new EstateViewModel.
 *
 * @property givenEstate the given estate data for this view model to set.
 * @property router the router interface for redirect user to set.
 */
class EstateViewModel(private val givenEstate: Estate,
                      private val router: EstateRouter
): ViewModel() {

    // FOR DATA
    private val estate = MutableLiveData<Estate>()
    private val primaryImage = MutableLiveData<Image>()

    init {
        setEstate()
    }

    // -------------
    // SET GIVEN ESTATE
    // -------------

    /**
     * Set given estate for the view model.
     */
    private fun setEstate() {
        estate.value = givenEstate
        if (!givenEstate.imageList.isNullOrEmpty())
            primaryImage.value = givenEstate.imageList.find { it.isPrimary } ?: givenEstate.imageList[0]
        else
            primaryImage.value = Image()
    }

    // -------------
    // LISTENERS
    // -------------

    /**
     * Item on click listener.
     */
    val itemClick = View.OnClickListener {
        estate.value?.let { estate ->  router.openEstateDetail(estate) }
    }

    /**
     * Item on long click listener.
     */
    val itemLongClick = View.OnLongClickListener {
        estate.value?.let { estate ->  router.openManageEstate(estate) }
        true
    }

    // --- GETTERS ---

    val getEstate: LiveData<Estate> = estate

    val getPrimaryImage: LiveData<Image> = primaryImage
}