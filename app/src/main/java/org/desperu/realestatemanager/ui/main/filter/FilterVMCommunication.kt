package org.desperu.realestatemanager.ui.main.filter

import androidx.appcompat.app.AppCompatActivity
import org.desperu.realestatemanager.model.Estate
import org.desperu.realestatemanager.ui.main.MainCommunication


/**
 * The interface that allows communication for FilterVMCommunication.
 */
interface FilterVMCommunication {

    /**
     * Used to allow view model to update bottom bar color. If hasFilters, set background red,
     * else use original color.
     * @param hasFilters true if has one filter or more set, false otherwise.
     */
    fun updateBottomBarColor(hasFilters: Boolean): Unit?

    /**
     * Used to allow view model to update estate list.
     * @param estateList the new estate list to set.
     * @param hasFilter true if list has filter, false otherwise.
     */
    fun updateEstateList(estateList: List<Estate>, hasFilter: Boolean)

    /**
     * Used to allow view model to close filter fragment.
     * @param toRemove true if remove fragment.
     */
    fun closeFilterFragment(toRemove: Boolean)
}

/**
 * Implementation of the FilterVMCommunication.
 *
 * @property activity the Activity that is used to perform communication.
 *
 * @constructor Instantiates a new FilterVMCommunicationImpl.
 *
 * @param activity the Activity that is used to perform communication to set.
 */
class FilterVMCommunicationImpl(private val activity: AppCompatActivity): FilterVMCommunication {

    /**
     * Get the current fragment of the activity from the view pager.
     */
    private fun getCurrentFragment() =
            (activity as MainCommunication).getFilterFragment()

    /**
     * Used to allow view model to update bottom bar color. If hasFilters, set background red,
     * else use original color.
     * @param hasFilters true if has one filter or more set, false otherwise.
     */
    override fun updateBottomBarColor(hasFilters: Boolean): Unit? =
            (getCurrentFragment() as FilterFragment?)?.setBottomBarColor(hasFilters)

    /**
     * Used to allow view model to update estate list.
     * @param estateList the new estate list to set.
     * @param hasFilter true if list has filter, false otherwise.
     */
    override fun updateEstateList(estateList: List<Estate>, hasFilter: Boolean) =
            (activity as MainCommunication).updateEstateList(estateList, hasFilter)

    /**
     * Used to allow view model to close filter fragment.
     * @param toRemove true if remove fragment.
     */
    override fun closeFilterFragment(toRemove: Boolean) =
            (activity as MainCommunication).closeFilterFragment(toRemove)
}