package org.desperu.realestatemanager.ui.main.fragment.filter

import androidx.appcompat.app.AppCompatActivity
import org.desperu.realestatemanager.ui.main.MainCommunication


/**
 * The interface that allows communication for FilterVMCommunication.
 */
interface FilterVMCommunication {

    /**
     * Used to allow view model to update bottom bar color. If hasFilters, set background red,
     * else use original color.
     * @param hasFilter true if has one filter or more set, false otherwise.
     */
    fun updateBottomBarColor(hasFilter: Boolean): Unit?

    /**
     * Used to allow view model to close filter fragment.
     * @param toRemove true if remove fragment.
     */
    fun closeFilterFragment(toRemove: Boolean)

    /**
     * Used to allow view model to switch fab filter state.
     * @param hasFilter true if has one filter or more set, false otherwise.
     */
    fun switchFabFilter(hasFilter: Boolean)
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
     * Used to allow view model to update bottom bar color. If hasFilters, set background red,
     * else use original color.
     * @param hasFilter true if has one filter or more set, false otherwise.
     */
    override fun updateBottomBarColor(hasFilter: Boolean): Unit? =
            (activity as MainCommunication).getFilterFragment()?.setBottomBarColor(hasFilter)

    /**
     * Used to allow view model to close filter fragment.
     * @param toRemove true if remove fragment.
     */
    override fun closeFilterFragment(toRemove: Boolean) =
            (activity as MainCommunication).closeFilterFragment(toRemove)

    /**
     * Used to allow view model to switch fab filter state.
     * @param hasFilter true if has one filter or more set, false otherwise.
     */
    override fun switchFabFilter(hasFilter: Boolean) =
            (activity as MainCommunication).switchFabFilter(hasFilter)
}