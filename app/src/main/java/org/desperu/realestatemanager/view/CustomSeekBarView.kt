package org.desperu.realestatemanager.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar
import kotlinx.android.synthetic.main.fragment_filter.view.*
import org.desperu.realestatemanager.R
import org.desperu.realestatemanager.utils.Utils.convertPriceToPatternPrice

/**
 * Custom crystal range seek bar, switch color when a value is selected, to make the toggling seem more smooth,
 * and show min and max values in text view.
 *
 * @param context the context from this CustomSeekBarView is instantiate.
 * @param attrs the attribute set to apply at this view.
 * @param defStyleAttr the default style to apply at this view.
 *
 * @constructor Instantiate a new CustomSeekBarView.
 */
@Suppress("deprecation")
class CustomSeekBarView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : CrystalRangeSeekbar(context, attrs, defStyleAttr) {

    // FOR DATA
    private var parentView: View? = null
    private var listener: OnRangeChangeListener? = null
    private val selectedColor = resources.getColor(R.color.selectedFilter)
    private val selectedBarColor = resources.getColor(R.color.selectedBar)
    private val selectedValueColor = resources.getColor(R.color.colorPassedWhite)
    private val unselectedColor = resources.getColor(R.color.unselectedFilter)
    private val unselectedBarColor = resources.getColor(R.color.unselectedBar)

    // Custom thumbs and bar size.
    override fun getThumbWidth(): Float = resources.getDimension(R.dimen.thumb_size) // TODO use 30dp and min range to 10
    override fun getThumbHeight(): Float = resources.getDimension(R.dimen.thumb_size)
    override fun getBarHeight(): Float = thumbHeight / 4.5f

    init { // Set original Crystal Seek Bar Listener with super method.
        super.setOnRangeSeekbarChangeListener { minValue, maxValue ->
            switchSeekBarColors(minValue.toInt(), maxValue.toInt())
            setTextMin(minValue.toInt())
            setTextMax(maxValue.toInt())
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        parentView = parent as View
    }

    /**
     * Set on range change listener.
     * @param onRangeChangeListener the listener to set.
     */
    fun setOnRangeChangeListener(onRangeChangeListener: OnRangeChangeListener) {
        listener = onRangeChangeListener
    }

    /**
     * Switch thumbs, bar and value colors. Sliding the seekbar between 1f..99f toggles it on.
     * 1f and 99f are chosen just to make the toggling seem more smooth.
     * Call appropriate callback depends of range selected/unselected.
     * @param minValue the selected min value.
     * @param maxValue the selected max value.
     */
    private fun switchSeekBarColors(minValue: Int, maxValue: Int) {
        var thumbColor = unselectedColor
        var barColor = unselectedBarColor
        var valueColor = unselectedColor

        if (!(minValue.toFloat() < 1f && maxValue.toFloat() > 99f)) {
            listener?.onRangeSelected(this, getCorrectedValue(minValue), getCorrectedValue(maxValue))
            thumbColor = selectedColor
            barColor = selectedBarColor
            valueColor = selectedValueColor
        } else if (minValue.toFloat() < 1f && maxValue.toFloat() > 99f) {
            listener?.onRangeUnselected(this)
            thumbColor = unselectedColor
            barColor = unselectedBarColor
            valueColor = unselectedColor
        }

        setLeftThumbHighlightColor(thumbColor)
        setRightThumbHighlightColor(thumbColor)
        setLeftThumbColor(thumbColor)
        setRightThumbColor(thumbColor)
        setBarHighlightColor(barColor)
        getTvMin()?.setTextColor(valueColor)
        getTvMax()?.setTextColor(valueColor)
    }

    /**
     * Set text min with the selected min value.
     * @param minValue the selected min value to set.
     */
    private fun setTextMin(minValue: Int) {
        val correctVal = getCorrectedValue(minValue)
        getTvMin()?.text =
                if (id == R.id.fragment_filter_seekbar_price) getPatternPrice(correctVal)
                else correctVal.toString()
    }

    /**
     * Set text max with the selected max value.
     * @param maxValue the selected max value to set.
     */
    private fun setTextMax(maxValue: Int) {
        val correctVal = getCorrectedValue(maxValue)
        getTvMax()?.text =
                if (id == R.id.fragment_filter_seekbar_price) getPatternPrice(correctVal)
                else correctVal.toString()
    }

    /**
     * Return the min text view for this seek bar.
     * @return Return the min text view for this seek bar.
     */
    private fun getTvMin() = when (id) {
        R.id.fragment_filter_seekbar_price -> parentView?.fragment_filter_seekbar_price_text_min
        R.id.fragment_filter_seekbar_surface -> parentView?.fragment_filter_seekbar_surface_text_min
        R.id.fragment_filter_seekbar_rooms -> parentView?.fragment_filter_seekbar_rooms_text_min
        else -> null
    }

    /**
     * Return the max text view for this seek bar.
     * @return Return the max text view for this seek bar.
     */
    private fun getTvMax() = when (id) {
        R.id.fragment_filter_seekbar_price -> parentView?.fragment_filter_seekbar_price_text_max
        R.id.fragment_filter_seekbar_surface -> parentView?.fragment_filter_seekbar_surface_text_max
        R.id.fragment_filter_seekbar_rooms -> parentView?.fragment_filter_seekbar_rooms_text_max
        else -> null
    }

    /**
     * Return the corrected value for this seek bar.
     * @return Return the corrected value for this seek bar.
     */
    private fun getCorrectedValue(value: Int): Int = when (id) {
        R.id.fragment_filter_seekbar_price -> value * 100000
        R.id.fragment_filter_seekbar_surface -> value * 5
        R.id.fragment_filter_seekbar_rooms -> value / 2
        else -> 0
    }

    /**
     * Get pattern price string.
     * @param value the integer value to convert.
     * @return the converted string pattern price.
     */
    private fun getPatternPrice(value: Int): String =
            if (value == 0) "$ 0"
            else convertPriceToPatternPrice(value.toString(), true)


    @Deprecated(message = "Use setOnRangeChangeListener instead", replaceWith = ReplaceWith(
            "setOnRangeChangeListener(listener)",
            "org.desperu.realestatemanager.view.CustomSeekBar.setOnRangeChangeListener"
    ))
    override fun setOnRangeSeekbarChangeListener(onRangeSeekbarChangeListener: OnRangeSeekbarChangeListener?) {
        throw IllegalArgumentException("Use setOnRangeChangeListener instead")
    }
}