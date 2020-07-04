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
import org.desperu.realestatemanager.ui.creditSimulator.CreditSimulatorViewModel
import org.desperu.realestatemanager.ui.main.ToolbarViewModel
import org.desperu.realestatemanager.ui.main.estateDetail.EstateDetailViewModel
import org.desperu.realestatemanager.ui.main.estateDetail.ImagesRouterImpl
import org.desperu.realestatemanager.ui.main.estateList.EstateListViewModel
import org.desperu.realestatemanager.ui.main.estateList.EstateRouter
import org.desperu.realestatemanager.ui.main.estateList.EstateRouterImpl
import org.desperu.realestatemanager.ui.main.estateMap.MapsViewModel
import org.desperu.realestatemanager.ui.main.filter.FilterVMCommunicationImpl
import org.desperu.realestatemanager.ui.main.filter.FilterViewModel
import org.desperu.realestatemanager.ui.manageEstate.fragment.ManageEstateVMCommunication
import org.desperu.realestatemanager.ui.manageEstate.fragment.ManageEstateVMCommunicationImpl
import org.desperu.realestatemanager.ui.manageEstate.fragment.ManageEstateViewModel
import org.koin.core.KoinComponent
import org.koin.core.get
import org.koin.core.parameter.parametersOf
import org.koin.java.KoinJavaComponent.inject

/**
 * Factory class which instantiates ViewModels.
 *
 * @param activity the activity witch ask for as view model instance.
 *
 * @constructor Provide the asked View Model Class.
 *
 * @property activity the activity witch ask for as view model instance to set.
 */
@Suppress("unchecked_cast")
class ViewModelFactory(private val activity: AppCompatActivity): ViewModelProvider.Factory, KoinComponent {

    /**
     * Instantiates the expected ViewModel and returns it.
     *
     * @param modelClass the model class to provide.
     *
     * @return the instance of the expected ViewModel.
     *
     * @throws IllegalArgumentException if the class of the ViewModel is not one of the expected ones.
     */
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        when {

            // Return an EstateListViewModel instance.
            modelClass.isAssignableFrom(EstateListViewModel::class.java) -> {
                val router = EstateRouterImpl(activity)
                val geocoder = GeocoderServiceImpl(activity)
                return EstateListViewModel(
                        inject(EstateRepository::class.java).value,
                        inject(ImageRepository::class.java).value,
                        inject(AddressRepository::class.java).value,
                        get { parametersOf(activity) },
                        router as EstateRouter,
                        geocoder as GeocoderService) as T
            }

            // Return a ManageEstateViewModel instance.
            modelClass.isAssignableFrom(ManageEstateViewModel::class.java) -> {
                val communication = ManageEstateVMCommunicationImpl(activity)
                val resourceService = ResourceServiceImpl(activity)
                return ManageEstateViewModel(
                        inject(EstateRepository::class.java).value,
                        inject(ImageRepository::class.java).value,
                        inject(AddressRepository::class.java).value,
                        communication as ManageEstateVMCommunication,
                        resourceService as ResourceService) as T
            }

            // Return a MapsViewModel instance.
            modelClass.isAssignableFrom(MapsViewModel::class.java) -> {
                val router = EstateRouterImpl(activity)
                return MapsViewModel(
                        router) as T
            }

            // Return an EstateDetailViewModel instance.
            modelClass.isAssignableFrom(EstateDetailViewModel::class.java) -> {
                val router = ImagesRouterImpl(activity)
                return EstateDetailViewModel(router) as T
            }

            // Return a ToolbarViewModel instance.
            modelClass.isAssignableFrom(ToolbarViewModel::class.java) ->
                return ToolbarViewModel(
                        get()) as T

            // Return a FilterViewModel instance.
            modelClass.isAssignableFrom(FilterViewModel::class.java) -> {
                val communication = FilterVMCommunicationImpl(activity)
                return FilterViewModel(
                        get(),
                        communication) as T
            }

            // Return a CreditSimulatorViewModel instance.
            modelClass.isAssignableFrom(CreditSimulatorViewModel::class.java) ->
                return CreditSimulatorViewModel() as T

            // Asked View Model Class Not Found.
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}