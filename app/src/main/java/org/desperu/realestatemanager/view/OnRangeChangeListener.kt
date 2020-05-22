package org.desperu.realestatemanager.view

import android.view.View

/**
 * Custom Crystal Seek Bar On Range Change Listener interface, with differentiate action selected and unselected.
 */
interface OnRangeChangeListener {

    /**
     * Called when a range was selected, if min > 1 or max < 99.
     * @param view the custom seek bar view instance.
     * @param minValue the selected min value.
     * @param maxValue the selected max value.
     */
    fun onRangeSelected(view: View, minValue: Number, maxValue: Number)

    /**
     * Called when a range was unselected, min < 1 and max > 99.
     * @param view the custom seek bar view instance.
     */
    fun onRangeUnselected(view: View)
}