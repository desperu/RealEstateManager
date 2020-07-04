package org.desperu.realestatemanager.di.module

import org.desperu.realestatemanager.filter.ManageFiltersHelper
import org.koin.dsl.module

/**
 * Koin module which provide dependencies related to list filters.
 */
val filterModule = module {

    /**
     * Provides a ManageFilters single instance.
     */
    single {
        ManageFiltersHelper(
                get()
        )
    }
}