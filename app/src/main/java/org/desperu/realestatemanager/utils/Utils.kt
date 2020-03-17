package org.desperu.realestatemanager.utils

import android.content.Context
import android.net.ConnectivityManager
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

/**
 * Created by Philippe on 21/02/2018.
 */
object Utils {

    // -----------------
    // CONVERT MONEY
    // -----------------

    /**
     * Conversion d'un prix d'un bien immobilier (Dollars vers Euros)
     * NOTE : NE PAS SUPPRIMER, A MONTRER DURANT LA SOUTENANCE
     * @param dollars Dollar value to convert.
     * @return Converted value in euro.
     */
    fun convertDollarToEuro(dollars: Int): Int = (dollars * exchangeRate).roundToInt()

    /**
     * Convert euro to dollar.
     * @param euro Euro value to convert.
     * @return Converted dollar value.
     */
    fun convertEuroToDollar(euro: Int): Int = (euro / exchangeRate).roundToInt()

    // -----------------
    // CONVERT DATE
    // -----------------

    /**
     * Conversion de la date d'aujourd'hui en un format plus approprié
     * NOTE : NE PAS SUPPRIMER, A MONTRER DURANT LA SOUTENANCE
     * @return Today date, string format.
     */
    fun todayDate(): String = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

    /**
     * Concatenate date from int to string.
     * @param day Selected day.
     * @param month Selected month.
     * @param year Selected year.
     * @return String date.
     */
    fun intDateToString(day: Int, month: Int, year: Int): String {
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
    fun dateToString(givenDate: Date): String = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(givenDate)

    /**
     * Convert string date format from "dd/MM/yyyy" to Date.
     * @param givenDate The given date.
     * @return Date object.
     */
    fun stringToDate(givenDate: String): Date {
        val givenDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        var date = Date()
        try {
            date = givenDateFormat.parse(givenDate)!!
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return date
    }

    // -----------------
    // WEB CONNECTION
    // -----------------

    /**
     * Vérification de la connexion réseau
     * NOTE : NE PAS SUPPRIMER, A MONTRER DURANT LA SOUTENANCE
     * @param context Context from this function is called.
     * @return If internet connected.
     */
    fun isInternetAvailable(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        return netInfo != null && netInfo.isConnected
    }
}