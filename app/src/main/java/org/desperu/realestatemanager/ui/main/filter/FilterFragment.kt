package org.desperu.realestatemanager.ui.main.filter

import android.content.Context
import android.view.MotionEvent
import android.view.View
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.fragment_filter.*
import org.desperu.realestatemanager.R
import org.desperu.realestatemanager.base.BaseBindingFragment
import org.desperu.realestatemanager.databinding.FragmentFilterBinding
import org.desperu.realestatemanager.di.ViewModelFactory
import org.desperu.realestatemanager.extension.createDatePickerDialog
import org.desperu.realestatemanager.view.adapter.NothingSelectedSpinnerAdapter


/**
 * Fragment to filter estates from list.
 *
 * @constructor Instantiates a new FilterFragment.
 */
class FilterFragment: BaseBindingFragment() {

    // FOR DATA
    private lateinit var binding: FragmentFilterBinding
    private var viewModel: FilterViewModel? = null

    // --------------
    // BASE METHODS
    // --------------

    override fun getBindingView(): View = configureViewModel()

    override fun configureDesign() {}

    override fun updateDesign() {
        configureDatePicker()
        configureSpinner()
        configureInterceptTouchEvent()
    }

    // -----------------
    // CONFIGURATION
    // -----------------

    /**
     * Configure data binding with view model.
     */
    private fun configureViewModel(): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_filter, container, false)
        viewModel = ViewModelProvider(this, ViewModelFactory(activity as AppCompatActivity)).get(FilterViewModel::class.java)

        binding.viewModel = viewModel
        return binding.root
    }

    /**
     * Configure begin and end date picker.
     */
    private fun configureDatePicker() {
        setPickerTextOnClickListener(context!!, fragment_filter_sale_date_begin, viewModel?.saleBegin)
        setPickerTextOnClickListener(context!!, fragment_filter_sale_date_end, viewModel?.saleEnd)
        setPickerTextOnClickListener(context!!, fragment_filter_sold_date_begin, viewModel?.soldBegin)
        setPickerTextOnClickListener(context!!, fragment_filter_sold_date_end, viewModel?.soldEnd)
    }

    /**
     * Set picker text view on click listener to show date picker dialog, and set sale date at today.
     * @param context the context from this function is called.
     * @param pickerView the associated picker text view.
     * @param date the given string date, to set DatePickerDialog.
     */
    private fun setPickerTextOnClickListener(context: Context, pickerView: TextView, date: ObservableField<String>?) {
        pickerView.setOnClickListener { createDatePickerDialog(context, pickerView, date) }
    }

    /**
     * Configure state spinner with adapter.
     */
    private fun configureSpinner() {
        val spinnerAdapter = ArrayAdapter(context!!, R.layout.spinner_item, resources.getStringArray(R.array.estate_state_list))
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        fragment_filter_spinner_state.adapter = NothingSelectedSpinnerAdapter(
                spinnerAdapter,
                R.layout.spinner_row_nothing_selected,
                context!!)
    }

    /**
     * Configure on touch listener on scroll root, to intercept touch event.
     * It's needed to properly dispatch touch event between bottom sheet and scroll view.
     */
    private fun configureInterceptTouchEvent() = fragment_filter_scroll.setOnTouchListener { v, event ->
        val isOnTop = fragment_filter_scroll.scrollY == 0
        when (event.action) {

            MotionEvent.ACTION_DOWN -> {
                v.parent.requestDisallowInterceptTouchEvent(!isOnTop)
                return@setOnTouchListener !isOnTop
            }

            MotionEvent.ACTION_UP -> {
                v.parent.requestDisallowInterceptTouchEvent(false)
                return@setOnTouchListener false
            }

            else -> return@setOnTouchListener false

        }
    }

    // -----------------
    // UI
    // -----------------

    /**
     * Set the bottom bar background color, red if has filter, original color otherwise.
     * @param hasFilters true if has one filter or more set, false otherwise.
     */
    @Suppress("deprecation")
    internal fun setBottomBarColor(hasFilters: Boolean) {
        fragment_filter_container_bottom_bar.setBackgroundColor(resources.getColor(
                if (hasFilters) R.color.bottomBarRed
                else R.color.colorPrimaryDark
        ))
    }

    /**
     * Scroll the scroll view to the top.
     */
    internal fun scrollToTop() {
        if (fragment_filter_scroll != null)
            fragment_filter_scroll.scrollY = 0
    }
}