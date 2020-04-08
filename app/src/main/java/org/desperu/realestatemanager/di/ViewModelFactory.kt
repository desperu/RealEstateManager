package org.desperu.realestatemanager.di

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.desperu.realestatemanager.repositories.AddressRepository
import org.desperu.realestatemanager.repositories.EstateRepository
import org.desperu.realestatemanager.repositories.ImageRepository
import org.desperu.realestatemanager.ui.main.EstateListViewModel
import org.desperu.realestatemanager.ui.manageEstate.ManageEstateViewModel
import org.koin.java.KoinJavaComponent.inject

class ViewModelFactory(private val activity: Activity): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EstateListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EstateListViewModel(
                    inject(EstateRepository::class.java).value,
                    inject(ImageRepository::class.java).value,
                    inject(AddressRepository::class.java).value) as T
        } else if (modelClass.isAssignableFrom(ManageEstateViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ManageEstateViewModel(
                    inject(EstateRepository::class.java).value,
                    inject(ImageRepository::class.java).value,
                    inject(AddressRepository::class.java).value) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}