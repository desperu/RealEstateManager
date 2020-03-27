package org.desperu.realestatemanager.ui.manageEstate

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import org.desperu.realestatemanager.R
import org.desperu.realestatemanager.base.BaseViewModel
import org.desperu.realestatemanager.model.Address
import org.desperu.realestatemanager.model.Estate
import org.desperu.realestatemanager.model.Image
import org.desperu.realestatemanager.repositories.AddressDataRepository
import org.desperu.realestatemanager.repositories.EstateDataRepository
import org.desperu.realestatemanager.repositories.ImageDataRepository
import org.desperu.realestatemanager.ui.ImageViewModel
import org.desperu.realestatemanager.utils.Utils.convertPatternPriceToString
import org.desperu.realestatemanager.view.RecyclerViewAdapter
import java.util.concurrent.*

class ManageEstateViewModel(private val estateDataRepository: EstateDataRepository,
                            private val imageDataRepository: ImageDataRepository,
                            private val addressDataRepository: AddressDataRepository,
                            private val executor: Executor): BaseViewModel() {

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
     * Set estate data with images and address, with estateId from database.
     */
    fun setEstate(estateId: Long) {
        if (estateId != 0.toLong()) {
            estate.value = estateDataRepository.getEstate(estateId).value
            estate.value?.imageList = imageDataRepository.getImageList(estateId).value!! as ArrayList<Image>
            estate.value?.address = addressDataRepository.getAddress(estateId).value!!
        } else {
            estate.value = Estate()
            estate.value?.imageList = ArrayList<Image>()
            estate.value?.address = Address()
        }
    }

    // -------------
    // UPDATE DATA
    // -------------

    /**
     * Update Recycler Image List.
     */
    fun updateRecyclerImageList() {
        val imageViewModelList = ArrayList<ImageViewModel>()
        estate.value?.imageList?.let { for (image in it) imageViewModelList.add(ImageViewModel(image)) }
        imageListAdapter.updateList(imageViewModelList)
    }

    /**
     * Add new image to image list.
     * @param imageUri Image uri.
     */
    fun addImageToImageList(imageUri: String) {
        estate.value?.imageList?.add(Image(imageUri = imageUri))
        estate.value?.imageList
        updateRecyclerImageList()
    }

    // -------------
    // LISTENERS
    // -------------

    /**
     * Spinner listener, for type, interest places and state spinners.
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

        if (estate.value?.id == 0L) {
            estate.value?.let { setEstateIdInOtherTables(createEstate(it)) }
            estate.value?.imageList?.let { createImageList(it) }
            estate.value?.address?.let { createAddress(it) }
        } else {
            estate.value?.let { updateEstate(it) }
            estate.value?.id?.let { setEstateIdInOtherTables(it) }
            estate.value?.imageList?.let { updateImageList(it) }
            estate.value?.address?.let { updateAddress(it) }
        }
    }

    private fun bindDataInEstate() {
        price.get()?.let {  estate.value?.price = convertPatternPriceToString(it).toLong() }

        // Spinners values
        estate.value?.type = type
        estate.value?.interestPlaces = interestPlaces
        estate.value?.state = state
    }

    private fun setEstateIdInOtherTables(estateId: Long) {
        // Other tables
        estate.value?.imageList?.let { for (image in it) image.estateId = estateId }
        estate.value?.address?.estateId = estateId
    }

    // ESTATE
    private fun createEstate(estate: Estate): Long {
        val callableTask: () -> Long = { estateDataRepository.createEstate(estate) }
        val executorService: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
        val rowCount: Future<Long>
        rowCount = executorService.schedule(callableTask, 1, TimeUnit.SECONDS)
        return rowCount.get()
    }

    private fun updateEstate(estate: Estate) { executor.execute { estateDataRepository.updateEstate(estate) } }

    private fun deleteEstate() { executor.execute { estateDataRepository.deleteEstate(estate.value!!.id) } }

    // IMAGE
    private fun createImage(image: Image) { executor.execute { imageDataRepository.createImage(image) } }

    private fun createImageList(imageList: List<Image>) { executor.execute { imageDataRepository.createImageList(imageList) } }

    private fun updateImage(image: Image) { executor.execute { imageDataRepository.updateImage(image) } }

    private fun updateImageList(imageList: List<Image>) { executor.execute { imageDataRepository.updateImageList(imageList) } }

    private fun deleteImage(imageId: Long) { executor.execute { imageDataRepository.deleteImage(imageId) } }

    // ADDRESS
    private fun createAddress(address: Address) { executor.execute { addressDataRepository.createAddress(address) } }

    private fun updateAddress(address: Address) { executor.execute { addressDataRepository.updateAddress(address) } }

    private fun deleteAddress() { executor.execute { addressDataRepository.deleteAddress(estate.value!!.id) } }

    // --- GETTERS ---

    val getImageListAdapter = imageListAdapter
}