package org.desperu.realestatemanager.ui.manageEstate

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.annotation.ArrayRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.LinearLayoutManager
import icepick.State
import kotlinx.android.synthetic.main.fragment_estate_address.*
import kotlinx.android.synthetic.main.fragment_estate_data.*
import kotlinx.android.synthetic.main.fragment_estate_image.*
import kotlinx.android.synthetic.main.fragment_estate_sale.*
import org.desperu.realestatemanager.R
import org.desperu.realestatemanager.base.BaseBindingFragment
import org.desperu.realestatemanager.databinding.FragmentEstateImageBinding
import org.desperu.realestatemanager.utils.*
import org.desperu.realestatemanager.utils.Utils.intDateToString
import org.desperu.realestatemanager.utils.Utils.todayDate
import java.util.*

class ManageEstateFragment(): BaseBindingFragment() {

    // FOR DATA
    @JvmField @State var fragmentKey: Int = -1 // TODO save value but can't with Icepick, do it manually with bundle??
    private lateinit var binding: ViewDataBinding
    private lateinit var viewModel: ManageEstateViewModel
    // DATE PICKER
    private lateinit var datePickerSale: OnDateSetListener
    private lateinit var datePickerSold: OnDateSetListener
    private var saleDate = String()
    private var soldDate = String()

    // SECOND CONSTRUCTOR
    constructor(fragmentKey: Int): this() {
        this.fragmentKey = fragmentKey
    }

    // --------------
    // BASE METHODS
    // --------------

    override fun getBindingView(): View = configureViewModel()

    override fun configureDesign() {}

    override fun updateDesign() {
        configureCorrespondingLayout()
    }

    // -----------------
    // CONFIGURATION
    // -----------------

    /**
     * Configure data binding with view model.
     */
    private fun configureViewModel(): View {
        binding = DataBindingUtil.inflate(inflater, getFragmentLayout(), container, false)
        configureImageRecycler()

        viewModel = (requireActivity() as ManageEstateActivity).getViewModel()
        if (fragmentKey != ESTATE_IMAGE) binding.setVariable(org.desperu.realestatemanager.BR.viewModel, viewModel)
        updateRecyclerImageList() // TODO to perfect
        return binding.root
    }

    /**
     * Configure corresponding layout, depending of fragmentKey.
     */
    private fun configureCorrespondingLayout() {
        when (fragmentKey) {
            ESTATE_DATA -> configureSpinner(fragment_estate_data_spinner_type, R.array.estate_type_list)
            ESTATE_ADDRESS -> configureSpinner(fragment_estate_address_spinner_interest_places, R.array.estate_interest_places_list)
            ESTATE_SALE -> { configureSpinner(fragment_estate_sale_spinner_state, R.array.estate_state_list)
                    configureDatePicker()}
        }
    }

    /**
     * Configure estate spinner with adapter.
     * @param spinner Spinner to configure.
     * @param id String array id for spinner.
     */
    private fun configureSpinner(spinner: Spinner, @ArrayRes id: Int) {
//        fragment_estate_data_spinner_type.onItemSelectedListener = this
//        activity_manage_estate_spinner_type.startDragAndDrop() // TODO customize list ??
        val spinnerAdapter = ArrayAdapter(context!!, android.R.layout.simple_spinner_item, resources.getStringArray(id))
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = spinnerAdapter
    }

    /**
     * Configure Linear Layout Manager for Image Recycler.
     */
    private fun configureImageRecycler() {
        if (fragmentKey == ESTATE_IMAGE && fragment_estate_image_recycler_view != null)
            (binding as FragmentEstateImageBinding).fragmentEstateImageRecyclerView.layoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    }

    // --------------
    // DATE PICKERS
    // --------------

    /**
     * Configure date picker.
     */
    private fun configureDatePicker() {
        fragment_estate_sale_date_picker_sale_date.text = todayDate() // TODO to check
        fragment_estate_sale_date_picker_sale_date.setOnClickListener { configureDatePickerDialog(datePickerSale) }
        datePickerSale = OnDateSetListener { _, year, month, dayOfMonth ->
            saleDate = intDateToString(dayOfMonth, month, year)
            fragment_estate_sale_date_picker_sale_date.text = saleDate
        }
        fragment_estate_sale_date_picker_sold_out_date.setOnClickListener { configureDatePickerDialog(datePickerSold) }
        datePickerSold = OnDateSetListener { _, year, month, dayOfMonth ->
            soldDate = intDateToString(dayOfMonth, month, year)
            fragment_estate_sale_date_picker_sold_out_date.text = soldDate
        }
    }

    /**
     * Configure Date picker dialog.
     * @param dateSetListener Date picker dialog listener.
     */
    private fun configureDatePickerDialog(dateSetListener: OnDateSetListener) {
        val cal: Calendar = Calendar.getInstance()
        if (dateSetListener === datePickerSale && saleDate.isNotEmpty()) cal.time = Utils.stringToDate(saleDate)
        if (dateSetListener === datePickerSold && soldDate.isNotEmpty()) cal.time = Utils.stringToDate(soldDate)
        val year: Int = cal.get(Calendar.YEAR)
        val monthOfYear: Int = cal.get(Calendar.MONTH)
        val dayOfMonth: Int = cal.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(context!!, R.style.DatePickerDialogTheme,
                dateSetListener, year, monthOfYear, dayOfMonth)
        datePickerDialog.show()
    }

    // -----------------
    // UI
    // -----------------

    /**
     * Update Recycler Image List.
     */
    private fun updateRecyclerImageList() {
        if (fragmentKey == ESTATE_IMAGE) viewModel.updateRecyclerImageList()
    }

    // -----------------
    // UTILS
    // -----------------

    /**
     * Get corresponding fragment layout, depending of fragmentKey value.
     */
    private fun getFragmentLayout() = when (fragmentKey) {
        ESTATE_DATA -> R.layout.fragment_estate_data
        ESTATE_IMAGE -> R.layout.fragment_estate_image
        ESTATE_ADDRESS -> R.layout.fragment_estate_address
        ESTATE_SALE -> R.layout.fragment_estate_sale
        else -> R.layout.fragment_estate_data
    }
}