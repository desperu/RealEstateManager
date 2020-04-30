package org.desperu.realestatemanager.di

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.desperu.realestatemanager.repositories.AddressRepository
import org.desperu.realestatemanager.repositories.EstateRepository
import org.desperu.realestatemanager.repositories.ImageRepository
import org.desperu.realestatemanager.service.GeocoderService
import org.desperu.realestatemanager.service.GeocoderServiceImpl
import org.desperu.realestatemanager.service.ResourceService
import org.desperu.realestatemanager.service.ResourceServiceImpl
import org.desperu.realestatemanager.ui.main.estateList.EstateListViewModel
import org.desperu.realestatemanager.ui.main.estateList.EstateRouter
import org.desperu.realestatemanager.ui.main.estateList.EstateRouterImpl
import org.desperu.realestatemanager.ui.manageEstate.ManageEstateVMCommunication
import org.desperu.realestatemanager.ui.manageEstate.ManageEstateVMCommunicationImpl
import org.desperu.realestatemanager.ui.manageEstate.ManageEstateViewModel
import org.koin.java.KoinJavaComponent.inject

/**
 * Factory class which instantiates ViewModels.
 */
@Suppress("unchecked_cast")
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
            val geocoder = GeocoderServiceImpl(activity)
            return EstateListViewModel(
                    inject(EstateRepository::class.java).value,
                    inject(ImageRepository::class.java).value,
                    inject(AddressRepository::class.java).value,
                    router as EstateRouter,
                    geocoder as GeocoderService) as T
        } else if (modelClass.isAssignableFrom(ManageEstateViewModel::class.java)) {
            val resourceService = ResourceServiceImpl(activity)
            val communication = ManageEstateVMCommunicationImpl(activity)
            return ManageEstateViewModel(
                    inject(EstateRepository::class.java).value,
                    inject(ImageRepository::class.java).value,
                    inject(AddressRepository::class.java).value,
                    communication as ManageEstateVMCommunication,
                    resourceService as ResourceService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}