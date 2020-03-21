package org.desperu.realestatemanager.ui.manageEstate

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import androidx.lifecycle.MutableLiveData
import org.desperu.realestatemanager.base.BaseViewModel
import org.desperu.realestatemanager.model.Estate
import org.desperu.realestatemanager.repositories.AddressDataRepository
import org.desperu.realestatemanager.repositories.EstateDataRepository
import org.desperu.realestatemanager.repositories.ImageDataRepository
import org.desperu.realestatemanager.utils.*

class ManageEstateViewModel(private val estateDataRepository: EstateDataRepository,
                            private val imageDataRepository: ImageDataRepository,
                            private val addressDataRepository: AddressDataRepository): BaseViewModel() {

    private var type = ""
    private var price = 0
    private var surface = 0
    private var roomNumber = 0
    private var description = ""

    // FOR DATA
    val estate = MutableLiveData<Estate>()

    // -------------
    // SET ESTATE
    // -------------

    /**
     * Set estate with images and address, with estateId from database.
     */
    fun setEstate(estateId: Long) {
        estate.value = estateDataRepository.getEstate(estateId).value
//        estate.value!!.images = imageDataRepository.getImages(estateId).value!!
//        estate.value!!.address = addressDataRepository.getAddress(estateId).value!!
    }

    // --- MANAGE ---

    fun createEstate(estate: Estate) { estateDataRepository.createEstate(estate) }

    fun updateEstate(estate: Estate) { estateDataRepository.updateEstate(estate) }

    fun deleteEstate() { estateDataRepository.deleteEstate(estate.value!!.id) }

    // -------------
    // LISTENERS
    // -------------

    val typeListener = object: AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {}

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            when (parent?.tag) {
                "spinnerType" -> type = parent.getItemAtPosition(position).toString()
            }
        }
    }

    /**
     * Edit Text Listener.
     * @param id Identifier for each edit text.
     */
    fun editTextListener(id: Int) = object: TextWatcher {

        override fun afterTextChanged(s: Editable?) {
            when (id) {
                PRICE_ID -> price = s.toString().toInt()
                SURFACE_ID -> surface = s.toString().toInt()
                ROOMS_ID -> roomNumber = s.toString().toInt()
                DESCRIPTION_ID -> description = s.toString()
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }
}