package org.desperu.realestatemanager.ui.main.estateMap

import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import org.desperu.realestatemanager.model.Estate
import org.desperu.realestatemanager.ui.main.estateList.EstateRouter

/**
 * View Model witch provide data for map.
 *
 * @param router the estate router interface witch provide user redirection.
 *
 * @constructor Instantiates a new MapsViewModel.
 *
 * @property router the estate router interface witch provide user redirection to set.
 */
class MapsViewModel(private val router: EstateRouter): ViewModel() {

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
     * Update the estate list for the map.
     */
    internal fun updateEstateList() {
        val tempEstateList = estateList.get()
        estateList.set(null)
        estateList.set(tempEstateList)
    }

    // -------------
    // ACTION
    // -------------

    /**
     * Redirect the user to estate detail fragment.
     * @param estate the estate associated with the marker.
     */
    internal fun onInfoClick(estate: Estate?) = estate?.let { router.openEstateDetail(it) }

    // --- GETTERS ---

    val getEstate = estate

    val getEstateList = estateList
}