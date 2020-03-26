package org.desperu.realestatemanager.injection

import dagger.Component
import org.desperu.realestatemanager.ui.main.EstateListViewModel
import org.desperu.realestatemanager.ui.main.EstateViewModel
import javax.inject.Singleton

/**
 * Component providing inject() methods for presenters.
 */
@Singleton
@Component
interface ViewModelInjector { // TODO remove if unused

    /**
     * Inject required dependencies into the specified EstateViewModel.
     * @param estateViewModel EstateViewModel in witch to inject the dependencies.
     */
    fun inject(estateViewModel: EstateViewModel)

    /**
     * Inject required dependencies into the specified EstateListViewModel.
     * @param estateListViewModel EstateListViewModel in witch to inject the dependencies.
     */
    fun inject(estateListViewModel: EstateListViewModel)

    @Component.Builder
    interface Builder {
        fun build(): ViewModelInjector
    }
}