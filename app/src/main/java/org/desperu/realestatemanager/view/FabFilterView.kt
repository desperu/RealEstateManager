package org.desperu.realestatemanager.view

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import androidx.core.view.updateLayoutParams
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.desperu.realestatemanager.R
import org.desperu.realestatemanager.utils.FILTERED
import org.desperu.realestatemanager.utils.FRAG_ESTATE_DETAIL
import org.desperu.realestatemanager.utils.FRAG_ESTATE_MAP
import org.desperu.realestatemanager.utils.UNFILTERED

/**
 * Custom Floating Action Button for filter list, switch color and icon when a filter is set.
 * Used to switch fab filter visibility, position in it's parent relative layout.
 *
 * @param context the context from this FabFilterView is instantiate.
 * @param attrs the attribute set to apply at this view.
 * @param defStyleAttr the default style to apply at this view.
 *
 * @constructor Instantiate a new FabFilterView.
 */
class FabFilterView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : FloatingActionButton(context, attrs, defStyleAttr) {

    /**
     * Switch fab filter state, normal (unfiltered) or filter when a filter is applied to the list.
     * @param hasFilter true if has filter applied.
     */
    @Suppress("deprecation")
    fun switchFabFilter(hasFilter: Boolean) = apply {
        if (hasFilter) {
            setImageDrawable(resources.getDrawable(R.drawable.ic_baseline_add_white_36))
            backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.bottomBarRed))
            rotation = 45F
            tag = FILTERED
        } else {
            setImageDrawable(resources.getDrawable(R.drawable.ic_baseline_filter_list_black_36))
            backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.filterBackground))
            rotation = 0F
            tag = UNFILTERED
        }
    }

    /**
     * Adapt the fab filter visibility and position, depends of the asked fragment key.
     * @param fragmentKey the asked fragment key.
     * @param isFrame2Visible true if the second frame is visible in main, for tablet mode.
     */
    fun adaptFabFilter(fragmentKey: Int, isFrame2Visible: Boolean) = when {
        !isFrame2Visible && fragmentKey == FRAG_ESTATE_DETAIL -> fabFilterVisibility(true)
        !isFrame2Visible && fragmentKey == FRAG_ESTATE_MAP -> { fabFilterVisibility(false); fabFilterPosition(false) }
        isFrame2Visible -> fabFilterPosition(false)
        else -> { fabFilterVisibility(false); fabFilterPosition(true) }
    }

    /**
     * Set fab filter visibility.
     * @param toHide if true hide fab filter, else show.
     */
    fun fabFilterVisibility(toHide: Boolean) = if (toHide) hide() else show()

    /**
     * Properly position fab filter, depends of toEnd value.
     * @param toEnd true to align with parent end; false to align with parent start.
     */
    private fun fabFilterPosition(toEnd: Boolean) {
        if ((parent as View) is RelativeLayout)
            updateLayoutParams<RelativeLayout.LayoutParams> {
                if (toEnd) {
                    addRule(RelativeLayout.ALIGN_PARENT_END)
                    removeRule(RelativeLayout.ALIGN_PARENT_START)
                } else {
                    addRule(RelativeLayout.ALIGN_PARENT_START)
                    removeRule(RelativeLayout.ALIGN_PARENT_END)
                }
            }
    }
}