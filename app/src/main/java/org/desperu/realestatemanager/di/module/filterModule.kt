package org.desperu.realestatemanager.di.module

import org.desperu.realestatemanager.base.BaseActivity
import org.desperu.realestatemanager.filter.ManageFiltersHelper
import org.desperu.realestatemanager.ui.main.MainCommunication
import org.koin.dsl.module

/**
 * Koin module which provide dependencies related to list filters.
 */
val filterModule = module {

    /**
     * Provides a ManageFilters instance.
     */
    single {
        ManageFiltersHelper(
                get()
        )
    }
}