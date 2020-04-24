package org.desperu.realestatemanager.ui.manageEstate

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.desperu.realestatemanager.R
import org.desperu.realestatemanager.model.Estate
import org.desperu.realestatemanager.model.Image
import org.desperu.realestatemanager.repositories.AddressRepository
import org.desperu.realestatemanager.repositories.EstateRepository
import org.desperu.realestatemanager.repositories.ImageRepository
import org.desperu.realestatemanager.ui.ImageViewModel
import org.desperu.realestatemanager.utils.Utils.convertPatternPriceToString
import org.desperu.realestatemanager.view.RecyclerViewAdapter

/**
 * View Model to manage full estate (estate, images and address) in database.
 */
class ManageEstateViewModel(private val estateRepository: EstateRepository,
                            private val imageRepository: ImageRepository,
                            private val addressRepository: AddressRepository): ViewModel() {

    // FOR DATA
    private val imageListAdapter = RecyclerViewAdapter(R.layout.item_image)
    val estate = MutableLiveData<Estate>()
    var price = ObservableField<String>()

    // For Spinners
    private var type = ""
    private var interestPlaces = ""
    private var state = ""

    // -------------
    // SET ESTATE
    // -------------

    /**
     * Set estate data with images and address.
     */
    fun setEstate(estate: Estate?) {
        if (estate != null) this.estate.value = estate
        else this.estate.value = Estate()
    }

    // -------------
    // UPDATE DATA
    // -------------

    /**
     * Update Recycler Image List.
     */
    fun updateRecyclerImageList() {
        val imageViewModelList = estate.value?.imageList?.map { image -> ImageViewModel(image) } ?: emptyList()
        imageListAdapter.updateList(imageViewModelList.toMutableList())
    }

    /**
     * Add new image to image list.
     * @param imageUri Image uri.
     */
    fun addImageToImageList(imageUri: String) {
        estate.value?.imageList?.add(Image(imageUri = imageUri))
        updateRecyclerImageList()
    }

    // -------------
    // LISTENERS
    // -------------

    /**
     * Spinner listener, for type, interest places and state spinners.
     */
    val spinnerListener = object: AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {
            setSpinnerValue(parent, 0)
        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            setSpinnerValue(parent, position)
        }
    }

    // TODO can be perfect, use Observable in layout
    private fun setSpinnerValue(parent: AdapterView<*>?, position: Int) {
        val value = parent?.getItemAtPosition(position).toString()
        when (parent?.tag) {
            "spinnerType" -> type = value
            "spinnerInterestPlaces" -> interestPlaces = value
            "spinnerState" -> state = value
        }
    }

    /**
     * Edit Text Listener for price edit text.
     */
    val editTextListener = object: TextWatcher {
        override fun afterTextChanged(s: Editable?) { price.set(s.toString()) }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }

    // -------------
    // MANAGE
    // -------------

    /**
     * Create or update estate in database, depending if exist.
     */
    fun createOrUpdateEstate() {

        bindDataInEstate()

        if (estate.value?.id == 0L)
            estate.value?.let { createEstate(it) }
        else
            estate.value?.let { updateEstate(it) }
    }

    /**
     * Bind data in estate object.
     */
    private fun bindDataInEstate() {
        price.get()?.let {  estate.value?.price = convertPatternPriceToString(it).toLong() }

        // Spinners values
        estate.value?.type = type
        estate.value?.interestPlaces = interestPlaces
        estate.value?.state = state
    }

    /**
     * Create new estate with image and address.
     * @param estate the estate to create in database.
     */
    private fun createEstate(estate: Estate) = viewModelScope.launch(Dispatchers.Main) {
        val estateId = estateRepository.createEstate(estate)
        setEstateIdInOtherTables(estateId)
        imageRepository.createImage(*estate.imageList.toTypedArray())
        addressRepository.createAddress(estate.address)
    }

    /**
     * Update estate with it's images and address.
     * @param estate the estate to update in database.
     */
    private fun updateEstate(estate: Estate) = viewModelScope.launch(Dispatchers.Main) {
        estateRepository.updateEstate(estate)
        setEstateIdInOtherTables(estate.id)
        imageRepository.updateImage(*estate.imageList.toTypedArray())
        addressRepository.updateAddress(estate.address)
    }

    /**
     * Set the estate id in it's address and image objects for database.
     * @param estateId the id of this estate.
     */
    private fun setEstateIdInOtherTables(estateId: Long) {
        // Other tables
        estate.value?.imageList?.forEach { image -> image.estateId = estateId }
        estate.value?.address?.estateId = estateId
    }

    // IMAGE

    fun deleteImage(imageId: Long) { viewModelScope.launch(Dispatchers.Main) { imageRepository.deleteImage(imageId) } }

    // --- GETTERS ---

    val getImageListAdapter = imageListAdapter

    val getSpinnerListener = spinnerListener

    val getEditTextListener = editTextListener
}