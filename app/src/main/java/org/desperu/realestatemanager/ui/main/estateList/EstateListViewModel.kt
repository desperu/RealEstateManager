package org.desperu.realestatemanager.ui.main.estateList

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.desperu.realestatemanager.R
import org.desperu.realestatemanager.model.Address
import org.desperu.realestatemanager.model.Estate
import org.desperu.realestatemanager.repositories.AddressRepository
import org.desperu.realestatemanager.repositories.EstateRepository
import org.desperu.realestatemanager.repositories.ImageRepository
import org.desperu.realestatemanager.service.GeocoderService
import org.desperu.realestatemanager.ui.main.MainCommunication
import org.desperu.realestatemanager.view.adapter.RecyclerViewAdapter
import java.lang.ref.WeakReference

/**
 * View Model witch provide data for estate list.
 *
 * @param estateRepository the estate repository interface witch provide database access.
 * @param imageRepository the image repository interface witch provide database access.
 * @param addressRepository the address repository interface witch provide database access.
 * @param communication the main communication interface that allow communication with activity.
 * @param router the estate router interface witch provide user redirection.
 * @param geocoder the geocoder service interface witch provide geocoder service access.
 *
 * @constructor Instantiates a new EstateListViewModel.
 *
 * @property estateRepository the estate repository interface witch provide database access to set.
 * @property imageRepository the image repository interface witch provide database access to set.
 * @property addressRepository the address repository interface witch provide database access to set.
 * @property communication the main communication interface that allow communication with activity to set.
 * @property router the estate router interface witch provide user redirection to set.
 * @property geocoder the geocoder service interface witch provide geocoder service access to set.
 */
class EstateListViewModel(private val estateRepository: EstateRepository,
                          private val imageRepository: ImageRepository,
                          private val addressRepository: AddressRepository,
                          private val communication: MainCommunication,
                          private val router: EstateRouter,
                          private val geocoder: GeocoderService
): ViewModel() {

    // FOR DATA
    private var estateListAdapter = WeakReference(RecyclerViewAdapter(
            if (communication.isFrame2Visible) R.layout.item_estate
            else R.layout.item_estate_large))
    private val estateVMList = mutableListOf<EstateViewModel>()
    private val refreshing = ObservableBoolean(false)
    private val showEmptyText = ObservableBoolean(false)
    // For tablet (two frame)
    private var selectedItem: Estate? = null
    private var estateNotification: Estate? = null

    init {
        loadEstateList()
    }

    // -------------
    // ESTATE LIST
    // -------------

    /**
     * Load estate list with images and address for each, from database.
     */
    private fun loadEstateList() = viewModelScope.launch(Dispatchers.Main) {
        val estateList = estateRepository.getAll().toMutableList()
        estateList.forEach { estate ->
            estate.imageList = imageRepository.getEstateImages(estate.id).toMutableList()
            estate.address = addressRepository.getAddress(estate.id)
        }
        onRetrieveEstateList(estateList, false)
        // Populate to main
        estateList.let { communication.populateEstateListToMain(it) }
    }

    /**
     * Reload estate list from database.
     */
    @Suppress("unchecked_cast")
    fun reloadEstateList() {
        refreshing.set(true)
        loadEstateList()
        estateVMList.map { it.getEstate.value }.let { communication.updateEstateList(it as List<Estate>, false) }
    }

    /**
     * Push data to recycler view when retrieve estate list.
     * @param estateList the list of estates retrieved from database.
     * @param isUpdate true if is call for an update, false for first launching data.
     */
    @Suppress("unchecked_cast")
    private fun onRetrieveEstateList(estateList: List<Estate>, isUpdate: Boolean) {
        // For Recycler adapter
        estateVMList.clear()
        estateVMList.addAll(estateList.map { estate -> EstateViewModel(estate, router, this) })
        estateListAdapter.get()?.updateList(estateVMList as MutableList<Any>)
        estateListAdapter.get()?.notifyDataSetChanged()
        // Set latitude and longitude for each estate address if not already do.
        estateList.forEach { setLatLngInAddress(it.address, false) }
        updateUi(null, isUpdate)
    }

    /**
     * Update estate list with filtered or unfiltered estate list.
     * @param estateList the new estate list to set.
     */
    internal fun updateEstateList(estateList: List<Estate>) = onRetrieveEstateList(estateList, true)

    // -------------
    // ADD ESTATE
    // -------------

    /**
     * Add new, or updated estate in the list and update user interface.
     * @param estate the new or updated estate.
     * @return the position affected in the recycler view.
     */
    fun addOrUpdateEstate(estate: Estate?): Int? {
        var position: Int? = null
        if (estate != null) {
            val oldEstateVM = estateVMList.find { it.getEstate.value?.id == estate.id }
            if (oldEstateVM == null) { // Add new element in first position
                position = 0
                estateVMList.add(position, EstateViewModel(estate, router, this))
                estateListAdapter.get()?.notifyItemInserted(position)
            } else { // Update existing element at his position
                position = estateVMList.indexOf(oldEstateVM)
                estateVMList[position] = EstateViewModel(estate, router, this)
                estateListAdapter.get()?.notifyItemChanged(position)
            }
            updateUi(estate, true)
            // Set latitude and longitude for the address.
            setLatLngInAddress(estate.address, true)
        }
        return position
    }

    // -------------
    // UPDATE UI
    // -------------

    /**
     * Update Ui when received database request response, stop refreshing animation
     * and show text empty if list is empty.
     * @param estate the updated estate to show.
     * @param isUpdate true if is call for an update, false for first launching data.
     */
    private fun updateUi(estate: Estate?, isUpdate: Boolean) {
        refreshing.set(false)
        showEmptyText.set(estateVMList.isEmpty())
        if (communication.isFrame2Visible) showDetailForTablet(estate, isUpdate)
    }

    /**
     * If the device is a tablet, show details of the given estate if not null,
     * else show first estate of the list.
     * @param estate the estate to show detail.
     * @param isUpdate true if is call for an update, false for first launching data.
     */
    private fun showDetailForTablet(estate: Estate?, isUpdate: Boolean) {
        if (estateVMList.isNotEmpty()) {
            val estateToShow = estateNotification ?: estate ?: estateVMList[0].getEstate.value
            estateToShow?.let { switchSelectedItem(it); router.openEstateDetailForTablet(it, isUpdate) }
        }
    }

    /**
     * Restore the estate detail in tablet mode (two frames).
     */
    internal fun restoreDetailForTablet() = showDetailForTablet(selectedItem, false)

    /**
     * Switch the selected item in the list only for tablet mode.
     * @param estate the selected estate item.
     */
    internal fun switchSelectedItem(estate: Estate) = viewModelScope.launch(Dispatchers.Default) {
        if (communication.isFrame2Visible) {
            estateVMList.find { it.getEstate.value?.id == selectedItem?.id }?.setIsSelected(false)
            estateVMList.find { it.getEstate.value?.id == estate.id }?.setIsSelected(true)
            selectedItem = estate
        }
    }

    // -------------
    // ADDRESS
    // -------------

    /**
     * Set latitude and longitude for the given address in database. Only if has address data,
     * city or country, and it's not already do or data were updated.
     * @param address the given address from witch retrieved latitude and longitude.
     * @param isUpdated true if data were updated, false otherwise
     */
    private fun setLatLngInAddress(address: Address, isUpdated: Boolean) = viewModelScope.launch(Dispatchers.Main) {
        val hasAddressData = address.city.isNotBlank() || address.country.isNotBlank()
        val isEmptyLatLng = address.latitude == 0.0 && address.longitude == 0.0
        if (hasAddressData && (isEmptyLatLng || isUpdated)) {
            val latLng = geocoder.getLatLngFromAddress(address)
            if (latLng.isNotEmpty()) {
                address.latitude = latLng[0]
                address.longitude = latLng[1]
                addressRepository.updateAddress(address)
            }
        }
    }

    // -------------
    // UTILS
    // -------------

    /**
     * Return the position of the given estate in the list.
     * @param estate the given estate to get it's position.
     * @return the position of the given estate in the list.
     */
    internal fun getItemPosition(estate: Estate): Int = runBlocking {
        estateVMList.indexOf(estateVMList.find { it.getEstate.value == estate })
    }

    // --- SETTERS ---

    internal fun setEstateNotification(estate: Estate) { estateNotification = estate }

    // --- GETTERS ---

    val getEstateListAdapter = estateListAdapter

    val getRefreshing = refreshing

    val getShowEmptyText = showEmptyText
}