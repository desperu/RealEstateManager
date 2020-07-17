package org.desperu.realestatemanager.ui.main.fragment.estateMap

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.fragment_maps.*
import org.desperu.realestatemanager.R
import org.desperu.realestatemanager.base.BaseBindingFragment
import org.desperu.realestatemanager.databinding.FragmentMapsBinding
import org.desperu.realestatemanager.di.ViewModelFactory
import org.desperu.realestatemanager.extension.animateCamera
import org.desperu.realestatemanager.model.Estate
import org.desperu.realestatemanager.utils.*
import org.desperu.realestatemanager.utils.Utils.isGooglePlayServicesAvailable
import org.desperu.realestatemanager.animation.MapMotionLayout
import org.desperu.realestatemanager.utils.Utils.isInternetAvailable
import org.desperu.realestatemanager.utils.Utils.isLocationEnabled
import pub.devrel.easypermissions.EasyPermissions

/**
 * Argument name for bundle, to save map view state.
 */
const val MAP_VIEW_BUNDLE_KEY: String = "MapViewBundleKey"

/**
 * Argument name for bundle, to received estate in this fragment.
 */
const val ESTATE_MAP: String = "estateMap"

/**
 * Argument name for bundle, to received estate list in this fragment.
 */
const val ESTATE_LIST_MAP: String = "estateListMap"

/**
 * Argument name for bundle, to received the map mode to set.
 */
const val MAP_MODE: String = "mapMode"

/**
 * Fragment to show Google maps, with markers for estates in the map.
 *
 * @constructor Instantiates a new MapsFragment.
 */
class MapsFragment : BaseBindingFragment() {

    // FOR BUNDLE
    private val estate: Estate? get() = arguments?.getParcelable(ESTATE_MAP)
    private val estateList: List<Estate>? get() = arguments?.getParcelableArrayList(ESTATE_LIST_MAP)
    private val mapMode: Int? get() = arguments?.getInt(MAP_MODE)

    // FOR DATA
    private lateinit var binding: FragmentMapsBinding
    private var viewModel: MapsViewModel? = null
    private var mGoogleMap: GoogleMap? = null
    private var mMapView: MapView? = null
    private var mapViewBundle: Bundle? = null

    // FOR LOCATION
    private var isLocationEnabled = false

    // --------------
    // BASE METHODS
    // --------------

    override fun getBindingView(): View = configureViewModel()

    override fun configureDesign() {}

    override fun updateDesign() {
        checkDependencies()
    }

    // --------------
    // METHODS OVERRIDE
    // --------------

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // Forward results to EasyPermissions.
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
        isLocationEnabled = grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null)
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY)
        if (mapViewBundle == null) {
            mapViewBundle = Bundle()
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle)
        }
        mMapView?.onSaveInstanceState(mapViewBundle)
    }

    // Forward Fragment lifecycle with MapView lifecycle.
    override fun onStart() { super.onStart(); mMapView?.onStart() }

    override fun onResume() { super.onResume(); mMapView?.onResume() }

    override fun onPause() { mMapView?.onPause(); super.onPause() }

    override fun onStop() { mMapView?.onStop(); super.onStop() }

    override fun onDestroy() { mMapView?.onDestroy(); super.onDestroy() }

    override fun onLowMemory() { super.onLowMemory(); mMapView?.onLowMemory() }

    // --------------
    // CONFIGURATION
    // --------------

    /**
     * Configure data binding and view model.
     */
    private fun configureViewModel(): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_maps, container, false)
        viewModel = ViewModelProvider(this, ViewModelFactory(activity as AppCompatActivity)).get(MapsViewModel::class.java)

        binding.viewModel = viewModel
        return binding.root
    }

    /**
     * Check that the needed dependencies are available.
     */
    private fun checkDependencies() = if (!isGooglePlayServicesAvailable(context!!))
        // If Google Play Services aren't available, hide map view and show message.
        showWarningMessage(getString(R.string.fragment_maps_text_no_google_play_services_available))
        // Else start map view.
        else configureMapView()

    /**
     * Configure Map View.
     */
    private fun configureMapView() {
        mMapView = fragment_maps_maps_view
        mMapView?.onCreate(mapViewBundle ?: Bundle())
        mMapView?.getMapAsync { mGoogleMap = it; configureMapMode() }
    }

    /**
     * Configure Map view with the good mode, little mode or full mode.
     */
    private fun configureMapMode() {
        if (mapMode == FULL_MODE) { // Full mode
            if (checkConnectivity()) {
                estateList?.let { viewModel?.setEstateList(it) }
                configureMapLocation()
                configureMapZoomButton()
                fragment_maps_fullscreen_button.visibility = View.GONE
            }
        } else { // Little mode
            estate?.let { viewModel?.setEstate(it) }
            fragment_maps_floating_button_location.visibility = View.GONE
        }
        configureMapGestureAndListener()
    }

    /**
     * Check connectivity status (internet and location).
     * @return true if connectivity are available, false otherwise.
     */
    private fun checkConnectivity(): Boolean = when {
        // If there's no internet connexion available, hide map view and show message.
        !isInternetAvailable(context!!) -> {
            showWarningMessage(getString(R.string.fragment_maps_text_no_internet_connexion))
            false
        }
        // If there's no location enabled, hide map view and show message.
        !isLocationEnabled(context!!) -> {
            showWarningMessage(getString(R.string.fragment_maps_text_no_location_enabled))
            false
        }
        // Else show full map mode.
        else -> true
    }

    /**
     * Check location permission, enable MyLocation, and hide google MyLocation button to use custom.
     */
    @SuppressLint("missingPermission")
    private fun configureMapLocation() {
        checkLocationPermissionsStatus()
        mGoogleMap?.isMyLocationEnabled = isLocationEnabled
        mGoogleMap?.uiSettings?.isMyLocationButtonEnabled = false
        fragment_maps_floating_button_location.setOnClickListener(onClickMyLocation)
        if (isLocationEnabled) updateMapWithLocation(userLocation)
    }

    /**
     * Configure map zoom control, and reposition them.
     */
    private fun configureMapZoomButton() {
        mGoogleMap?.uiSettings?.isZoomControlsEnabled = MySharedPreferences.getBoolean(context!!, MAP_ZOOM_BUTTON, ZOOM_BUTTON_DEFAULT)
        repositionMapZoom()
    }

    /**
     * Configure map gesture and listeners.
     */
    private fun configureMapGestureAndListener() = mGoogleMap?.apply {
        uiSettings?.setAllGesturesEnabled(true)
        setOnMarkerClickListener(::onMarkerClick)
        setOnInfoWindowClickListener(::onInfoClick)
        setOnMapLongClickListener(::onMapLongClick)
        setOnCameraIdleListener(::onCameraIdle)
        fragment_maps_fullscreen_button.setOnClickListener(onClickFullScreen)
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
    // ACTION
    // --------------

    /**
     * On click listener for my location button, set isMyLocationEnabled to prevent mistakes.
     * If location is enabled, update map with location, else ask for location permissions.
     */
    @SuppressLint("missingPermission")
    private val onClickMyLocation = View.OnClickListener {
        mGoogleMap?.isMyLocationEnabled = isLocationEnabled
        if (isLocationEnabled) updateMapWithLocation(userLocation)
        else checkLocationPermissionsStatus()
    }

    /**
     * On click listener for switch map size (little size and full screen), use motion layout to perform animation.
     */
    private val onClickFullScreen = View.OnClickListener { MapMotionLayout(context!!, view).switchMapSize() }

    /**
     * On marker click, show marker data.
     * @param marker the clicked marker.
     */
    @Suppress("unused_parameter")
    private fun onMarkerClick(marker: Marker): Boolean {
        repositionMapToolbar()
        return false
    }

    /**
     * On info window click, redirect user to estate detail fragment.
     * @param marker the clicked info window marker's.
     */
    private fun onInfoClick(marker: Marker) {
        viewModel?.onInfoClick(marker.tag as Estate?)
    }

    /**
     * On map long click, this add a marker at the long click position.
     * @param latLng the LatLng corresponding with the long click position.
     */
    private fun onMapLongClick(latLng: LatLng) { mGoogleMap?.addMarker(MarkerOptions().position(latLng)) }

    /**
     * On camera idle (camera move), refresh marker estates on map, only when show estate list on map.
     */
    private fun onCameraIdle() { if (mapMode == FULL_MODE) viewModel?.updateEstateList(null) }

    /**
     * Update estate.
     * @param estate the estate to set.
     */
    internal fun updateEstate(estate: Estate) {
        mGoogleMap?.clear()
        viewModel?.updateEstate(estate)
    }

    /**
     * Update estate list.
     * @param estateList the estate list to set.
     */
    internal fun updateEstateList(estateList: List<Estate>) {
        mGoogleMap?.clear()
        viewModel?.updateEstateList(estateList)
    }

    // --------------
    // UI
    // --------------

    /**
     * Update map with user location, animate camera to this point.
     * @param userLocation the user location to animate map to.
     */
    private fun updateMapWithLocation(userLocation: Location?) {
        userLocation?.let { mMapView?.animateCamera(LatLng(it.latitude, it.longitude)) }
    }

    /**
     * Reposition MapToolbar (toolbar shown when click on a marker), it's needed to correct position
     * due to custom my location button.
     */
    private fun repositionMapToolbar() {
        if (mMapView != null && mMapView?.findViewWithTag<View>(GOOGLE_MAP_TOOLBAR) != null) {
            // Get the toolbar view
            val mapToolbar: View? = mMapView?.findViewWithTag(GOOGLE_MAP_TOOLBAR)
            // position to the left of custom My Location button
            mapToolbar?.updateLayoutParams<RelativeLayout.LayoutParams> {
                setMargins(0, 0, 0, resources.getDimension(R.dimen.fragment_maps_toolbar_margin_bottom).toInt())
                marginEnd = resources.getDimension(R.dimen.fragment_maps_toolbar_margin_end).toInt()
            }
            mapToolbar?.bottom = resources.getDimension(R.dimen.fragment_maps_toolbar_margin_bottom).toInt()
        }
        supportLocationButtonKitkat()
    }

    /**
     * Reposition MapZoom, it's needed to correct position due to custom my location button.
     */
    private fun repositionMapZoom() {
        if (mMapView != null && mMapView?.findViewWithTag<View>(GOOGLE_MAP_ZOOM_OUT_BUTTON) != null) {
            // Get the zoom button view
            val mapZoom: View? = mMapView?.findViewWithTag(GOOGLE_MAP_ZOOM_OUT_BUTTON)
            // position to the top of custom My Location button
            mapZoom?.updateLayoutParams<LinearLayout.LayoutParams> {
                setMargins(0, 0, resources.getDimension(R.dimen.fragment_maps_zoom_button_margin_end).toInt(), resources.getDimension(R.dimen.fragment_maps_zoom_button_margin_bottom).toInt())
            }
        }
        supportLocationButtonKitkat()
    }

    /**
     * Correct my location button position for KITKAT.
     */
    private fun supportLocationButtonKitkat() {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            fragment_maps_floating_button_location.updateLayoutParams<ConstraintLayout.LayoutParams> {
                setMargins(0, 0, resources.getDimension(R.dimen.fragment_maps_floating_button_location_margin_kitkat).toInt(), resources.getDimension(R.dimen.fragment_maps_floating_button_location_margin_kitkat).toInt())
            }
        }
    }

    /**
     * Show warning message when there's no internet connexion or google play services on the device.
     * @param message the warning message to show.
     */
    private fun showWarningMessage(message: String) {
        fragment_maps_text_warning.visibility = View.VISIBLE
        fragment_maps_text_warning.text = message
        fragment_maps_maps_view.visibility = View.GONE
        fragment_maps_fullscreen_button.visibility = View.GONE
        fragment_maps_floating_button_location.visibility = View.GONE
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
            var myLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (myLocation == null) {
                val criteria = Criteria()
                criteria.accuracy = Criteria.ACCURACY_COARSE
                val provider = lm.getBestProvider(criteria, true)
                provider?.let { myLocation = lm.getLastKnownLocation(it) }
            }
            return myLocation
        }
}