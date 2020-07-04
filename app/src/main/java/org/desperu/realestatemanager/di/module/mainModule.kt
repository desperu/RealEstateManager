package org.desperu.realestatemanager.di.module

import org.desperu.realestatemanager.base.BaseActivity
import org.desperu.realestatemanager.ui.main.MainCommunication
import org.desperu.realestatemanager.ui.main.MainFragmentManager
import org.koin.core.parameter.parametersOf
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

    /**
     * Provides a MainFragmentManager single instance for the MainActivity instance.
     */
    single { (activity: BaseActivity) ->
        MainFragmentManager(activity, get { parametersOf(activity) })
    }
}