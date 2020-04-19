package org.desperu.realestatemanager.database.dao

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import org.desperu.realestatemanager.database.EstateDatabase
import org.desperu.realestatemanager.model.Address
import org.desperu.realestatemanager.model.Estate
import org.desperu.realestatemanager.model.Image


class DaoTestHelper {

    /**
     * Init Database for Dao test.
     * @return the created EstateDatabase instance.
     */
    internal fun initDb(): EstateDatabase =
            // init Db for test
            Room.inMemoryDatabaseBuilder(
                    InstrumentationRegistry.getInstrumentation().context,
                    EstateDatabase::class.java)
                    // allowing main thread queries, just for testing
                    .allowMainThreadQueries()
                    .build()

    /**
     * Set and return estate data for Db test.
     */
    internal val getEstate = Estate(
            1L,
            "Flat",
            1500000L,
            200,
            15,
            "A beautiful Flat !",
            "Park",
            "For Sale",
            "15/04/2020",
            "",
            "Desperu")

    /**
     * Set and return image list data fo Db test.
     * @param estateId the given estate id for this image list.
     * @return the created image list.
     */
    internal fun getImageList(estateId: Long) = listOf(
            Image(1L, estateId, "an uri ...", true, "bathroom"),
            Image(2L, estateId, "another uir !!!", false, "bedroom")
    )

    /**
     * Set and return an image data for Db test.
     * @param estateId the given estate id for this image.
     * @return the created image object.
     */
    internal fun getImage(estateId: Long) = Image(
            3L,
            estateId,
            "uri",
            false,
            "kitchen"
    )

    /**
     * Set and return an Address data for Db test.
     * @param estateId the given estate id for this address.
     * @return the created address object.
     */
    internal fun getAddress(estateId: Long) = Address(
            estateId,
            323,
            "Mountainview Dr. Brooklyn",
            "flat 45",
            11204,
            "New York",
            "United States")
}