package org.desperu.realestatemanager.di

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.desperu.realestatemanager.repositories.AddressRepository
import org.desperu.realestatemanager.repositories.EstateRepository
import org.desperu.realestatemanager.repositories.ImageRepository
import org.desperu.realestatemanager.service.ResourceService
import org.desperu.realestatemanager.service.ResourceServiceImpl
import org.desperu.realestatemanager.ui.main.EstateListViewModel
import org.desperu.realestatemanager.ui.main.EstateRouter
import org.desperu.realestatemanager.ui.main.EstateRouterImpl
import org.desperu.realestatemanager.ui.manageEstate.ManageEstateCommunication
import org.desperu.realestatemanager.ui.manageEstate.ManageEstateCommunicationImpl
import org.desperu.realestatemanager.ui.manageEstate.ManageEstateViewModel
import org.koin.java.KoinJavaComponent.inject

/**
 * Factory class which instantiates ViewModels.
 */
class ViewModelFactory(private val activity: AppCompatActivity): ViewModelProvider.Factory {

    /**
     * Instantiates the expected ViewModel and returns it.
     * @param modelClass the model class to provide.
     * @return the instance of the expected ViewModel.
     * @throws IllegalArgumentException if the class of the ViewModel is not one of the expected ones.
     */
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EstateListViewModel::class.java)) {
            val router = EstateRouterImpl(activity)
            @Suppress("UNCHECKED_CAST")
            return EstateListViewModel(
                    inject(EstateRepository::class.java).value,
                    inject(ImageRepository::class.java).value,
                    inject(AddressRepository::class.java).value,
                    router as EstateRouter) as T
        } else if (modelClass.isAssignableFrom(ManageEstateViewModel::class.java)) {
            val resourceService = ResourceServiceImpl(activity) // TODO use koin to inject, and this@RealEstateManager as context
            val communication = ManageEstateCommunicationImpl(activity)
            @Suppress("UNCHECKED_CAST")
            return ManageEstateViewModel(
                    inject(EstateRepository::class.java).value,
                    inject(ImageRepository::class.java).value,
                    inject(AddressRepository::class.java).value,
                    communication as ManageEstateCommunication,
                    resourceService as ResourceService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}