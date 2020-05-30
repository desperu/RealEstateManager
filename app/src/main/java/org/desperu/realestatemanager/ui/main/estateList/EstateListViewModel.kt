package org.desperu.realestatemanager.ui.main.estateList

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.desperu.realestatemanager.R
import org.desperu.realestatemanager.model.Address
import org.desperu.realestatemanager.model.Estate
import org.desperu.realestatemanager.repositories.AddressRepository
import org.desperu.realestatemanager.repositories.EstateRepository
import org.desperu.realestatemanager.repositories.ImageRepository
import org.desperu.realestatemanager.service.GeocoderService
import org.desperu.realestatemanager.view.RecyclerViewAdapter

/**
 * View Model witch provide data for estate list.
 *
 * @param estateRepository the estate repository interface witch provide database access.
 * @param imageRepository the image repository interface witch provide database access.
 * @param addressRepository the address repository interface witch provide database access.
 * @param router the estate router interface witch provide user redirection.
 * @param geocoder the geocoder service interface witch provide geocoder service access.
 *
 * @constructor Instantiates a new EstateListViewModel.
 *
 * @property estateRepository the estate repository interface witch provide database access to set.
 * @property imageRepository the image repository interface witch provide database access to set.
 * @property addressRepository the address repository interface witch provide database access to set.
 * @property router the estate router interface witch provide user redirection to set.
 * @property geocoder the geocoder service interface witch provide geocoder service access to set.
 */
class EstateListViewModel(private val estateRepository: EstateRepository,
                          private val imageRepository: ImageRepository,
                          private val addressRepository: AddressRepository,
                          private val router: EstateRouter,
                          private val geocoder: GeocoderService
): ViewModel() {

    // FOR DATA
    private val estateListAdapter = MutableLiveData(RecyclerViewAdapter(R.layout.item_estate_large))
    private val estateVMList = mutableListOf<EstateViewModel>()
    private val refreshing = ObservableBoolean(false)
    private val showEmptyText = ObservableBoolean(false)

    init {
        loadEstateList()
    }

    // -------------
    // LOAD ESTATE LIST
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
        onRetrieveEstateList(estateList)
        // Populate to main
        router.populateEstateListToMain(estateList)
    }

    /**
     * Reload estate list from database.
     */
    fun reloadEstateList() { refreshing.set(true); loadEstateList() }

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
                estateVMList.add(position, EstateViewModel(estate, router))
                estateListAdapter.value?.notifyItemInserted(position)
            } else { // Update existing element at his position
                position = estateVMList.indexOf(oldEstateVM)
                estateVMList[position] = EstateViewModel(estate, router)
                estateListAdapter.value?.notifyItemChanged(position)
            }
            updateUi()
            // Set latitude and longitude for the address.
            setLatLngInAddress(estate.address, true)
        }
        return position
    }

    // -------------
    // UPDATE UI
    // -------------

    /**
     * Push data to recycler view when retrieve estate list.
     * @param estateList the list of estates retrieved from database.
     */
    @Suppress("unchecked_cast")
    private fun onRetrieveEstateList(estateList: List<Estate>) {
        // For Recycler adapter
        estateVMList.clear()
        estateVMList.addAll(estateList.map { estate -> EstateViewModel(estate, router) })
        estateListAdapter.value?.updateList(estateVMList as MutableList<Any>)
        estateListAdapter.value?.notifyDataSetChanged()
        // Set latitude and longitude for each estate address if not already do.
        estateList.forEach { setLatLngInAddress(it.address, false) }
        updateUi()
    }

    /**
     * Update Ui when received database request response, stop refreshing animation
     * and show text empty if list is empty.
     */
    private fun updateUi() {
        refreshing.set(false)
        showEmptyText.set(estateVMList.isEmpty())
    }

    /**
     * Update estate list with filtered or unfiltered estate list.
     * @param estateList the new estate list to set.
     */
    internal fun updateEstateList(estateList: List<Estate>) = onRetrieveEstateList(estateList)

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
        val hasAddressData = address.city.isNotBlank() && address.country.isNotBlank()
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

    // --- GETTERS ---

    val getEstateListAdapter: LiveData<RecyclerViewAdapter> = estateListAdapter

    val getRefreshing = refreshing

    val getShowEmptyText = showEmptyText
}