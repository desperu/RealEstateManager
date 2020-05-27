package org.desperu.realestatemanager.ui.manageEstate.fragment

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
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
import org.desperu.realestatemanager.service.ResourceService
import org.desperu.realestatemanager.utils.Utils.concatenateStringFromMutableList
import org.desperu.realestatemanager.utils.Utils.convertPatternPriceToString
import org.desperu.realestatemanager.utils.Utils.deConcatenateStringToMutableList
import org.desperu.realestatemanager.view.RecyclerViewAdapter
import java.lang.ref.WeakReference

/**
 * View Model to manage full estate (estate, images and address) in database.
 *
 * @param estateRepository the estate repository interface witch provide database access.
 * @param imageRepository the image repository interface witch provide database access.
 * @param addressRepository the address repository interface witch provide database access.
 * @param communication the manage estate view model communication interface witch provide view model communication.
 * @param resourceService the resource service interface witch provide application resources access.
 *
 * @constructor Instantiates a new ManageEstateViewModel.
 *
 * @property estateRepository the estate repository interface witch provide database access to set.
 * @property imageRepository the image repository interface witch provide database access to set.
 * @property addressRepository the address repository interface witch provide database access to set.
 * @property communication the manage estate view model communication interface witch provide view model communication to set.
 * @property resourceService the resource service interface witch provide application resources access to set.
 */
class ManageEstateViewModel(private val estateRepository: EstateRepository,
                            private val imageRepository: ImageRepository,
                            private val addressRepository: AddressRepository,
                            private val communication: ManageEstateVMCommunication,
                            private val resourceService: ResourceService): ViewModel() {

    // FOR DATA
    private val imageListAdapter = WeakReference(RecyclerViewAdapter(R.layout.item_manage_image))
    private lateinit var manageImageVMList: MutableList<ManageImageViewModel>
    private var oldImageList = listOf<Image>()
    val estate = MutableLiveData<Estate>()
    var price = ObservableField<String>()
    private var interestPlaces = MutableLiveData<MutableList<String>>()
    val saleDate = ObservableField<String>()
    val soldDate = ObservableField<String>()

    // -------------
    // SET ESTATE
    // -------------

    /**
     * Set estate data with images and address.
     * @param givenEstate the given estate to manage.
     */
    internal fun setEstate(givenEstate: Estate?) {
        if (givenEstate != null) {
            estate.value = givenEstate
            estate.value?.imageList?.let { oldImageList = it } // Save db image list
        }
        else this.estate.value = Estate()
        setSpecificValues()
    }

    /**
     * Set specific values, for price, dates and interest places.
     */
    private fun setSpecificValues() {
        price.set(estate.value?.price.toString())
        saleDate.set(estate.value?.saleDate)
        soldDate.set(estate.value?.soldDate)
        interestPlaces.value = estate.value?.interestPlaces?.let { deConcatenateStringToMutableList(it) }
//        if (estate.value?.id == 0L) estate.value?.createdTime = System.currentTimeMillis() TODO to implement and sort list with
    }

    // -------------
    // UPDATE DATA
    // -------------

    /**
     * Update Recycler Adapter Manage Image View Model List.
     * Set Manage Image View Model List only if not already do.
     */
    @Suppress("unchecked_cast")
    internal fun updateRecyclerImageList() {
        if (!::manageImageVMList.isInitialized)
            manageImageVMList = oldImageList.map { image -> ManageImageViewModel(image, this) }.toMutableList()
        imageListAdapter.get()?.updateList(manageImageVMList as MutableList<Any>)
        imageListAdapter.get()?.notifyDataSetChanged()
    }

    /**
     * Add new image to manage image view model list.
     * @param imageUri the image uri to set.
     */
    internal fun addImageToImageList(imageUri: String) {
        val image = Image(imageUri = imageUri)
        manageImageVMList.add(ManageImageViewModel(image, this))
        val position = manageImageVMList.size.minus(1)
        imageListAdapter.get()?.notifyItemInserted(position)
        communication.scrollToNewItem(position)
    }

    // -------------
    // LISTENERS
    // -------------

    /**
     * Spinner listener, for type, interest places and state spinners.
     */
    private val spinnerListener = object: AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {}

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            if (position > 0) {
                val value = parent?.getItemAtPosition(position).toString()
                when (parent?.tag) {
                    "spinnerType" -> estate.value?.type = value
                    "spinnerInterestPlaces1" -> setInterestPlace(0, value)
                    "spinnerInterestPlaces2" -> setInterestPlace(1, value)
                    "spinnerInterestPlaces3" -> setInterestPlace(2, value)
                    "spinnerState" -> estate.value?.state = value
                }
            }
        }
    }

    /**
     * Set interest places value, add new if needed.
     * @param index the index to set the value.
     * @param value the value to set.
     */
    private fun setInterestPlace(index: Int, value: String) {
        val size = interestPlaces.value?.size
        if (size != null && size > index) interestPlaces.value?.set(index, value)
        else interestPlaces.value?.add(index, value)
    }

    /**
     * Edit Text Listener for price edit text.
     */
    private val editTextListener = object: TextWatcher {
        override fun afterTextChanged(s: Editable?) { price.set(s.toString()) }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }

    // -------------
    // MANAGE ESTATE
    // -------------

    /**
     * Create or update estate in database, depend if already exist.
     */
    internal fun createOrUpdateEstate() {
        bindDataInEstate()

        if (estate.value?.id == 0L) estate.value?.let { createEstate(it) }
        else estate.value?.let { updateEstate(it) }
    }

    /**
     * Bind data in estate object, set price, sale date and sold date from ui to estate object.
     * For interest places, concatenate list to simple string to save in database.
     * Set updated image list in estate object to properly update ui after estate management.
     */
    internal fun bindDataInEstate() {
        price.get()?.let {  estate.value?.price = convertPatternPriceToString(it).toLong() }
        estate.value?.interestPlaces = interestPlaces.value?.let { concatenateStringFromMutableList(it) }.toString()
        estate.value?.saleDate = saleDate.get().toString()
        estate.value?.soldDate = soldDate.get().toString()
        estate.value?.imageList = manageImageVMList.map { it.image.value!! } as MutableList<Image>
    }

    /**
     * Create new estate with image and address.
     * @param estate the estate to create in database.
     */
    private fun createEstate(estate: Estate) = viewModelScope.launch(Dispatchers.Default) {
        val estateId = estateRepository.createEstate(estate)
        setEstateIdInOtherTables(estateId)
        manageImageVMList.map { it.image.value!! }.let { imageRepository.createImage(*it.toTypedArray()) }
        addressRepository.createAddress(estate.address)
    }

    /**
     * Update estate with it's images and address.
     * @param estate the estate to update in database.
     */
    private fun updateEstate(estate: Estate) = viewModelScope.launch(Dispatchers.Default) {
        estateRepository.updateEstate(estate)
        setEstateIdInOtherTables(estate.id)
        // Sort image view model list, images to update/images to create
        val imageListPair = manageImageVMList.partition { oldImageList.contains(it.image.value) }
        imageRepository.updateImage(*imageListPair.first.map { it.image.value!! }.toTypedArray())
        imageRepository.createImage(*imageListPair.second.map { it.image.value!! }.toTypedArray())
        addressRepository.updateAddress(estate.address)
    }

    /**
     * Set the estate id in it's address and image objects for database.
     * @param estateId the id of this estate.
     */
    private fun setEstateIdInOtherTables(estateId: Long) {
        // Other tables
        manageImageVMList.forEach { it.image.value?.estateId = estateId }
        estate.value?.address?.estateId = estateId
    }

    // -------------
    // MANAGE IMAGE
    // -------------

    /**
     * Manage primary image in image list, only one can be primary in the list,
     * so if another is primary, set it to not primary.
     * If the given image is set to primary, update the recycler adapter list
     * to move the given image at first position and scroll recycler to it.
     * @param manageImageVM the manage image view model witch user change image primary state.
     */
    internal fun managePrimaryImage(manageImageVM: ManageImageViewModel) {
        // Manage primary image in list
        if (manageImageVM.image.value?.isPrimary!!) {
            manageImageVMList.forEach {
                if (it != manageImageVM && it.image.value?.isPrimary!!) {
                    it.image.value?.isPrimary = false
                    it.setPrimaryVisibility()
                }
            }
            // Update list and ui
            val position = manageImageVMList.indexOf(manageImageVM)
            manageImageVMList.removeAt(position)
            manageImageVMList.add(0, manageImageVM)
            communication.scrollToNewItem(0)
            imageListAdapter.get()?.notifyItemMoved(position, 0)
        }
    }

    /**
     * Remove the given image in manage image view model list, storage and database.
     * @param manageImageVM the view model of the image to remove.
     */
    internal fun removeImage(manageImageVM: ManageImageViewModel) {
        val position = manageImageVMList.indexOf(manageImageVM)
        manageImageVMList.removeAt(position)
        imageListAdapter.get()?.notifyItemRemoved(position)
        manageImageVM.image.value?.let {
            deleteImage(it.id) // database
            communication.deleteImageInStorage(it.imageUri) // external storage
        }
    }

    private fun deleteImage(imageId: Long) = viewModelScope.launch(Dispatchers.Main) { imageRepository.deleteImage(imageId) }

    // --- GETTERS ---

    val getImageListAdapter = imageListAdapter

    val getInterestPlaces: LiveData<MutableList<String>> = interestPlaces

    val getSpinnerListener = spinnerListener

    val getEditTextListener = editTextListener
}