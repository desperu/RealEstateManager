package org.desperu.realestatemanager.database

import androidx.room.Database
import androidx.room.RoomDatabase
import org.desperu.realestatemanager.database.dao.EstateDao
import org.desperu.realestatemanager.database.dao.ImageDao
import org.desperu.realestatemanager.model.Estate
import org.desperu.realestatemanager.model.Image

@Database(entities = [Estate::class, Image::class], version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {

    abstract fun estateDao(): EstateDao

    abstract fun imageDao(): ImageDao
}