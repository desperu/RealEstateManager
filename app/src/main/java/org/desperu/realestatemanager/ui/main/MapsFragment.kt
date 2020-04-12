package org.desperu.realestatemanager.ui.main

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import org.desperu.realestatemanager.R
import org.desperu.realestatemanager.utils.PERM_COARSE_LOCATION
import org.desperu.realestatemanager.utils.PERM_FINE_LOCATION
import org.desperu.realestatemanager.utils.RC_PERM_LOCATION
import pub.devrel.easypermissions.EasyPermissions

class MapsFragment : SupportMapFragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMapLongClickListener, GoogleMap.OnCameraIdleListener {

    // FOR DATA
    private var mapFragment: SupportMapFragment? = null
    private var mMap: GoogleMap? = null

    // FOR LOCATION
    private var isLocationEnabled = false
    private var myLocation: Location? = null

    // --------------
    // BASE METHODS
    // --------------

//    override fun getFragmentLayout(): Int = R.layout.fragment_maps
//
//    override fun configureDesign() {
//        configureMapFragment()
//    }

    // --------------
    // METHODS OVERRIDE
    // --------------

    override fun onMapReady(googleMap: GoogleMap?) {
        super.getMapAsync(this) // TODO ??? needed??
        mMap = googleMap

        // Check location permission, enable MyLocation,
        // and hide google MyLocation button to use custom.
        checkLocationPermissionsStatus()
        mMap?.isMyLocationEnabled = isLocationEnabled
        mMap?.uiSettings?.isMyLocationButtonEnabled = true
        if (isLocationEnabled) updateMapWithLocation(userLocation)

        // Show zoom control, and reposition them.
        mMap?.uiSettings?.isZoomControlsEnabled = true
//        mMap?.uiSettings?.isZoomControlsEnabled = Go4LunchPrefs.getBoolean(context, MAP_ZOOM_BUTTON, ZOOM_BUTTON_DEFAULT)
//        repositionMapButton(GOOGLE_MAP_ZOOM_OUT_BUTTON, resources.getDimension(R.dimen.fragment_maps_zoom_button_margin_bottom).toInt(), resources.getDimension(R.dimen.fragment_maps_zoom_button_margin_end).toInt())

        // Set gestures for google map.
        mMap?.uiSettings?.setAllGesturesEnabled(true)
        mMap?.setOnMapLongClickListener(this)
        mMap?.setOnCameraIdleListener(this)

        // Set onMarkerClick Listener
        mMap?.setOnMarkerClickListener(this)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
        isLocationEnabled = grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
        if (isLocationEnabled) updateMapWithLocation(userLocation)
    }

    override fun onMarkerClick(marker: Marker): Boolean {
//        repositionMapButton(GOOGLE_MAP_TOOLBAR, resources.getDimension(R.dimen.fragment_maps_toolbar_margin_bottom).toInt(), resources.getDimension(R.dimen.fragment_maps_toolbar_margin_end).toInt())
        return marker.snippet != null
    }

    override fun onMapLongClick(latLng: LatLng) {
        mMap?.addMarker(MarkerOptions().position(latLng))
    }

    override fun onCameraIdle() {
        TODO("Not yet implemented")
    }

    // --------------
    // CONFIGURATION
    // --------------

    /**
     * Configure and show map fragment.
     */
    private fun configureMapFragment() {
        if (mapFragment == null) {
            mapFragment = SupportMapFragment()
            mapFragment?.getMapAsync(this)
        }
        childFragmentManager.beginTransaction()
                .add(R.id.map, mapFragment!!)
                .commit()
    }

    /**
     * Check if Coarse Location and Fine Location are granted, if not, ask for them.
     */
    private fun checkLocationPermissionsStatus() {
        if (!EasyPermissions.hasPermissions(context!!, PERM_FINE_LOCATION, PERM_COARSE_LOCATION))
            EasyPermissions.requestPermissions(this, getString(R.string.fragment_maps_popup_title_permission_location),
                RC_PERM_LOCATION, PERM_FINE_LOCATION, PERM_COARSE_LOCATION)
        else isLocationEnabled = true
    }

    // --------------
    // UI
    // --------------

    /**
     * Update map with user location, animate camera to this point.
     * @param userLocation Current user location.
     */
    private fun updateMapWithLocation(userLocation: Location?) {
        if (userLocation != null) {
            mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    LatLng(userLocation.latitude, userLocation.longitude),
//                    Go4LunchPrefs.getInt(context, MAP_ZOOM_LEVEL, ZOOM_LEVEL_DEFAULT)),
                    10F),
                    1500, null)
            myLocation = userLocation
        }
    }

    // --------------
    // LOCATION
    // --------------

    /**
     * Get current location, or last know.
     * @return User location.
     */
    @get:SuppressLint("MissingPermission")
    private val userLocation: Location?
        get() {
            val lm = (context!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager)
            myLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (myLocation == null) {
                val criteria = Criteria()
                criteria.accuracy = Criteria.ACCURACY_COARSE
                val provider = lm.getBestProvider(criteria, true)!!
                myLocation = lm.getLastKnownLocation(provider)
            }
            return myLocation
        }
}