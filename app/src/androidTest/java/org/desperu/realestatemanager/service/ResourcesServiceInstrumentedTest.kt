package org.desperu.realestatemanager.service

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.desperu.realestatemanager.test.R
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ResourcesServiceInstrumentedTest {

    // Context of the app under test.
    private val appContext = InstrumentationRegistry.getInstrumentation().context

    @Test
    fun getString() {
        val expected = "a string for test"

        val output = ResourceServiceImpl(appContext).getString(R.string.string_test)

        assertEquals(expected, output)
    }

    @Test
    fun getStringArray() {
        val expected = arrayOf("item1", "item2")

        val output = ResourceServiceImpl(appContext).getStringArray(R.array.test_list)

        assertArrayEquals(expected, output)
    }
}