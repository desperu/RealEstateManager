package org.desperu.realestatemanager.ui.manageEstate

import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_manage_estate.*
import org.desperu.realestatemanager.R
import org.desperu.realestatemanager.base.BaseActivity
import org.desperu.realestatemanager.utils.estateTypeList

class ManageEstateActivity: BaseActivity(), AdapterView.OnItemSelectedListener {

    // FOR DATA
    private lateinit var estateType: String

    // --------------
    // BASE METHODS
    // --------------

    override fun getActivityLayout(): Int = R.layout.activity_manage_estate

    override fun configureDesign() {
        configureToolBar()
        configureUpButton()
        configureTypeSpinner()
    }

    // -----------------
    // CONFIGURATION
    // -----------------

    private fun configureTypeSpinner() {
        activity_manage_estate_spinner_type.onItemSelectedListener = this
//        activity_manage_estate_spinner_type.startDragAndDrop()
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, estateTypeList)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        activity_manage_estate_spinner_type.adapter = spinnerAdapter
    }

    // -----------------
    // METHODS OVERRIDE
    // -----------------

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        estateType = parent?.getItemAtPosition(position).toString()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }
}