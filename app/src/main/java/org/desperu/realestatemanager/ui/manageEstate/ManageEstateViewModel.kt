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
import org.desperu.realestatemanager.ui.ImageViewModel
import org.desperu.realestatemanager.utils.*
import org.desperu.realestatemanager.view.RecyclerViewAdapter
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

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
//    private val imageList = MutableLiveData<ArrayList<Image>>()
    private var imageList = ArrayList<Image>()
//    var price = ObservableField<String>()
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
            imageList = imageDataRepository.getImages(estateId).value as ArrayList<Image>
//            estate.value!!.images = imageDataRepository.getImages(estateId).value!!
//            estate.value!!.address = addressDataRepository.getAddress(estateId).value!!
        } //else
//            estate.value = Estate()
    }

    // -------------
    // UPDATE DATA
    // -------------

    /**
     * Update Recycler Image List.
     */
    fun updateRecyclerImageList() {
        val imageViewModelList = ArrayList<ImageViewModel>()
//        if (imageList != null) {
            for (image in imageList)
                imageViewModelList.add(ImageViewModel(image))
//        }
        imageViewModelList.add(ImageViewModel(Image(0,0,"content://com.google.android.apps.photos.contentprovider/-1/1/content%3A%2F%2Fmedia%2Fexternal%2Fimages%2Fmedia%2F123/ORIGINAL/NONE/2100203926",false,"toto")))
        imageListAdapter.updateList(imageViewModelList)
//        imageList.value?.let { imageListAdapter.updateList(it) }
//        mutableRefreshing.value = false
    }

    /**
     * Add new image to image list.
     * @param imageUri Image uri.
     */
    fun addImageToImageList(imageUri: String) {
        imageList.add(Image(0, 0, imageUri, false, "")) // TODO not good
        updateRecyclerImageList()
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

        var isEditing = false
        override fun afterTextChanged(s: Editable?) {
            when (id) {
                PRICE_ID -> {
                    price = s.toString().toLong()
//                    if (s.isNullOrBlank()) { price.set(""); return }
//                    if (isEditing) return
//                    isEditing = true
//
//                    val s1: Double = s.replace(Regex.fromLiteral(","), "")
//                            .toString().toDouble()
//
//                    val nf2: NumberFormat = NumberFormat.getInstance(Locale.ENGLISH)
//                    (nf2 as DecimalFormat).applyPattern("###,###,###")
//                    price.set(nf2.format(s1))
//
//                    isEditing = false
                }
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