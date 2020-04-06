package org.desperu.realestatemanager.injection

import android.app.Activity
import androidx.room.Room
import org.desperu.realestatemanager.database.AppDatabase
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class Injection {

    private var db: AppDatabase? = null
    private var executor: Executor? = null

    /**
     * Provide db instance.
     */
    private fun provideDb(activity: Activity): AppDatabase {
        if (db == null)
            db = Room.databaseBuilder(activity.applicationContext, AppDatabase::class.java, "estate").build()
        return db!!
    }

    /**
     * Provide executor instance.
     */
    private fun provideExecutor(): Executor {
        if (executor == null) executor = Executors.newSingleThreadExecutor()
        return executor!!
    }

    /**
     * Provide View Model Factory.
     */
    fun provideViewModelFactory(activity: Activity) = ViewModelFactory(provideDb(activity), provideExecutor())
}