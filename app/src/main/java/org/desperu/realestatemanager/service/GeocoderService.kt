package org.desperu.realestatemanager.service

import android.content.Context
import android.location.Geocoder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.desperu.realestatemanager.model.Address
import org.desperu.realestatemanager.utils.Utils.isInternetAvailable
import java.lang.Exception
import java.util.*

/**
 * Service for being able to request Geocoder service.
 */
interface GeocoderService {

    /**
     * Used to allow view model to retrieved latitude and longitude from an address.
     *
     * @param address the given address from witch retrieved latitude and longitude.
     *
     * @return the latitude and longitude of the address in a list, or empty list otherwise.
     */
    suspend fun getLatLngFromAddress(address: Address): List<Double>
}

/**
 * Implementation of the Geocoder Service which uses a Context instance to request Geocoder Api.
 *
 * @property context The Context instance used to request the Geocoder Api.
 *
 * @constructor Instantiates a new GeocoderServiceImpl.
 *
 * @param context The Context instance used to request the Geocoder Api to set.
 */
class GeocoderServiceImpl(private val context: Context) : GeocoderService {

    /**
     * Used to allow view model to retrieved latitude and longitude from an address.
     *
     * @param address the given address from witch retrieved latitude and longitude.
     *
     * @return the latitude and longitude of the address in a list, or empty list otherwise.
     */
    override suspend fun getLatLngFromAddress(address: Address): List<Double> = withContext(Dispatchers.IO) {
        var latLng = emptyList<Double>()
// TODO not work dispatcher ??
        if (isInternetAvailable(context)) {
            val geocoder = Geocoder(context, Locale.getDefault())
            try {
                val result = geocoder.getFromLocationName("${address.streetNumber} ${address.streetName}, ${address.postalCode} ${address.city}, ${address.country}", 1)
                if (!result.isNullOrEmpty())
                    latLng = listOf(result[0].latitude, result[0].longitude)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return@withContext latLng
    }
}