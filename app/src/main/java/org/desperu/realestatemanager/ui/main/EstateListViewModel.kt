package org.desperu.realestatemanager.ui.main

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.desperu.realestatemanager.R
import org.desperu.realestatemanager.model.Estate
import org.desperu.realestatemanager.repositories.AddressRepository
import org.desperu.realestatemanager.repositories.EstateRepository
import org.desperu.realestatemanager.repositories.ImageRepository
import org.desperu.realestatemanager.view.RecyclerViewAdapter
import org.desperu.realestatemanager.view.updateList

/**
 * View Model witch provide data for estate list.
 */
class EstateListViewModel(private val estateRepository: EstateRepository,
                          private val imageRepository: ImageRepository,
                          private val addressRepository: AddressRepository,
                          private val router: EstateRouter): ViewModel() {

    // FOR DATA
    private val estateListAdapter: RecyclerViewAdapter = RecyclerViewAdapter(R.layout.item_estate)
    private lateinit var estateVMList: MutableList<EstateViewModel>
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
        viewModelScope.launch(Dispatchers.Main) {
            val estateList = estateRepository.getAll().toMutableList()
            estateList.forEach { estate ->
                estate.imageList = imageRepository.getEstateImages(estate.id).toMutableList()
                estate.address = addressRepository.getAddress(estate.id)
            }
            onRetrieveEstateList(estateList)
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
            val oldEstateVM = estateVMList.find { it.getEstate.value?.id == estate.id }
            if (oldEstateVM == null) { // Add new element in first position
                position = 0
                estateVMList.add(position, EstateViewModel(estate, router))
                estateListAdapter.notifyItemInserted(position)
            } else { // Update existing element at his position
                position = estateVMList.indexOf(oldEstateVM)
                estateVMList[position] = EstateViewModel(estate, router)
                estateListAdapter.notifyItemChanged(position)
            }
            updateUi()
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
        estateVMList = (estateList.map { estate -> EstateViewModel(estate, router) }).toMutableList()
        estateListAdapter.updateList(estateVMList as MutableList<Any>)
        estateListAdapter.notifyDataSetChanged()
        updateList(estateVMList as MutableList<Any>)
        updateUi()
    }

    /**
     * Update Ui when received database request response, stop refreshing animation
     * and show text empty if list is empty.
     */
    private fun updateUi() {
        mutableRefreshing.value = false
        mutableVisibility.value = if (estateVMList.isEmpty()) View.VISIBLE else View.GONE
    }

    // --- GETTERS ---

    val getEstateListAdapter = estateListAdapter

    val getEstateVMList = if (::estateVMList.isInitialized) estateVMList else mutableListOf()

    val getMutableRefreshing = mutableRefreshing

    val getMutableVisibility = mutableVisibility

    // --- MANAGE ---

    fun deleteFullEstate(estateId: Long)  = viewModelScope.launch(Dispatchers.Main) {
        addressRepository.deleteAddress(estateId)
        imageRepository.deleteEstateImages(estateId)
        estateRepository.deleteEstate(estateId)
    }
}