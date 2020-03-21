package org.desperu.realestatemanager.ui.manageEstate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_estate_data.*
import org.desperu.realestatemanager.R
import org.desperu.realestatemanager.databinding.FragmentEstateDataBinding

class EstateDataFragment(private val viewModel: ManageEstateViewModel): Fragment() {

    // FOR DATA
    private lateinit var binding: FragmentEstateDataBinding

    // --------------
    // BASE METHODS
    // --------------

//    override fun getFragmentLayout(): Int = R.layout.fragment_estate_data
//
//    override fun configureDesign() {
//    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        configureViewModel(inflater, container)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        configureTypeSpinner()
    }

    // -----------------
    // CONFIGURATION
    // -----------------

    /**
     * Configure data binding with view model.
     */
    private fun configureViewModel(inflater: LayoutInflater, container: ViewGroup?) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_estate_data, container, false)
        binding.viewModel = viewModel
    }

    /**
     * Configure estate type spinner with adapter.
     */
    private fun configureTypeSpinner() {
//        fragment_estate_data_spinner_type.onItemSelectedListener = this
//        activity_manage_estate_spinner_type.startDragAndDrop() // TODO customize list ??
        val spinnerAdapter = ArrayAdapter(context!!, android.R.layout.simple_spinner_item, resources.getStringArray(R.array.estate_type_list))
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        fragment_estate_data_spinner_type.adapter = spinnerAdapter
    }

    // -----------------
    // METHODS OVERRIDE
    // -----------------
}