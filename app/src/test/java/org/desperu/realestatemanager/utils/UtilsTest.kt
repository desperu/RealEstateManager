package org.desperu.realestatemanager.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import junit.framework.TestCase.*
import org.desperu.realestatemanager.utils.Utils.convertDollarToEuro
import org.desperu.realestatemanager.utils.Utils.convertEuroToDollar
import org.desperu.realestatemanager.utils.Utils.convertPatternPriceToString
import org.desperu.realestatemanager.utils.Utils.convertPriceToPatternPrice
import org.desperu.realestatemanager.utils.Utils.dateToString
import org.desperu.realestatemanager.utils.Utils.intDateToString
import org.desperu.realestatemanager.utils.Utils.isGooglePlayServicesAvailable
import org.desperu.realestatemanager.utils.Utils.isInternetAvailable
import org.desperu.realestatemanager.utils.Utils.stringToDate
import org.desperu.realestatemanager.utils.Utils.todayDate
import org.junit.Test
import java.text.ParseException
import java.util.*
import kotlin.math.roundToInt

/**
 * Utils class test, to check that all utils functions work as needed.
 */
class UtilsTest {

    private var mockContext = mockk<Context>()

    private lateinit var output: String

    @Test
    fun given_dollar_When_convertDollarToEuro_Then_checkEuroValue() {
        val dollar = 1
        val expected: Int = (dollar * exchangeRate).roundToInt()
        val output = convertDollarToEuro(dollar)

        assertEquals(expected, output)
    }

    @Test
    fun given_euro_When_convertEuroToDollar_Then_checkDollarValue() {
        val euro = 7
        val expected: Int = (euro / exchangeRate).roundToInt()
        val output = convertEuroToDollar(euro)

        assertEquals(expected, output)
    }

    @Test
    fun given_priceString_When_convertPriceToPatternPrice_Then_checkPatternPrice() {
        val price = "12000000"
        val expected = "12,000,000"
        val output = convertPriceToPatternPrice(price, false)

        assertEquals(expected, output)

        val expected2 = "$ 12,000,000"
        val output2 = convertPriceToPatternPrice(price, true)

        assertEquals(expected2, output2)
    }

    @Test
    fun given_patternPrice_When_convertEuroToDollar_Then_checkDollarValue() {
        val patternPrice = "17,000,000"
        val expected = "17000000"
        val output = convertPatternPriceToString(patternPrice)

        assertEquals(expected, output)
    }

    @Test
    fun given_nothing_When_todayDate_Then_checkStringDate() {
        output = todayDate()
        val cal: Calendar = Calendar.getInstance()
        val year: Int = cal.get(Calendar.YEAR)
        val monthOfYear: Int = cal.get(Calendar.MONTH) + 1
        val dayOfMonth: Int = cal.get(Calendar.DAY_OF_MONTH)
        val stringDay: String = if (dayOfMonth < 10) "0$dayOfMonth" else dayOfMonth.toString()
        val stringMonth: String = if (monthOfYear < 10) "0$monthOfYear" else monthOfYear.toString()
        val expected = "$stringDay/$stringMonth/$year"

        assertEquals(expected, output)
    }

    @Test
    fun given_intDateMonthSeptember_When_intDateToString_Then_checkStringDate() {
        val day = 1
        val month = 8
        val year = 2019
        output = intDateToString(day, month, year)
        val expected = "01/09/2019"

        assertEquals(expected, output)
    }

    @Test
    fun given_intDateMonthNovember_When_intDateToString_Then_checkStringDate() {
        val day = 21
        val month = 10
        val year = 2019
        output = intDateToString(day, month, year)
        val expected = "21/11/2019"

        assertEquals(expected, output)
    }

    @Test
    fun given_dateObject_When_askDateToString_Then_checkNewDateFormat() {
        val expected = "15/10/2019"
        val calendar: Calendar = Calendar.getInstance()
        calendar.set(2019, 9, 15)
        val date: Date = calendar.time
        output = dateToString(date)

        assertEquals(expected, output)
    }

    @Test
    @Throws(ParseException::class)
    fun given_stringDate_When_askStringToDate_Then_checkNewDateFormat() {
        val givenDate = "5/9/2019"
        val cal = Calendar.getInstance()
        cal.set(Calendar.MILLISECOND, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.DAY_OF_MONTH, 5)
        cal.set(Calendar.MONTH, 8)
        cal.set(Calendar.YEAR, 2019)
        val expected = Date()
        expected.time = cal.timeInMillis
        val output: Date? = stringToDate(givenDate)

        assertEquals(expected, output)
    }

    @Test
    @Throws(ParseException::class)
    fun given_wrongStringDate_When_askStringToDate_Then_checkNull() {
        val givenDate = "592019"
        val output = stringToDate(givenDate)

        assertNull(output)
    }

    @Test
    @Suppress("DEPRECATION")
    fun given_availableNetwork_When_isInternetAvailableSdk19_20_Then_checkResult() {
        val mockConnectivityManager = mockk<ConnectivityManager>()
        every { mockContext.getSystemService(Context.CONNECTIVITY_SERVICE) } returns mockConnectivityManager

        val mockNetworkInfo = mockk<NetworkInfo>()
        every { mockConnectivityManager.activeNetworkInfo } returns mockNetworkInfo
        every { mockNetworkInfo.isConnected } returns true

        val output = isInternetAvailable(mockContext)

        assertTrue(output)
    }

    @Test
    fun given_availableGoogleServices_When_isGooglePlayServicesAvailable_Then_checkResult() {
        val mockGoogleApiAvailability = mockk<GoogleApiAvailability>()
        mockkStatic(GoogleApiAvailability::class)
        every { GoogleApiAvailability.getInstance() } returns mockGoogleApiAvailability
        every { mockGoogleApiAvailability.isGooglePlayServicesAvailable(mockContext) } returns ConnectionResult.SUCCESS

        val output = isGooglePlayServicesAvailable(mockContext)

        assertTrue(output)
    }

//    @Test // TODO remove if unused
//    fun Given_sectionsArrayList_When_askConcatenateStringSectionsFromArrayList_Then_checkString() {
//        val expected = "news_desk.contains:(\"Politics\" \"Business\" \"Entrepreneurs\")"
//        val sections: ArrayList<String> = ArrayList()
//        sections.add("Politics")
//        sections.add("Business")
//        sections.add("Entrepreneurs")
//        output = MyNewsUtils.concatenateStringSectionsFromArrayList(sections)

//        assertEquals(expected, output)
//    }
//
//    @Test
//    fun Given_emptySectionsArrayList_When_askConcatenateStringSectionsFromArrayList_Then_checkEmptyString() {
//        val expected = ""
//        val sections: ArrayList<String> = ArrayList()
//        output = MyNewsUtils.concatenateStringSectionsFromArrayList(sections)

//        assertEquals(expected, output)
//    }
//
//    @Test
//    fun Given_stringSection_When_askDeConcatenateStringSectionsToArrayList_Then_checkArrayList() {
//        val expected: ArrayList<String> = ArrayList()
//        expected.add("Politics")
//        expected.add("Business")
//        expected.add("Entrepreneurs")
//        expected.add("Arts")
//        val sections = "news_desk.contains:(\"Politics\" \"Business\" \"Entrepreneurs\" \"Arts\")"
//        val output: List<String> = MyNewsUtils.deConcatenateStringSectionToArrayList(sections)

//        assertEquals(expected, output)
//    }
//
//    @Mock
//    var mockContext: Context? = null
//
//    @Mock
//    var mockPrefs: SharedPreferences? = null
//
//    @Test
//    fun Given_articleUrl_When_searchUrlInHistory_Then_returnPosition() {
//        val expected = 0
//        val url: String = MyNewsTools.Constant.nyTimesImageUrl
//        `when`(mockContext.getSharedPreferences(anyString(), anyInt())).thenReturn(mockPrefs)
//        `when`(mockPrefs!!.getString(ARTICLE_READ_URL + 0, "")).thenReturn(url)
//        `when`(mockPrefs!!.getInt(MAX_HISTORY_VALUE, 0)).thenReturn(expected)
//        val output: Int = MyNewsUtils.searchReadArticle(mockContext, url)

//        assertEquals(expected, output)
//    }
//
//    @Test
//    fun Given_articleUrl_When_searchUrlInHistory_Then_returnDefault() {
//        val expected = -1
//        val url: String = MyNewsTools.Constant.nyTimesImageUrl
//        `when`(mockContext.getSharedPreferences(anyString(), anyInt())).thenReturn(mockPrefs)
//        `when`(mockPrefs!!.getString(ARTICLE_READ_URL + 0, "")).thenReturn("test")
//        `when`(mockPrefs!!.getInt(MAX_HISTORY_VALUE, 0)).thenReturn(0)
//        val output: Int = MyNewsUtils.searchReadArticle(mockContext, url)

//        assertEquals(expected, output)
//    }
}