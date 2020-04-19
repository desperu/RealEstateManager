package org.desperu.realestatemanager.database.dao

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.desperu.realestatemanager.database.EstateDatabase
import org.desperu.realestatemanager.model.Estate
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class EstateDaoInstrumentedTest {

    private lateinit var mDatabase: EstateDatabase
    // Create an Estate for Db test
    private lateinit var estate: Estate

    @get:Rule
    val instantTaskExecutorRule: InstantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    @Throws(Exception::class)
    fun initDbAndData() {
        // init Db for test
        mDatabase = DaoTestHelper().initDb()

        // Set estate data for Db test
        estate = DaoTestHelper().getEstate
    }

    @After
    @Throws(Exception::class)
    fun closeDb() { mDatabase.close() }

    @Test
    @ExperimentalCoroutinesApi
    fun insertAndGetEstate() = runBlockingTest {
        // Given an Estate that has been inserted into the DB
        val rowId = mDatabase.estateDao().insertEstate(estate)

        // When getting the Estate via the DAO
        val estateDb = mDatabase.estateDao().getEstate(rowId)

        // Then the retrieved Estate match the original estate object
        assertEquals(estate, estateDb)

        // Check that the row id and the estate id match
        assertEquals(rowId, estate.id)

        // Clean up coroutines
        cleanupTestCoroutines()
    }

    @Test
    @ExperimentalCoroutinesApi
    fun updateAndGetAll() = runBlockingTest {
        // Insert an estate in database for the test
        mDatabase.estateDao().insertEstate(estate)

        // Change some data to update them in database
        estate.price = 1700000L
        estate.state = "Sold Out"
        estate.soldDate = "20/04/2020"

        // Given an Estate that has been updated into the DB
        val rowAffected = mDatabase.estateDao().updateEstate(estate)

        // Check that's there only one row affected when updating
        assertEquals(1, rowAffected)

        // When getting the Estate via the DAO
        val estateDb = mDatabase.estateDao().getAll()

        // Check that the retrieved Estate match the original estate object
        assertEquals(estate, estateDb[0])

        // Clean up coroutines
        cleanupTestCoroutines()
    }

    @Test
    @ExperimentalCoroutinesApi
    fun deleteEstateAndCheckDb() = runBlockingTest {
        // Insert an estate in database for the test
        mDatabase.estateDao().insertEstate(estate)

        // Given an Estate that has been deleted into the DB
        val rowAffected = mDatabase.estateDao().deleteEstate(estate.id)

        // Check that's there only one row affected when deleting
        assertEquals(1, rowAffected)

        // When getting the Estate via the DAO
        val estateDb = mDatabase.estateDao().getAll()

        // Then the retrieved Estate match the original estate object
        assertTrue(estateDb.isEmpty())

        // Clean up coroutines
        cleanupTestCoroutines()
    }
}