package org.desperu.realestatemanager.injection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.desperu.realestatemanager.database.AppDatabase
import org.desperu.realestatemanager.repositories.AddressDataRepository
import org.desperu.realestatemanager.repositories.EstateDataRepository
import org.desperu.realestatemanager.repositories.ImageDataRepository
import org.desperu.realestatemanager.ui.main.EstateListViewModel
import org.desperu.realestatemanager.ui.manageEstate.ManageEstateViewModel
import java.util.concurrent.Executor

class ViewModelFactory(private val db: AppDatabase,
                       private val executor: Executor): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EstateListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EstateListViewModel(EstateDataRepository(db.estateDao()),
                    ImageDataRepository(db.imageDao()),
                    AddressDataRepository(db.addressDao()),
                    executor) as T
        } else if (modelClass.isAssignableFrom(ManageEstateViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ManageEstateViewModel(EstateDataRepository(db.estateDao()),
                    ImageDataRepository(db.imageDao()),
                    AddressDataRepository(db.addressDao()),
                    executor) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}