package org.desperu.realestatemanager.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.desperu.realestatemanager.R
import org.desperu.realestatemanager.base.BaseViewModel
import org.desperu.realestatemanager.model.Estate
import org.desperu.realestatemanager.repositories.EstateDataRepository
import org.desperu.realestatemanager.repositories.ImageDataRepository
import org.desperu.realestatemanager.view.RecyclerViewAdapter

class EstateListViewModel(private val estateDataRepository: EstateDataRepository,
                          private val imageDataRepository: ImageDataRepository): BaseViewModel() {

    // FOR DATA
    val estateListAdapter: RecyclerViewAdapter = RecyclerViewAdapter(R.layout.item_estate)
    private val estateList = MutableLiveData<List<Estate>>()
    val mutableRefreshing = MutableLiveData<Boolean>()

    init {
        loadEstateList()
    }

    // -------------
    // LOAD ESTATE LIST
    // -------------

    /**
     * Load estate list with image for each, from database.
     */
    private fun loadEstateList() {
        estateList.value = estateDataRepository.getAll.value
        if (estateList.value != null)
            for (estate in estateList.value!!)
                estate.images = imageDataRepository.getImages(estate.id).value!!
        onRetrieveEstateList()
    }

    /**
     * Reload estate list from database.
     */
    fun reloadEstateList() = loadEstateList()

    /**
     * Push data to recycler view when retrieve, and stop swipe refreshing animation.
     */
    private fun onRetrieveEstateList() {
        estateList.value?.let { estateListAdapter.updateList(it) }
        mutableRefreshing.value = false
    }

    // --- GETTERS ---

    fun getEstate(estateId: Long): LiveData<Estate> = estateDataRepository.getEstate(estateId)

    val getAll: MutableLiveData<List<Estate>> = estateList

    // --- MANAGE ---

    fun insertEstate(estate: Estate) { estateDataRepository.createEstate(estate) }

    fun updateEstate(estate: Estate) { estateDataRepository.updateEstate(estate) }

    fun deleteEstate(estateId: Long) { estateDataRepository.deleteEstate(estateId) }
}