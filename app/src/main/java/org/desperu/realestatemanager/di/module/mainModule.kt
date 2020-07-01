package org.desperu.realestatemanager.di.module

import org.desperu.realestatemanager.base.BaseActivity
import org.desperu.realestatemanager.ui.main.MainCommunication
import org.koin.dsl.module

/**
 * Koin module which provide dependencies related to list filters.
 */
val mainModule = module {

    /**
     * Provides a MainCommunication interface from the instance of MainActivity.
     */
    single { (activity: BaseActivity) ->
        activity as MainCommunication
    }
}