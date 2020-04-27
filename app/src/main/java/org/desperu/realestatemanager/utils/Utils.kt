package org.desperu.realestatemanager.utils

import android.content.Context
import android.location.Geocoder
import android.net.ConnectivityManager
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import org.desperu.realestatemanager.model.Address
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt


/**
 * Utils object witch provide utils functions for this application.
 */
internal object Utils {

    // -----------------
    // CONVERT MONEY
    // -----------------

    /**
     * Conversion d'un prix d'un bien immobilier (Dollars vers Euros)
     * NOTE : NE PAS SUPPRIMER, A MONTRER DURANT LA SOUTENANCE
     * @param dollars Dollar value to convert.
     * @return Converted value in euro.
     */
    internal fun convertDollarToEuro(dollars: Int): Int = (dollars * exchangeRate).roundToInt()

    /**
     * Convert euro to dollar.
     * @param euro Euro value to convert.
     * @return Converted dollar value.
     */
    internal fun convertEuroToDollar(euro: Int): Int = (euro / exchangeRate).roundToInt()

    // -----------------
    // CONVERT PRICE
    // -----------------

    // Marker to prevent mistakes
    private var isEditing = false

    /**
     * Convert simple string price (12000000) to pattern string price (12,000,000) or ($ 12,000,000).
     * @param s Simple string price to convert.
     * @param moneyUnity With or without money unity ($).
     * @return Pattern string price.
     */
    internal fun convertPriceToPatternPrice(s: String, moneyUnity: Boolean): String {
        if (isEditing) return s
        if (s.isBlank() || s == "0") return ""

        isEditing = true

        val s1: Double = s.replace(Regex.fromLiteral(","), "").toDouble()

        val pattern = if (moneyUnity) "$ ###,###,###" else "###,###,###"
        val nf2: NumberFormat = NumberFormat.getInstance(Locale.ENGLISH)
        (nf2 as DecimalFormat).applyPattern(pattern)

        isEditing = false
        return nf2.format(s1)
    }

    /**
     * Convert string pattern price to simple string price.
     * @param str String pattern price to convert.
     * @return Converted string price.
     */
    internal fun convertPatternPriceToString(str: String): String = str.replace(",","", false)

    // -----------------
    // CONVERT DATE
    // -----------------

    /**
     * Conversion de la date d'aujourd'hui en un format plus approprié
     * NOTE : NE PAS SUPPRIMER, A MONTRER DURANT LA SOUTENANCE
     * @return Today date, string format.
     */
    internal fun todayDate(): String = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

    /**
     * Concatenate date from int to string.
     * @param day Selected day.
     * @param month Selected month.
     * @param year Selected year.
     * @return String date.
     */
    internal fun intDateToString(day: Int, month: Int, year: Int): String {
        var month1 = month
        month1 += 1
        val stringDay: String = if (day < 10) "0$day" else day.toString()
        val stringMonth: String = if (month1 < 10) "0$month1" else month1.toString()
        return "$stringDay/$stringMonth/$year"
    }

    // TODO not used
    /**
     * Convert date object to string format "dd/MM/yyyy".
     * @param givenDate Given date object.
     * @return String date with good format.
     */
    internal fun dateToString(givenDate: Date): String = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(givenDate)

    /**
     * Convert string date format from "dd/MM/yyyy" to Date, return null if an error happened.
     * @param givenDate The given date.
     * @return Date object, null if an error happened.
     */
    internal fun stringToDate(givenDate: String): Date? {
        val givenDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        var date: Date? = null
        try {
            date = givenDateFormat.parse(givenDate)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return date
    }

    // -----------------
    // CONVERT STRING
    // -----------------

    /**
     * Get the folder and file name from a content uri.
     * @param stringUri the string content uri.
     * @return the map list containing the folder name and the file name.
     */
    internal fun getFolderAndFileNameFromContentUri(stringUri: String): Map<String, String> {
        val tempList = stringUri.split("/")
        val listSize = tempList.size
        return mapOf(Pair("folderName", tempList[listSize - 2]), Pair("fileName", tempList[listSize - 1]))
    }


    // -----------------
    // CONVERT ADDRESS
    // -----------------

    /**
     * Get latitude and longitude from address.
     * @param context the context from this function is called.
     * @param address the address to convert.
     * @return the latitude and longitude in a list, an empty list otherwise.
     */
    internal fun getLatLngFromAddress(context: Context, address: Address): List<Double> {
        val geocoder = Geocoder(context, Locale.getDefault())
        val result = geocoder.getFromLocationName("${address.streetNumber}, ${address.streetName} ${address.postalCode} ${address.city} ${address.country}", 1)
        return if (!result.isNullOrEmpty()) listOf(result[0].latitude, result[0].longitude)
               else emptyList()
    }

    // -----------------
    // WEB CONNECTION
    // -----------------

    /**
     * Vérification de la connexion réseau
     * NOTE : NE PAS SUPPRIMER, A MONTRER DURANT LA SOUTENANCE
     * @param context Context from this function is called.
     * @return true if internet is connected.
     */
    @Suppress("DEPRECATION")
    internal fun isInternetAvailable(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        return netInfo != null && netInfo.isConnected
    }

    // -----------------
    // GOOGLE PLAY SERVICE
    // -----------------

    /**
     * Check that google play services are available.
     * @param context the context from this function is called.
     * @return true if the google play services are available.
     */
    internal fun isGooglePlayServicesAvailable(context: Context): Boolean {
        val googleApiAvailability = GoogleApiAvailability.getInstance()
        val resultCode = googleApiAvailability.isGooglePlayServicesAvailable(context)
        return resultCode == ConnectionResult.SUCCESS
    }
}