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
}