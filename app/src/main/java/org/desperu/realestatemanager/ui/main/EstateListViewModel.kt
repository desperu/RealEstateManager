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
                          private val addressRepository: AddressRepository): ViewModel() {

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
        viewModelScope.launch(Dispatchers.Main) {
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
    // UPDATE UI
    // -------------

    /**
     * Push data to recycler view when retrieve, and stop swipe refreshing animation.
     */
    private fun onRetrieveEstateList() {
        val estateViewModelList = ArrayList<Any>()
        estateList.forEach { estate -> estateViewModelList.add(EstateViewModel(estate)) }
        estateListAdapter.updateList(estateViewModelList)
        updateList(estateViewModelList)
        updateUi()
    }

    /**
     * Update Ui when received database request response.
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

    fun deleteFullEstate(estateId: Long) {
        viewModelScope.launch(Dispatchers.Main) {
            addressRepository.deleteAddress(estateId)
            imageRepository.deleteEstateImages(estateId)
            estateRepository.deleteEstate(estateId)
        }
    }
}