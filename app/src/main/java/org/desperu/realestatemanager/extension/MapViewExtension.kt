package org.desperu.realestatemanager.extension

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import org.desperu.realestatemanager.model.Estate
import org.desperu.realestatemanager.utils.MAP_ZOOM_LEVEL
import org.desperu.realestatemanager.utils.MySharedPreferences
import org.desperu.realestatemanager.utils.Utils.convertPriceToPatternPrice
import org.desperu.realestatemanager.utils.ZOOM_LEVEL_DEFAULT

/**
 * Add a new estate marker on the map.
 * @param estate the given estate for witch add a marker on the map.
 */
internal fun MapView.addMarker(estate: Estate) {
    val estatePosition = LatLng(estate.address.latitude, estate.address.longitude)
    getMapAsync {
        it.addMarker(MarkerOptions()
                .position(estatePosition)
                .title("${estate.type} (${estate.address.city})")
                .snippet(convertPriceToPatternPrice(estate.price.toString(), true)))
                .tag = estate
    }
}

/**
 * Animate camera map view to the latLng position.
 * @param latLng the given latLng to animate to.
 */
internal fun MapView.animateCamera(latLng: LatLng) {
    val zoomLevel = MySharedPreferences.getInt(context, MAP_ZOOM_LEVEL, ZOOM_LEVEL_DEFAULT).toFloat()
    getMapAsync {
        it.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel), 1500, null)
    }
}