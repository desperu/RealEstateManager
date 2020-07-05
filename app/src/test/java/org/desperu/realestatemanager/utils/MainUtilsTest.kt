package org.desperu.realestatemanager.utils

import android.content.Context
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import io.mockk.every
import io.mockk.mockk
import org.desperu.realestatemanager.R
import org.desperu.realestatemanager.ui.main.fragment.estateDetail.EstateDetailFragment
import org.desperu.realestatemanager.ui.main.fragment.estateList.EstateListFragment
import org.desperu.realestatemanager.ui.main.fragment.estateMap.MapsFragment
import org.desperu.realestatemanager.ui.showImages.fragment.ShowImageFragment
import org.desperu.realestatemanager.utils.MainUtils.getFragClassFromKey
import org.desperu.realestatemanager.utils.MainUtils.getFrame
import org.desperu.realestatemanager.utils.MainUtils.retrievedFragKeyFromClass
import org.desperu.realestatemanager.utils.MainUtils.setTitleActivity
import org.desperu.realestatemanager.utils.MainUtils.switchFrameSizeForTablet
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Main Utils class test, to check that all utils functions work as needed.
 */
class MainUtilsTest {

    private var mockContext = mockk<Context>()

    @Test
    fun given_fragMapAndFrameVisible_When_getFrame_Then_checkResult() {
        val expected = R.id.activity_main_frame_layout2
        val output = getFrame(FRAG_ESTATE_MAP, true)

        assertEquals(expected, output)
    }

    @Test
    fun given_fragListAndFrameVisible_When_getFrame_Then_checkResult() {
        val expected = R.id.activity_main_frame_layout
        val output = getFrame(FRAG_ESTATE_LIST, true)

        assertEquals(expected, output)
    }

    @Test
    fun given_fragDetailAndFrameNotVisible_When_getFrame_Then_checkResult() {
        val expected = R.id.activity_main_frame_layout
        val output = getFrame(FRAG_ESTATE_DETAIL, false)

        assertEquals(expected, output)
    }

    @Test
    fun given_fragDetailAndFrameVisible_When_switchFrameSizeForTablet_Then_checkResult() {
        val mockFrame = mockk<FrameLayout>()
        val mockLinearLayoutParams = LinearLayout.LayoutParams(mockContext, null)

        every { mockFrame.layoutParams } returns mockLinearLayoutParams
        every { mockFrame.layoutParams = any() } returns Unit

        val expected = 1F
        switchFrameSizeForTablet(mockFrame, FRAG_ESTATE_DETAIL, true)
        val output = (mockFrame.layoutParams as LinearLayout.LayoutParams).weight

        assertEquals(expected, output)
    }

    @Test
    fun given_fragMapAndFrameVisible_When_switchFrameSizeForTablet_Then_checkResult() {
        val mockFrame = mockk<FrameLayout>()
        val mockLinearLayoutParams = LinearLayout.LayoutParams(mockContext, null)

        every { mockFrame.layoutParams } returns mockLinearLayoutParams
        every { mockFrame.layoutParams = any() } returns Unit

        val expected = 0F
        switchFrameSizeForTablet(mockFrame, FRAG_ESTATE_MAP, true)
        val output = (mockFrame.layoutParams as LinearLayout.LayoutParams).weight

        assertEquals(expected, output)
    }

    @Test
    fun given_fragDetailAndFrameNotVisible_When_switchFrameSizeForTablet_Then_checkResult() {
        val mockFrame = mockk<FrameLayout>()

        every { mockFrame.layoutParams } returns null
        every { mockFrame.layoutParams = any() } returns Unit

        switchFrameSizeForTablet(mockFrame, FRAG_ESTATE_DETAIL, false)
        val output = (mockFrame.layoutParams as LinearLayout.LayoutParams?)?.weight

        assertNull(output)
    }

    @Test
    fun given_fragListKey_When_getFragClassFromKey_Then_checkResult() {
        val expected = EstateListFragment::class.java
        val output: Class<Fragment> = getFragClassFromKey(FRAG_ESTATE_LIST)

        assertEquals(expected, output)
    }

    @Test
    fun given_fragMapKey_When_getFragClassFromKey_Then_checkResult() {
        val expected = MapsFragment::class.java
        val output: Class<Fragment> = getFragClassFromKey(FRAG_ESTATE_MAP)

        assertEquals(expected, output)
    }

    @Test
    fun given_fragDetailKey_When_getFragClassFromKey_Then_checkResult() {
        val expected = EstateDetailFragment::class.java
        val output: Class<Fragment> = getFragClassFromKey(FRAG_ESTATE_DETAIL)

        assertEquals(expected, output)
    }

    @Test
    fun given_wrongKey_When_getFragClassFromKey_Then_checkError() {
        val fragmentKey = 100

        val expected = "Fragment key not found : $fragmentKey"
        val output = try { getFragClassFromKey<Fragment>(fragmentKey) }
                     catch (e: IllegalArgumentException) { e.message }

        assertEquals(expected, output)
    }

    @Test
    fun given_fragList_When_retrievedFragKeyFromClass_Then_checkResult() {
        val expected = FRAG_ESTATE_LIST
        val output = retrievedFragKeyFromClass(EstateListFragment::class.java)

        assertEquals(expected, output)
    }

    @Test
    fun given_fragMap_When_retrievedFragKeyFromClass_Then_checkResult() {
        val expected = FRAG_ESTATE_MAP
        val output = retrievedFragKeyFromClass(MapsFragment::class.java)

        assertEquals(expected, output)
    }

    @Test
    fun given_fragDetail_When_retrievedFragKeyFromClass_Then_checkResult() {
        val expected = FRAG_ESTATE_DETAIL
        val output = retrievedFragKeyFromClass(EstateDetailFragment::class.java)

        assertEquals(expected, output)
    }

    @Test
    fun given_wrongFragment_When_retrievedFragKeyFromClass_Then_checkError() {
        val fragment = ShowImageFragment::class.java

        val expected = "Fragment class not found : ${fragment.simpleName}"
        val output = try { retrievedFragKeyFromClass(fragment) }
                     catch (e: IllegalArgumentException) { e.message }

        assertEquals(expected, output)
    }

    @Test
    fun given_fragMapAndFrameVisible_When_setTitleActivity_Then_checkResult() {
        val expected = "Estate Map"

        val mockActivity = mockk<AppCompatActivity>()
        every { mockActivity.title = any() } returns Unit
        every { mockActivity.getString(R.string.fragment_maps_name) } returns "Estate Map"
        every { mockActivity.title } returns "Estate Map"

        setTitleActivity(mockActivity, FRAG_ESTATE_MAP, true)

        val output = mockActivity.title

        assertEquals(expected, output)
    }

    @Test
    fun given_fragDetailAndFrameVisible_When_setTitleActivity_Then_checkResult() {
        val expected = "Estate Detail"

        val mockActivity = mockk<AppCompatActivity>()
        every { mockActivity.title = any() } returns Unit
        every { mockActivity.getString(R.string.fragment_estate_detail_name) } returns "Estate Detail"
        every { mockActivity.title } returns "Estate Detail"

        setTitleActivity(mockActivity, FRAG_ESTATE_DETAIL, false)

        val output = mockActivity.title

        assertEquals(expected, output)
    }

    @Test
    fun given_fragListAndFrameVisible_When_setTitleActivity_Then_checkResult() {
        val expected = "Real Estate Manager"

        val mockActivity = mockk<AppCompatActivity>()
        every { mockActivity.title = any() } returns Unit
        every { mockActivity.getString(R.string.app_name) } returns "Real Estate Manager"
        every { mockActivity.title } returns "Real Estate Manager"

        setTitleActivity(mockActivity, FRAG_ESTATE_LIST, true)

        val output = mockActivity.title

        assertEquals(expected, output)
    }
}