package org.desperu.realestatemanager.utils

import android.content.Context
import android.content.SharedPreferences
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * MySharedPreferences class test, to check that all functions works as needed.
 */
class MySharedPreferencesTest {

    // MOCK CLASSES
    private val mockContext = mockk<Context>()
    private val mockPrefs = mockk<SharedPreferences>()

    // DATA FOR TEST
    private val key = "test"
    private val intTest = 1
    private val stringTest = "A string !"
    private val longTest = System.currentTimeMillis()
    private val booleanTest = true

    @Before
    fun before() {
        every { mockContext.getSharedPreferences(any(), any()) } returns mockPrefs
        every { mockPrefs.getString(key, null) } returns stringTest
        every { mockPrefs.getInt(key, 0) } returns intTest
        every { mockPrefs.getLong(key, 0L) } returns longTest
        every { mockPrefs.getBoolean(key, false) } returns booleanTest
    }

    @Test
    fun given_String_When_getString_Then_checkValue() {
        val output: String? = MySharedPreferences.getString(mockContext, key, null)
        assertEquals(stringTest, output)
    }

    @Test
    fun given_integer_When_getInt_Then_checkValue() {
        val output: Int = MySharedPreferences.getInt(mockContext, key, 0)
        assertEquals(intTest, output)
    }

    @Test
    fun given_Long_When_getLong_Then_checkValue() {
        val output: Long = MySharedPreferences.getLong(mockContext, key, 0)
        assertEquals(longTest, output)
    }

    @Test
    fun given_boolean_When_getBoolean_Then_checkValue() {
        val output: Boolean = MySharedPreferences.getBoolean(mockContext, key, false)
        assertEquals(booleanTest, output)
    }
}