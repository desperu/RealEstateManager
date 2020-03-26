package org.desperu.realestatemanager.base

import androidx.lifecycle.ViewModel
import org.desperu.realestatemanager.injection.DaggerViewModelInjector
import org.desperu.realestatemanager.injection.ViewModelInjector
import org.desperu.realestatemanager.ui.main.EstateListViewModel
import org.desperu.realestatemanager.ui.main.EstateViewModel

abstract class BaseViewModel: ViewModel() { // TODO remove if unused

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