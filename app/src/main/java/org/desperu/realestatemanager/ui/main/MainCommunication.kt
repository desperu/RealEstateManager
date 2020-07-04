package org.desperu.realestatemanager.ui.main

import org.desperu.realestatemanager.model.Estate
import org.desperu.realestatemanager.ui.main.filter.FilterFragment

/**
 * Interface to allow communications with Main Activity.
 */
interface MainCommunication {

    /**
     * Get the fragment key value.
     */
    fun getFragmentKey(): Int

    /**
     * Set fragment key value.
     * @param fragmentKey the fragment key value to set.
     */
    fun setFragmentKey(fragmentKey: Int)

    /**
     * Populate full estate list to manage filters helper.
     * @param estateList the full estate list to populate.
     */
    fun populateEstateList(estateList: List<Estate>)

    /**
     * Get the current fragment instance, from fragment manager.
     * @return the current Fragment instance.
     */
    fun getFilterFragment(): FilterFragment?

    /**
     * Update estate list after filtered or unfiltered list.
     * @param estateList the new estate list to set.
     */
    fun updateEstateList(estateList: List<Estate>)

    /**
     * Close filter fragment.
     * @param toRemove true if remove fragment.
     */
    fun closeFilterFragment(toRemove: Boolean)

    /**
     * Remove filters and search query in manage filters helper.
     * @param isReload true if is called from swipe refresh to reload data from database.
     */
    fun removeFilters(isReload: Boolean)

    /**
     * Used to allow view model to switch fab filter state.
     * @param hasFilter true if has one filter or more set, false otherwise.
     */
    fun switchFabFilter(hasFilter: Boolean)

    /**
     * Switch search view visibility with animation.
     * @param fromDrawer true if is called from drawer, false otherwise.
     * @param isReload true if is called for a reload data, false otherwise.
     */
    fun switchSearchView(fromDrawer: Boolean, isReload: Boolean)

    /**
     * Redirects the user to the EstateDetail Fragment to show estate details.
     * @param estate the estate to show details.
     * @param isUpdate true if is call for an update, false for first launching data.
     */
    fun showEstateDetail(estate: Estate, isUpdate: Boolean)

    /**
     * True if activity main frame layout 2 is visible, false otherwise.
     */
    val isFrame2Visible: Boolean
}