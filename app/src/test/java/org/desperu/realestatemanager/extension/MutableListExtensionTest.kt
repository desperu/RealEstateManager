package org.desperu.realestatemanager.extension

import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * MutableListExtension class test, to check that all functions work as needed.
 */
class MutableListExtensionTest {


    @Test
    fun given_mutableList_When_deleteUpTo3_Then_checkResult() {
        val mutableList = mutableListOf("toto", "tata", "tutu", "tete", "titi", "tyty")

        mutableList.deleteUpTo3()

        assertTrue(mutableList.size == 3)
    }

    @Test
    fun given_null_When_deleteUpTo3_Then_checkNull() {
        val mutableList = null

        mutableList.deleteUpTo3()

        assertNull(mutableList)
    }
}