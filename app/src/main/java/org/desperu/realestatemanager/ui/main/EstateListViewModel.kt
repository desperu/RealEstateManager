package org.desperu.realestatemanager.ui.main

import android.view.View
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
import org.desperu.realestatemanager.view.updateList
import java.util.concurrent.Executor

class EstateListViewModel(private val estateDataRepository: EstateDataRepository,
                          private val imageDataRepository: ImageDataRepository,
                          private val addressDataRepository: AddressDataRepository,
                          private val executor: Executor): BaseViewModel() {

    // FOR DATA
    private val estateListAdapter: RecyclerViewAdapter = RecyclerViewAdapter(R.layout.item_estate)
    private val estateList = MutableLiveData<List<Estate>>()
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
        estateList.value = ArrayList<Estate>()
        estateDataRepository.getAll.observeForever {
            t -> estateList.value = t //} // TODO remove LiveData from estateDao
            estateList.value?.let { it ->
                for (estate in it) {
                    estate.imageList = ArrayList<Image>()
                    estate.address = Address()
                    imageDataRepository.getImageList(estate.id).observeForever {
                        t -> if (t.isNotEmpty()) estate.imageList = t as ArrayList<Image>
                        onRetrieveEstateList()
                    }
                    addressDataRepository.getAddress(estate.id).observeForever {
                        t -> if (t != null) estate.address = t
                        onRetrieveEstateList() }
                }
            }
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
        val estateViewModelList = ArrayList<Any>()
        estateList.value?.let { for (estate in it) estateViewModelList.add(EstateViewModel(estate)) }
        estateListAdapter.updateList(estateViewModelList)
        updateList(estateViewModelList)
        // For UI
        mutableRefreshing.value = false
        mutableVisibility.value = if (estateViewModelList.isEmpty()) View.VISIBLE else View.GONE
    }

    // --- GETTERS ---

    val getEstateListAdapter = estateListAdapter

    val getEstateList = estateList

    val getMutableRefreshing = mutableRefreshing

    val getMutableVisibility = mutableVisibility

    // --- MANAGE ---

    fun deleteFullEstate(estateId: Long) { executor.execute {
        addressDataRepository.deleteAddress(estateId)
        imageDataRepository.deleteEstateImages(estateId)
        estateDataRepository.deleteEstate(estateId) } }
}