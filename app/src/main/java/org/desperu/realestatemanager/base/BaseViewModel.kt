package org.desperu.realestatemanager.base

import androidx.lifecycle.ViewModel
import org.desperu.realestatemanager.injection.DaggerViewModelInjector
import org.desperu.realestatemanager.injection.ViewModelInjector
import org.desperu.realestatemanager.ui.EstateListViewModel
import org.desperu.realestatemanager.ui.EstateViewModel

abstract class BaseViewModel: ViewModel() {

    private val injector: ViewModelInjector = DaggerViewModelInjector
            .builder()
            .build()

    init {
        inject()
    }

    /**
     * Inject the required dependencies
     */
    private fun inject() {
        when (this) {
            is EstateViewModel -> injector.inject(this)
            is EstateListViewModel -> injector.inject(this)
        }
    }
}