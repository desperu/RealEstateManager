package org.desperu.realestatemanager.ui.main

import org.desperu.realestatemanager.model.Estate
import org.desperu.realestatemanager.ui.main.filter.FilterFragment

/**
 * Interface to allow communications with Main Activity.
 */
interface MainCommunication {

    /**
     * Get the current fragment instance, from fragment manager.
     * @return the current Fragment instance.
     */
    fun getFilterFragment(): FilterFragment?

    /**
     * Update estate list after filtered or unfiltered list.
     * @param estateList the new estate list to set.
     * @param hasFilter true if list has filter, false otherwise.
     */
    fun updateEstateList(estateList: List<Estate>, hasFilter: Boolean)

    /**
     * Close filter fragment.
     * @param toRemove true if remove fragment.
     */
    fun closeFilterFragment(toRemove: Boolean)

    /**
     * Redirects the user to the EstateDetail Fragment to show estate details.
     * @param estate the estate to show details in the EstateDetail Fragment.
     */
    fun showEstateDetailFragment(estate: Estate)

    /**
     *
     * @param estate the estate to show details.
     * @param isUpdate true if is call for an update, false for first launching data.
     */
    fun showDetailForTablet(estate: Estate, isUpdate: Boolean)

    /**
     * Populate full estate list to main.
     * @param estateList the full estate list to populate.
     */
    fun populateEstateListToMain(estateList: List<Estate>)

    /**
     * True if activity main frame layout 2 is visible, false otherwise.
     */
    val isFrame2Visible: Boolean
}