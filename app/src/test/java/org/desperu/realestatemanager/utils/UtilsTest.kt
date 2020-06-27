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
import kotlinx.coroutines.runBlocking
import org.desperu.realestatemanager.utils.Utils.convertDollarToEuro
import org.desperu.realestatemanager.utils.Utils.convertEuroToDollar
import org.desperu.realestatemanager.utils.Utils.convertPatternPriceToString
import org.desperu.realestatemanager.utils.Utils.convertPriceToPatternPrice
import org.desperu.realestatemanager.utils.Utils.creditCalculus
import org.desperu.realestatemanager.utils.Utils.getFolderAndFileNameFromContentUri
import org.desperu.realestatemanager.utils.Utils.intDateToString
import org.desperu.realestatemanager.utils.Utils.isGooglePlayServicesAvailable
import org.desperu.realestatemanager.utils.Utils.isInternetAvailable
import org.desperu.realestatemanager.utils.Utils.realCreditRate
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
        val dollar = 10
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
        output = convertPriceToPatternPrice(price, false)

        assertEquals(expected, output)

        val expected2 = "$ 12,000,000"
        val output2 = convertPriceToPatternPrice(price, true)

        assertEquals(expected2, output2)
    }

    @Test
    fun given_patternPrice_When_convertPatternPriceToString_Then_checkValue() {
        val patternPrice = "17,000,000"
        val expected = "17000000"
        output = convertPatternPriceToString(patternPrice)

        assertEquals(expected, output)
    }

    @Test
    fun given_creditData_When_creditCalculus_Then_monthlyPayment() = runBlocking {
        val monthlyPayment = 2971
        val creditCost = 112488

        val output = creditCalculus(20, 700000.0, 1.7, 100000)

        assertEquals(monthlyPayment, output["monthlyPayment"])
        assertEquals(creditCost, output["creditCost"])
    }

    @Test
    fun given_wrongCreditData_When_creditCalculus_Then_monthlyPayment() = runBlocking {
        val monthlyPayment = 1
        val creditCost = 0

        val output = creditCalculus(10, 20.0, 1.7, 0)

        assertEquals(monthlyPayment, output["monthlyPayment"])
        assertEquals(creditCost, output["creditCost"])
    }

    @Test
    fun given_creditData_When_realCreditRate_Then_realRate() {
        val expected = "18,75"
        output = realCreditRate(600000.0, 112488.0).replace(".", ",")

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
    fun given_imageUri_When_getFolderAndFileNameFromContentUri_Then_checkResult() {
        val contentUri = "content://org.desperu.realestatemanager.fileprovider/EstateImages/Android/data/org.desperu.realestatemanager/files/Pictures/EstateImages/1587561197794.jpg"
        val expected = mapOf(Pair("folderName", FOLDER_NAME), Pair("fileName", "1587561197794.jpg"))

        val output = getFolderAndFileNameFromContentUri(contentUri)

        assertEquals(expected, output)
    }

    @Test
    fun given_wrongUri_When_getFolderAndFileNameFromContentUri_Then_checkResult() {
        val contentUri = "wrong uri"

        val output = getFolderAndFileNameFromContentUri(contentUri).isEmpty()

        assertTrue(output)
    }

    @Test
    fun given_mutableList_When_concatenateStringFromMutableList_Then_checkString() {
        val expected = "School, Shop, Park"

        val interestPlaces = mutableListOf<String>()
        interestPlaces.add("School")
        interestPlaces.add("Shop")
        interestPlaces.add("Park")
        output = Utils.concatenateStringFromMutableList(interestPlaces)

        assertEquals(expected, output)
    }

    @Test
    fun given_emptyMutableList_When_concatenateStringFromMutableList_Then_checkEmptyString() {
        val expected = ""

        val interestPlaces = mutableListOf<String>()
        output = Utils.concatenateStringFromMutableList(interestPlaces)

        assertEquals(expected, output)
    }

    @Test
    fun given_stringPlaces_When_deConcatenateStringToMutableList_Then_checkMutableList() {
        val expected = mutableListOf<String>()
        expected.add("School")
        expected.add("Shop")
        expected.add("Park")

        val interestPlaces = "School, Shop, Park"
        val output: List<String> = Utils.deConcatenateStringToMutableList(interestPlaces)

        assertEquals(expected, output)
    }

    @Test
    @Suppress("deprecation")
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
}