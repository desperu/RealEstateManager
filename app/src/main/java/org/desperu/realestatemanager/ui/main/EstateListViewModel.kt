package org.desperu.realestatemanager.ui.main

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.desperu.realestatemanager.R
import org.desperu.realestatemanager.base.BaseViewModel
import org.desperu.realestatemanager.model.Address
import org.desperu.realestatemanager.model.Estate
import org.desperu.realestatemanager.model.Image
import org.desperu.realestatemanager.repositories.AddressDataRepository
import org.desperu.realestatemanager.repositories.EstateDataRepository
import org.desperu.realestatemanager.repositories.ImageDataRepository
import org.desperu.realestatemanager.view.RecyclerViewAdapter

class EstateListViewModel(private val estateDataRepository: EstateDataRepository, // TODO move to main package
                          private val imageDataRepository: ImageDataRepository,
                          private val addressDataRepository: AddressDataRepository): BaseViewModel() {

    // FOR DATA
    private val estateListAdapter: RecyclerViewAdapter = RecyclerViewAdapter(R.layout.item_estate)
    private val estateListLV = MutableLiveData<List<Estate>>()
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
     * Load estate list with image for each, from database.
     */
    private fun loadEstateList() {
        estateList = ArrayList()
        estateListLV.value = estateDataRepository.getAll.value
        if (estateListLV.value != null) estateList = (estateListLV.value as? ArrayList<Estate>)!! // TODO remove LiveData from estateDao
        for (estate in estateList) {
            estate.images = imageDataRepository.getImages(estate.id).value!!
            estate.address = addressDataRepository.getAddress(estate.id).value!!
        }
        onRetrieveEstateList()
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
        val estateViewModelList = ArrayList<EstateViewModel>()
        // TODO for test
        val estate = Estate(0,"type", 0, 0, 0, "description", "interest place", "state", "sale date", "sod date", "agent")
        estate.images = listOf(Image(0, 0, "content://com.google.android.apps.photos.contentprovider/-1/1/content%3A%2F%2Fmedia%2Fexternal%2Fimages%2Fmedia%2F123/ORIGINAL/NONE/2100203926", true, "toto"))
        estate.address = Address(0, 12, "street name", "flat", 3000, "Rennes", "France")
        estateList = arrayListOf(estate)
        for (estate in estateList)
            estateViewModelList.add(EstateViewModel(estate))
        estateListAdapter.updateList(estateViewModelList)
//        estateList.value?.let { estateListAdapter.updateList(it) }
        // For UI
        mutableRefreshing.value = false
        mutableVisibility.value = if (estateList.isEmpty()) View.VISIBLE else View.GONE
    }

    // --- GETTERS ---

    val getEstateListAdapter = estateListAdapter

    val getMutableRefreshing = mutableRefreshing

    val getMutableVisibility = mutableVisibility

    fun getEstate(estateId: Long): LiveData<Estate> = estateDataRepository.getEstate(estateId)

//    val getAll: MutableLiveData<List<Estate>> = estateList

    // --- MANAGE ---

    fun insertEstate(estate: Estate) { estateDataRepository.createEstate(estate) }

    fun updateEstate(estate: Estate) { estateDataRepository.updateEstate(estate) }

    fun deleteEstate(estateId: Long) { estateDataRepository.deleteEstate(estateId) }
}