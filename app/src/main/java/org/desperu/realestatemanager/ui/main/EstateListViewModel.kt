package org.desperu.realestatemanager.ui.main

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.desperu.realestatemanager.R
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
                          private val executor: Executor): ViewModel() {

    // FOR DATA
    private val estateListAdapter: RecyclerViewAdapter = RecyclerViewAdapter(R.layout.item_estate)
    private var estateList = ArrayList<Estate>()
    private val mutableRefreshing = MutableLiveData<Boolean>()
    private val mutableVisibility = MutableLiveData<Int>()

    private lateinit var subscribeEstate: Disposable
    private lateinit var subscribeImages: Disposable
    private lateinit var subscribeAddress: Disposable
    private var count = 0

    init {
        loadEstateList()
    }

    // -------------
    // LOAD ESTATE LIST
    // -------------

    /**
     * Load estate list, from database.
     */
    private fun loadEstateList() {
        subscribeEstate = estateDataRepository.getAll
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { estateList -> this.estateList = estateList as ArrayList<Estate>
                            count = 0
                            for (estate in estateList) {
                                count++
                                loadImagesForEstate(estate)
                                loadAddressForEstate(estate)
                            }
                        },
                        { mutableVisibility.value = View.VISIBLE }
                )
    }

    /**
     * Load images list for estate.
     */
    private fun loadImagesForEstate(estate: Estate) {
        subscribeImages = imageDataRepository.getImageList(estate.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { imageList -> estate.imageList = imageList as ArrayList<Image>
                            if (estateList.size == count) onRetrieveEstateList()
                        },
                        { mutableVisibility.value = View.VISIBLE }
                )
    }

    /**
     * Load address for estate.
     */
    private fun loadAddressForEstate(estate: Estate) {
        subscribeAddress = addressDataRepository.getAddress(estate.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { address -> estate.address = address
                            if (estateList.size == count) onRetrieveEstateList()
                        },
                        { mutableVisibility.value = View.VISIBLE }
                )
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
        for (estate in estateList) estateViewModelList.add(EstateViewModel(estate))
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

    fun deleteFullEstate(estateId: Long) {
        executor.execute {
            addressDataRepository.deleteAddress(estateId)
            imageDataRepository.deleteEstateImages(estateId)
            estateDataRepository.deleteEstate(estateId)
        }
    }
}