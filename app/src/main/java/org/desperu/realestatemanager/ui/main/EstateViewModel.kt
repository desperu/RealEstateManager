package org.desperu.realestatemanager.ui.main

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.desperu.realestatemanager.model.Estate
import org.desperu.realestatemanager.model.Image

class EstateViewModel(private val givenEstate: Estate): ViewModel() {

    // FOR DATA
    private val estate = MutableLiveData<Estate>()
    private val primaryImage = MutableLiveData<Image>()
    private lateinit var router: EstateRouter

    // SECOND CONSTRUCTOR
    constructor(givenEstate: Estate, router: EstateRouter): this(givenEstate) {
        this.router = router
    }

    init {
        setEstate()
    }

    // -------------
    // SET GIVEN ESTATE
    // -------------

    /**
     * Set given estate for the view model.
     */
    private fun setEstate() {
        estate.value = givenEstate
        if (!givenEstate.imageList.isNullOrEmpty())
            primaryImage.value = givenEstate.imageList.find { it.isPrimary } ?: givenEstate.imageList[0]
        else
            primaryImage.value = Image()
    }

    // -------------
    // LISTENER
    // -------------

    /**
     * Item on click listener.
     */
    val itemListener = View.OnClickListener { _ ->
        estate.value?.let { router.openEstateDetail(it) }
    }

    // --- GETTERS ---

    val getEstate: LiveData<Estate> = estate

    val getPrimaryImage: LiveData<Image> = primaryImage
}