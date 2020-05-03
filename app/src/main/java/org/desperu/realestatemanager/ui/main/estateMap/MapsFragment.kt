package org.desperu.realestatemanager.ui.main.estateMap

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
import org.desperu.realestatemanager.ui.main.MainActivity
import org.desperu.realestatemanager.ui.main.estateDetail.ShareViewModel
import org.desperu.realestatemanager.ui.main.estateList.EstateListViewModel
import org.desperu.realestatemanager.ui.main.estateList.EstateViewModel
import org.desperu.realestatemanager.utils.*
import org.desperu.realestatemanager.utils.Utils.isGooglePlayServicesAvailable
import pub.devrel.easypermissions.EasyPermissions

/**
 * Argument name for bundle, to save map key when recreate fragment.
 */
const val MAP_VIEW_BUNDLE_KEY: String = "MapViewBundleKey"

/**
 * Fragment to show Google maps, with markers for estates in the map.
 * @constructor Instantiates a new MapsFragment.
 */
class MapsFragment : BaseBindingFragment() {

    // FOR DATA
    private lateinit var binding: FragmentMapsBinding
    private var viewModel: EstateViewModel? = null
    private var viewModel2: EstateListViewModel? = null
    private var mMap: GoogleMap? = null
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
        configureMapView()
    }

    // --------------
    // METHODS OVERRIDE
    // --------------

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
        isLocationEnabled = grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
    }

    override fun onAttach(context: Context) {
        if (isGooglePlayServicesAvailable(context))
            super.onAttach(context)
        else { // If Google Play Services aren't available, hide map view and show message
            fragment_maps_text_no_google_services.visibility = View.VISIBLE
            fragment_maps_maps_view.visibility = View.GONE
        }
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

        viewModel = (parentFragment as? ShareViewModel)?.getViewModel()
        binding.viewModel = viewModel

        if (viewModel == null) {
            viewModel2 = ViewModelProvider(this, ViewModelFactory(requireActivity() as MainActivity)).get(EstateListViewModel::class.java)
            binding.viewModel2 = viewModel2
        }

        return binding.root
    }

    /**
     * Configure Map View.
     */
    private fun configureMapView() {
        mMapView = fragment_maps_maps_view
        mMapView?.onCreate(mapViewBundle)
        mMapView?.getMapAsync { mMap = it; configureAskedMap() }
    }

    /**
     * Configure Map view with the good mode.
     */
    private fun configureAskedMap() {
        if (viewModel == null) {
            configureMapLocation()
            configureMapZoomButton()
        } else
            fragment_maps_floating_button_location.visibility = View.GONE
        configureMapGestureAndListener()
    }

    /**
     * Check location permission, enable MyLocation, and hide google MyLocation button to use custom.
     */
    private fun configureMapLocation() {
        checkLocationPermissionsStatus()
        mMap?.isMyLocationEnabled = isLocationEnabled
        mMap?.uiSettings?.isMyLocationButtonEnabled = false
        fragment_maps_floating_button_location.setOnClickListener(onClickMyLocation)
        if (isLocationEnabled) updateMapWithLocation(userLocation)
    }

    /**
     * Configure map zoom control, and reposition them.
     */
    private fun configureMapZoomButton() {
        mMap?.uiSettings?.isZoomControlsEnabled = true
//        mMap?.uiSettings?.isZoomControlsEnabled = Go4LunchPrefs.getBoolean(context, MAP_ZOOM_BUTTON, ZOOM_BUTTON_DEFAULT)
        repositionMapZoom()
    }

    /**
     * Configure map gesture and listeners.
     */
    private fun configureMapGestureAndListener() = mMap?.apply {
        uiSettings?.setAllGesturesEnabled(true)
        setOnMarkerClickListener(::onMarkerClick)
        setOnInfoWindowClickListener(::onInfoClick)
        setOnMapLongClickListener(::onMapLongClick)
        setOnCameraIdleListener(::onCameraIdle)
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
    private val onClickMyLocation = View.OnClickListener {
        mMap?.isMyLocationEnabled = isLocationEnabled
        if (isLocationEnabled) updateMapWithLocation(userLocation)
        else checkLocationPermissionsStatus()
    }

    /**
     * On marker click, show marker data.
     * @param marker the clicked marker.
     */
    private fun onMarkerClick(marker: Marker): Boolean {
        repositionMapToolbar()
//        return marker.snippet != null
        return false
    }

    /**
     * On info window click, redirect user to estate detail fragment.
     * @param marker the clicked info window marker's.
     */
    private fun onInfoClick(marker: Marker) {
        viewModel2?.onInfoClick(marker.tag as Estate?)
    }

    /**
     * On map long click, this add a marker at the long click position.
     * @param latLng the LatLng corresponding with the long click position.
     */
    private fun onMapLongClick(latLng: LatLng) { mMap?.addMarker(MarkerOptions().position(latLng)) }

    /**
     * On camera idle (camera move), update marker estates on map, only when show estate list on map.
     */
    private fun onCameraIdle() { viewModel2?.updateEstateList() }

    /**
     * Method called when query text change.
     * @param query Query term.
     */
//    fun onSearchQueryTextChange(query: String) {
//        this.queryTerm = query
//        this.startNewRequest(query)
//    }

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
//
//    /**
//     * Add a new marker for an estate on the map.
//     * @param estate the estate to set on the map
//     */
//    internal fun addMarker(estate: Estate) {
//        val estateLatLng = LatLng(estate.address.latitude, estate.address.longitude)
//        mMap?.addMarker(MarkerOptions()
//                .position(estateLatLng)
//                .title("${estate.type} ${estate.address.city}")
////                .icon(this.switchMarkerColors(this.isBookedRestaurant(restaurant)))
//                .snippet(convertPriceToPatternPrice(estate.price.toString(), true))
//        )
//        updateMapWithLocation(estateLatLng)
//    }

    /**
     * Reposition MapToolbar (toolbar shown when click on a marker), it's needed to correct position
     * due to custom my location button.
     */
    private fun repositionMapToolbar() {
        if (mMapView != null && mMapView?.findViewWithTag<View>(GOOGLE_MAP_TOOLBAR) != null) {
            // Get the toolbar view
            val button: View = mMapView?.findViewWithTag(GOOGLE_MAP_TOOLBAR)!!
            val layoutParams = button.layoutParams as RelativeLayout.LayoutParams
            // position to the left of custom My Location button
            layoutParams.setMargins(0, 0, 0, resources.getDimension(R.dimen.fragment_maps_toolbar_margin_bottom).toInt())
            layoutParams.marginEnd = resources.getDimension(R.dimen.fragment_maps_toolbar_margin_end).toInt()
            button.bottom = resources.getDimension(R.dimen.fragment_maps_toolbar_margin_bottom).toInt()
        }
        supportLocationButtonKitkat()
    }

    /**
     * Reposition MapZoom, it's needed to correct position due to custom my location button.
     */
    private fun repositionMapZoom() {
        if (mMapView != null && mMapView?.findViewWithTag<View>(GOOGLE_MAP_ZOOM_OUT_BUTTON) != null) {
            // Get the zoom button view
            val button: View = mMapView?.findViewWithTag(GOOGLE_MAP_ZOOM_OUT_BUTTON)!!
            val layoutParams = button.layoutParams as LinearLayout.LayoutParams
            // position to the top of custom My Location button
            layoutParams.setMargins(0, 0, resources.getDimension(R.dimen.fragment_maps_zoom_button_margin_end).toInt(), resources.getDimension(R.dimen.fragment_maps_zoom_button_margin_bottom).toInt())
        }
        supportLocationButtonKitkat()
    }

    /**
     * Correct my location button position for KITKAT.
     */
    private fun supportLocationButtonKitkat() {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            val layoutParams = fragment_maps_floating_button_location.layoutParams as RelativeLayout.LayoutParams
            layoutParams.setMargins(0, 0, resources.getDimension(R.dimen.fragment_maps_floating_button_location_margin_kitkat).toInt(), resources.getDimension(R.dimen.fragment_maps_floating_button_location_margin_kitkat).toInt())
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
            var myLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (myLocation == null) {
                val criteria = Criteria()
                criteria.accuracy = Criteria.ACCURACY_COARSE
                val provider = lm.getBestProvider(criteria, true)
                provider?.let { myLocation = lm.getLastKnownLocation(it) }
            }
            return myLocation
        }

    /**
     * Get screen global location.
     * @return LatLng bounds for current screen.
     */
    private val getLatLngBounds = mMap?.projection?.visibleRegion?.latLngBounds
}