package org.desperu.realestatemanager.database

import androidx.room.Database
import androidx.room.RoomDatabase
import org.desperu.realestatemanager.database.dao.AddressDao
import org.desperu.realestatemanager.database.dao.EstateDao
import org.desperu.realestatemanager.database.dao.ImageDao
import org.desperu.realestatemanager.model.Address
import org.desperu.realestatemanager.model.Estate
import org.desperu.realestatemanager.model.Image

/**
 * The database class of the application.
 */
@Database(entities = [Estate::class, Image::class, Address::class], version = 1)
abstract class EstateDatabase: RoomDatabase() {

    /**
     * Returns the database access object for estate.
     * @return the database access object for estate.
     */
    abstract fun estateDao(): EstateDao

    /**
     * Returns the database access object for image.
     * @return the database access object for image.
     */
    abstract fun imageDao(): ImageDao

    /**
     * Returns the database access object for address.
     * @return the database access object for address.
     */
    abstract fun addressDao(): AddressDao
}