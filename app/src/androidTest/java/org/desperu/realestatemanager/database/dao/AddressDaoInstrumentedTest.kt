package org.desperu.realestatemanager.database.dao

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.desperu.realestatemanager.database.EstateDatabase
import org.desperu.realestatemanager.model.Address
import org.desperu.realestatemanager.model.Estate
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class AddressDaoInstrumentedTest {

    private lateinit var mDatabase: EstateDatabase
    // Create an Estate for foreign key
    private var estateId = 1L
    // Create an Address for Db test
    private lateinit var address: Address

    @get:Rule
    val instantTaskExecutorRule: InstantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    @Throws(Exception::class)
    @ExperimentalCoroutinesApi
    fun initDbAndData() {
        // init Db for test
        mDatabase = DaoTestHelper().initDb()

        // Set Estate for foreign keys matches
        runBlockingTest { estateId = mDatabase.estateDao().insertEstate(Estate()) }

        // Set address data for Db test
        address = DaoTestHelper().getAddress(estateId)
    }

    @After
    @Throws(Exception::class)
    fun closeDb() { mDatabase.close() }

    @Test
    @ExperimentalCoroutinesApi
    fun insertAndGetAddress() = runBlockingTest {
        // Given an Address that has been inserted into the DB
        mDatabase.addressDao().insertAddress(address)

        // When getting the Address via the DAO
        val addressDb = mDatabase.addressDao().getAddress(estateId)

        // Then the retrieved Address match the original estate object
        assertEquals(address, addressDb)

        // Clean up coroutines
        cleanupTestCoroutines()
    }

    @Test
    @ExperimentalCoroutinesApi
    fun updateAndGetAddress() = runBlockingTest {
        // Insert an address in database for the test
        mDatabase.addressDao().insertAddress(address)

        // Change some data to update them in database
        address.streetNumber = 222
        address.streetName = "15"
        address.city = "Boston"

        // Given an Address that has been updated into the DB
        val rowAffected = mDatabase.addressDao().updateAddress(address)

        // Check that's there only one row affected when updating
        assertEquals(1, rowAffected)

        // When getting the Address via the DAO
        val addressDb = mDatabase.addressDao().getAddress(address.estateId)

        // Then the retrieved Address match the original address object
        assertEquals(address, addressDb)

        // Clean up coroutines
        cleanupTestCoroutines()
    }

    @Test
    @ExperimentalCoroutinesApi
    fun deleteAddressAndCheckDb() = runBlockingTest {
        // Insert an address in database for the test
        mDatabase.addressDao().insertAddress(address)

        // Given an Address that has been deleted into the DB
        val rowAffected = mDatabase.addressDao().deleteAddress(address.estateId)

        // Check that's there only one row affected when deleting
        assertEquals(1, rowAffected)

        // When getting the Address via the DAO
        val addressDb = mDatabase.addressDao().getAddress(address.estateId)

        // Then the retrieved Address match the original address object
        assertNull(addressDb)

        // Clean up coroutines
        cleanupTestCoroutines()
    }
}