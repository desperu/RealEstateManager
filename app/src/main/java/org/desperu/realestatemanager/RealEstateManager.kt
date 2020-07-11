package org.desperu.realestatemanager

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import org.desperu.realestatemanager.di.module.dbModule
import org.desperu.realestatemanager.di.module.filterModule
import org.desperu.realestatemanager.di.module.repositoryModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.KoinContextHandler
import org.koin.core.context.startKoin

open class RealEstateManager: Application() {

    /**
     * Initializes the application, by adding strict mode and starting koin.
     */
    override fun onCreate() {
        super.onCreate()
        if (KoinContextHandler.getOrNull() == null) { // For Robolectric in unit test.
            startKoin {
                androidLogger()
                androidContext(this@RealEstateManager)
                modules(listOf(dbModule, repositoryModule, filterModule))
            }
        }

        // Support for kitkat bug with setImageDrawable() and vector drawable.
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    }
}