package org.desperu.realestatemanager.ui.main.fragment.estateList

import android.view.View
import androidx.databinding.ObservableBoolean
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
 * @param estateListVM the instance of parent view model.
 *
 * @constructor Instantiates a new EstateViewModel.
 *
 * @property givenEstate the given estate data for this view model to set.
 * @property router the router interface for redirect user to set.
 * @property estateListVM the instance of parent view model to set.
 */
class EstateViewModel(private val givenEstate: Estate,
                      private val router: EstateRouter,
                      private val estateListVM: EstateListViewModel
): ViewModel() {

    // FOR DATA
    private val estate = MutableLiveData<Estate>()
    private val primaryImage = MutableLiveData<Image>()
    private val isSelected = ObservableBoolean(false)

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
        primaryImage.value =
                if (!givenEstate.imageList.isNullOrEmpty())
                    givenEstate.imageList.find { it.isPrimary } ?: givenEstate.imageList[0]
                else
                    Image()
    }

    // -------------
    // LISTENERS
    // -------------

    /**
     * Item on click listener.
     */
    val itemClick = View.OnClickListener {
        estate.value?.let { estate ->
            estateListVM.switchSelectedItem(estate)
            router.openEstateDetail(estate, true)
        }
    }

    /**
     * Item on long click listener.
     */
    val itemLongClick = View.OnLongClickListener {
        estate.value?.let { estate -> router.openManageEstate(estate) }
        true
    }

    // --- SETTERS ---

    internal fun setIsSelected(isSelected: Boolean) { this.isSelected.set(isSelected) }

    // --- GETTERS ---

    val getEstate: LiveData<Estate> = estate

    val getPrimaryImage: LiveData<Image> = primaryImage

    val getIsSelected = isSelected
}