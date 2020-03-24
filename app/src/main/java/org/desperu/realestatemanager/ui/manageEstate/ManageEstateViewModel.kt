package org.desperu.realestatemanager.ui.manageEstate

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import org.desperu.realestatemanager.R
import org.desperu.realestatemanager.base.BaseViewModel
import org.desperu.realestatemanager.model.Estate
import org.desperu.realestatemanager.model.Image
import org.desperu.realestatemanager.repositories.AddressDataRepository
import org.desperu.realestatemanager.repositories.EstateDataRepository
import org.desperu.realestatemanager.repositories.ImageDataRepository
import org.desperu.realestatemanager.utils.*
import org.desperu.realestatemanager.view.RecyclerViewAdapter

class ManageEstateViewModel(private val estateDataRepository: EstateDataRepository,
                            private val imageDataRepository: ImageDataRepository,
                            private val addressDataRepository: AddressDataRepository): BaseViewModel() {

    private var type = ""
    private var price: Long = 0
    private var surface = 0
    private var roomNumber = 0
    private var description = ""

    private var interestPlaces = ""

    private var state = ""

    // FOR DATA
    private val imageListAdapter = RecyclerViewAdapter(R.layout.item_image)
    val estate = MutableLiveData<Estate>()
    private val imageList = MutableLiveData<List<Image>>()
    val city = ObservableField<String>()
    val country = MutableLiveData<String>()

    // -------------
    // SET ESTATE
    // -------------

    /**
     * Set estate data with images and address, with estateId from database.
     */
    fun setEstate(estateId: Long) {
        if (estateId != 0.toLong()) {
            estate.value = estateDataRepository.getEstate(estateId).value
            imageList.value = imageDataRepository.getImages(estateId).value
//            estate.value!!.images = imageDataRepository.getImages(estateId).value!!
//            estate.value!!.address = addressDataRepository.getAddress(estateId).value!!
        } //else
//            estate.value = Estate()
    }

    /**
     * Update Recycler Image List.
     */
    fun updateRecyclerImageList() {
        imageList.value?.let { imageListAdapter.updateList(it) }
//        mutableRefreshing.value = false
    }

    // -------------
    // LISTENERS
    // -------------

    /**
     * Spinner listener.
     */
    val spinnerListener = object: AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {}

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            when (parent?.tag) {
                "spinnerType" -> type = parent.getItemAtPosition(position).toString()
                "spinnerInterestPlaces" -> interestPlaces = parent.getItemAtPosition(position).toString()
                "spinnerState" -> state = parent.getItemAtPosition(position).toString()
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
                PRICE_ID -> price = s.toString().toLong()
                SURFACE_ID -> surface = s.toString().toInt()
                ROOMS_ID -> roomNumber = s.toString().toInt()
                DESCRIPTION_ID -> description = s.toString()
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }

    // --- GETTERS ---

    val getImageListAdapter = imageListAdapter

    // --- MANAGE ---

    fun createEstate(estate: Estate) { estateDataRepository.createEstate(estate) }

    fun updateEstate(estate: Estate) { estateDataRepository.updateEstate(estate) }

    fun deleteEstate() { estateDataRepository.deleteEstate(estate.value!!.id) }
}