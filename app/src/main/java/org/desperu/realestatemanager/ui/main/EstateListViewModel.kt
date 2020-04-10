package org.desperu.realestatemanager.ui.main

import android.view.View
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
import org.desperu.realestatemanager.view.RecyclerViewAdapter
import org.desperu.realestatemanager.view.updateList

class EstateListViewModel(private val estateRepository: EstateRepository,
                          private val imageRepository: ImageRepository,
                          private val addressRepository: AddressRepository,
                          private val router: EstateRouter): ViewModel() {

    // FOR DATA
    private val estateListAdapter: RecyclerViewAdapter = RecyclerViewAdapter(R.layout.item_estate)
    private var estateList = ArrayList<Estate>()
    private val mutableRefreshing = MutableLiveData<Boolean>()
    private val mutableVisibility = MutableLiveData<Int>()

    init {
        loadEstateList()
    }

    // -------------
    // LOAD ESTATE LIST
    // -------------

    /**
     * Load estate list with images and address for each, from database.
     */
    private fun loadEstateList() {
        viewModelScope.launch(Dispatchers.Main) {// TODO dispatch main or IO??
            estateList = estateRepository.getAll() as ArrayList<Estate>
            estateList.forEach { estate ->
                estate.imageList = imageRepository.getEstateImages(estate.id) as ArrayList<Image>
                estate.address = addressRepository.getAddress(estate.id)
            }
            onRetrieveEstateList()
        }
    }

    /**
     * Reload estate list from database.
     */
    fun reloadEstateList() = loadEstateList()

    // -------------
    // ADD ESTATE
    // -------------

    /**
     * Add new, or updated estate.
     * @param estate the new or updated estate.
     * @return the position affected in the recycler view.
     */
    fun addOrUpdateEstate(estate: Estate?): Int? {
        var position: Int? = null
        if (estate != null) {
            val oldEstate = estateList.find { it.id == estate.id }
            if (oldEstate == null) { // Add new element in first position
                position = 0
                estateList.add(position, estate)
                estateListAdapter.addItem(position, EstateViewModel(estate, router))
            } else { // Update existing element at his position
                position = estateList.indexOf(oldEstate)
                estateList.remove(oldEstate)
                estateList.add(position, estate)
                estateListAdapter.updateItem(position, estate)
            }
        }
        return position
    }

    // -------------
    // UPDATE UI
    // -------------

    /**
     * Push data to recycler view when retrieve estate list.
     */
    private fun onRetrieveEstateList() {
        val estateViewModelList = ArrayList<Any>()
        estateList.forEach { estate -> estateViewModelList.add(EstateViewModel(estate, router)) }
        estateListAdapter.updateList(estateViewModelList)
        updateList(estateViewModelList)
        updateUi()
    }

    /**
     * Update Ui when received database request response, stop refreshing animation
     * and show text empty if list is empty.
     */
    private fun updateUi() {
        mutableRefreshing.value = false
        mutableVisibility.value = if (estateList.isEmpty()) View.VISIBLE else View.GONE
    }

    // --- GETTERS ---

    val getEstateListAdapter = estateListAdapter

    val getEstateList = estateList // TODO remove field?

    val getMutableRefreshing = mutableRefreshing

    val getMutableVisibility = mutableVisibility

    // --- MANAGE ---

    fun deleteFullEstate(estateId: Long)  = viewModelScope.launch(Dispatchers.Main) {
        addressRepository.deleteAddress(estateId)
        imageRepository.deleteEstateImages(estateId)
        estateRepository.deleteEstate(estateId)
    }
}