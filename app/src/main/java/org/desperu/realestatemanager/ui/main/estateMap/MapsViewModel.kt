package org.desperu.realestatemanager.ui.main.estateMap

import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import org.desperu.realestatemanager.model.Estate
import org.desperu.realestatemanager.ui.main.MainCommunication
import org.desperu.realestatemanager.ui.main.estateList.EstateRouter

/**
 * View Model witch provide data for map.
 *
 * @param router the estate router interface witch provide user redirection.
 * @param communication the main communication interface that allow communication with activity.
 *
 * @constructor Instantiates a new MapsViewModel.
 *
 * @property router the estate router interface witch provide user redirection to set.
 * @property communication the main communication interface that allow communication with activity to set.
 */
class MapsViewModel(private val router: EstateRouter,
                    private var communication: MainCommunication
): ViewModel() {

    // FOR DATA
    private val estate = ObservableField<Estate>()
    private val estateList = ObservableField<List<Estate>>()

    // -------------
    // SET DATA
    // -------------

    /**
     * Set estate for the view model.
     * @param givenEstate the given estate to set.
     */
    internal fun setEstate(givenEstate: Estate) { estate.set(givenEstate) }

    /**
     * Set estate list for the view model.
     * @param givenEstateList the given estate list to set.
     */
    internal fun setEstateList(givenEstateList: List<Estate>) { estateList.set(givenEstateList) }

    // -------------
    // UPDATE UI
    // -------------

    /**
     * Update the estate for the map, in estate, if new estate is null, set original estate..
     * @param newEstate the new estate to set.
     */
    internal fun updateEstate(newEstate: Estate?) {
        estate.set(null)
        estate.set(newEstate)
    }

    /**
     * Update the estate list for the map, if new list is null, set original list..
     * @param newEstateList the new estate list to set.
     */
    internal fun updateEstateList(newEstateList: List<Estate>?) {
        val tempEstateList = estateList.get()
        estateList.set(newEstateList)
        newEstateList ?: estateList.set(tempEstateList)
    }

    // -------------
    // ACTION
    // -------------

    /**
     * Redirect the user to estate detail fragment.
     * @param estate the estate associated with the marker.
     */
    internal fun onInfoClick(estate: Estate?) = estate?.let {
        if (communication.isFrame2Visible)
            router.openEstateDetailForTablet(it, false)
        else
            router.openEstateDetail(it)
    }

    // --- GETTERS ---

    val getEstate = estate

    val getEstateList = estateList
}