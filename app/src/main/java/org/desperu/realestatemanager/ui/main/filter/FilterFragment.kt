package org.desperu.realestatemanager.ui.main.filter

import android.content.Context
import android.view.View
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
import org.desperu.realestatemanager.model.Estate

/**
 * The argument name for bundle, to received estate list in this Fragment.
 */
const val FILTER_ESTATE_LIST: String = "filterEstateList"

/**
 * Fragment to filter estates from list.
 *
 * @constructor Instantiates a new FilterFragment.
 */
class FilterFragment: BaseBindingFragment() {

    // FROM BUNDLE
    private val estateList: List<Estate>? get() = arguments?.getParcelableArrayList(FILTER_ESTATE_LIST)

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
        estateList?.let { viewModel?.setOriginalList(it) }

        binding.viewModel = viewModel
        return binding.root
    }

    /**
     * Configure begin and end date picker.
     */
    private fun configureDatePicker() {
        setPickerTextOnClickListener(context!!, fragment_filter_begin_date, viewModel?.saleBeginObs)
        setPickerTextOnClickListener(context!!, fragment_filter_end_date, viewModel?.saleEndObs)
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
}