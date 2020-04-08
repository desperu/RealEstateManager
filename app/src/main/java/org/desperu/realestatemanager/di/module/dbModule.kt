package org.desperu.realestatemanager.di.module

import androidx.room.Room
import org.desperu.realestatemanager.database.EstateDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/**
 * The database name of the application.
 */
private const val DATABASE_NAME = "estate"

/**
 * Koin module which provides dependency related to database.
 */
val dbModule = module{

    /**
     * Provides the database of the application.
     */
    single{
        Room.databaseBuilder(androidContext(), EstateDatabase::class.java, DATABASE_NAME).build()
    }

    /**
     * Provides the database access object for estates.
     */
    single{
        (get() as EstateDatabase).estateDao()
    }

    /**
     * Provides the database access object for images.
     */
    single{
        (get() as EstateDatabase).imageDao()
    }

    /**
     * Provides the database access object for address.
     */
    single{
        (get() as EstateDatabase).addressDao()
    }
}