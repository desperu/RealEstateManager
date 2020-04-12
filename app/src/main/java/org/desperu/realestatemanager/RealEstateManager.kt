package org.desperu.realestatemanager

import android.app.Application
import org.desperu.realestatemanager.di.module.dbModule
import org.desperu.realestatemanager.di.module.repositoryModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class RealEstateManager: Application() {

    /**
     * Initializes the application, by adding strict mode and starting koin.
     */
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@RealEstateManager)
            modules(listOf(dbModule, repositoryModule))
        }
    }
}