package org.desperu.realestatemanager.injection

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import org.desperu.realestatemanager.database.AppDatabase
import org.desperu.realestatemanager.repositories.AddressDataRepository
import org.desperu.realestatemanager.repositories.EstateDataRepository
import org.desperu.realestatemanager.repositories.ImageDataRepository
import org.desperu.realestatemanager.ui.main.EstateListViewModel
import org.desperu.realestatemanager.ui.manageEstate.ManageEstateViewModel

class ViewModelFactory(private val activity: AppCompatActivity): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EstateListViewModel::class.java)) {
            val db = Room.databaseBuilder(activity.applicationContext, AppDatabase::class.java, "estate").build()
            @Suppress("UNCHECKED_CAST")
            return EstateListViewModel(EstateDataRepository(db.estateDao()),
                    ImageDataRepository(db.imageDao()),
                    AddressDataRepository(db.addressDao())) as T
        } else if (modelClass.isAssignableFrom(ManageEstateViewModel::class.java)) {
            val db = Room.databaseBuilder(activity.applicationContext, AppDatabase::class.java, "estate").build()
            @Suppress("UNCHECKED_CAST")
            return ManageEstateViewModel(EstateDataRepository(db.estateDao()),
                    ImageDataRepository(db.imageDao()),
                    AddressDataRepository(db.addressDao())) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}