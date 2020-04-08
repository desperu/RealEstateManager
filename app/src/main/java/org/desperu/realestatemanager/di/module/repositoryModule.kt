package org.desperu.realestatemanager.di.module

import org.desperu.realestatemanager.repositories.*
import org.koin.dsl.module

/**
 * Koin module which provide dependencies related to repositories
 */
val repositoryModule = module {

    /**
     * Provides an EstateDataRepository instance.
     */
    single<EstateRepository> {
        EstateRepositoryImpl(
                get()
        )
    }

    /**
     * Provides an ImageRepository instance.
     */
    single<ImageRepository> {
        ImageRepositoryImpl(
                get()
        )
    }

    /**
     * Provides an AddressRepository instance.
     */
    single<AddressRepository> {
        AddressRepositoryImpl(
                get()
        )
    }
}