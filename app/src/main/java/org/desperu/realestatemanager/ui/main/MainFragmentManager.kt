package org.desperu.realestatemanager.ui.main

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.activity_main.*
import org.desperu.realestatemanager.R
import org.desperu.realestatemanager.filter.ManageFiltersHelper
import org.desperu.realestatemanager.model.Estate
import org.desperu.realestatemanager.ui.main.estateDetail.ESTATE_DETAIL
import org.desperu.realestatemanager.ui.main.estateDetail.EstateDetailFragment
import org.desperu.realestatemanager.ui.main.estateList.ESTATE_NOTIFICATION_FOR_LIST
import org.desperu.realestatemanager.ui.main.estateList.EstateListFragment
import org.desperu.realestatemanager.ui.main.estateMap.ESTATE_LIST_MAP
import org.desperu.realestatemanager.ui.main.estateMap.MAP_MODE
import org.desperu.realestatemanager.ui.main.estateMap.MapsFragment
import org.desperu.realestatemanager.ui.main.filter.FilterFragment
import org.desperu.realestatemanager.utils.*
import org.desperu.realestatemanager.utils.MainUtils.getFragClassFromKey
import org.desperu.realestatemanager.utils.MainUtils.getFrame
import org.desperu.realestatemanager.utils.MainUtils.retrievedFragKeyFromClass
import org.desperu.realestatemanager.utils.MainUtils.setTitleActivity
import org.desperu.realestatemanager.utils.MainUtils.switchFrameSizeForTablet
import org.koin.core.KoinComponent
import org.koin.core.get

/**
 * Main Fragment Manager class witch manage all fragments for main activity.
 *
 * @param activity the activity instance from this class is called.
 * @param communication the main communication interface that allow communication with activity.
 *
 * @constructor Instantiates a new MainFragmentManager.
 *
 * @property activity the activity instance from this class is called to set.
 * @property communication the main communication interface that allow communication with activity to set.
 */
class MainFragmentManager(private val activity: AppCompatActivity,
                          private val communication: MainCommunication
): KoinComponent {

    // FOR DATA
    private val fragmentKey get() = communication.getFragmentKey()
    private val fm = activity.supportFragmentManager
    private val frameLayout by lazy { activity.activity_main_frame_layout }
    private val isFrame2Visible = communication.isFrame2Visible
    private var fabFilter = activity.activity_main_fab_filter
    private lateinit var bottomSheet: BottomSheetBehavior<View>

    // --------------
    // FRAGMENT MANAGEMENT
    // --------------

    /**
     * Configure and show fragments, with back stack management to restore instance.
     * @param fragmentKey the fragment key to show corresponding fragment.
     * @param estate the estate to show in estate detail or maps.
     */
    internal fun configureAndShowFragment(fragmentKey: Int, estate: Estate?) {
        if (this.fragmentKey != fragmentKey || estate != null) {
            communication.setFragmentKey(fragmentKey)

            // Get the fragment class from the fragment key
            val fragmentClass: Class<Fragment> = getFragClassFromKey(fragmentKey)

            // Restore instance from back stack if there's one,
            // else create a new instance for asked fragment.
            val fragment = fm.findFragmentByTag(fragmentClass.simpleName) ?: fragmentClass.newInstance()

            // Populate data to fragment with bundle.
            populateDataToFragment(fragment, estate)

            // Clear all back stack when recall Estate List Fragment,
            // because it's the root fragment of this activity.
            if (fragmentKey == FRAG_ESTATE_LIST) clearAllBackStack()

            // Set the corresponding activity title.
            setTitleActivity(activity, fragmentKey, isFrame2Visible)

            // If the device is a tablet and asked fragment is maps, collapse list frame,
            // else set the list frame to original size.
            switchFrameSizeForTablet(frameLayout, fragmentKey, isFrame2Visible)

            // Adapt fab filter position or hide, depend of the asked fragment.
            fabFilter.adaptFabFilter(fragmentKey, isFrame2Visible)

            // Apply the fragment transaction in the corresponding frame.
            fragmentTransaction(fragment, getFrame(fragmentKey, isFrame2Visible))
        }
    }

    /**
     * Populate data to fragment with bundle.
     * @param fragment the fragment instance to send data.
     * @param estate the to send to fragment.
     */
    private fun populateDataToFragment(fragment: Fragment, estate: Estate?) {
        // Populate estate to fragment with bundle if there's one.
        if (estate != null) {
            // Try to update estate detail data if there is an instance of fragment.
            estateDetailFragment?.updateEstate(estate)
            populateEstateToFragment(fragment, estate)
        }
        // Populate estate list to maps fragment with bundle, and set map mode.
        if (fragmentKey == FRAG_ESTATE_MAP) setMapsFragmentBundle(fragment)
    }

    /**
     * Show fragment in corresponding container, add to back stack and set transition.
     * @param fragment the fragment to show in the frame layout.
     * @param frame the unique identifier of the frame layout to set the fragment.
     */
    private fun fragmentTransaction(fragment: Fragment, frame: Int) {
        if (!fm.isDestroyed) {
            fm.beginTransaction()
                    .replace(frame, fragment, fragment.javaClass.simpleName)
                    .addToBackStack(fragment.javaClass.simpleName)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit()
        }
    }

    /**
     * Remove all fragments from the back stack.
     */
    internal fun clearAllBackStack() { while (fm.backStackEntryCount > 0) fm.popBackStackImmediate() }

    // --------------
    // SHOW FRAGMENT
    // --------------

    /**
     * Show EstateDetailFragment for the given estate, with tablet mode support.
     * @param estate the estate to show details.
     * @param isUpdate true if is call for an update, false for first launching data.
     */
    internal fun showEstateDetail(estate: Estate, isUpdate: Boolean) {
        if (isFrame2Visible) {
            if (getCurrentFragment() is EstateDetailFragment)
                estateDetailFragment?.updateEstate(estate)
            else if (!isUpdate)
                configureAndShowFragment(FRAG_ESTATE_DETAIL, estate)
            estateListFragment?.scrollToNewItem(null, estate)
        } else
            configureAndShowFragment(FRAG_ESTATE_DETAIL, estate)
    }

    /**
     * Resume fragment, when resume activity or when start application.
     * @param estateNotification the estate notification to show, null is there isn't.
     */
    internal fun resumeFragment(estateNotification: Estate?) {
        // Show estate notification if there's one.
        if (estateNotification != null) {
            if (isFrame2Visible) configureAndShowFragment(FRAG_ESTATE_LIST, estateNotification)
            else configureAndShowFragment(FRAG_ESTATE_DETAIL, estateNotification)
            activity.intent.removeExtra(ESTATE_NOTIFICATION)
            // Resume last visible fragment if there's one, else launch estate list frag.
        } else {
            val tempFragmentKey = fragmentKey
            communication.setFragmentKey(NO_FRAG)
            configureAndShowFragment(
                    if (tempFragmentKey != NO_FRAG) tempFragmentKey
                    else FRAG_ESTATE_LIST,
                    null)
        }
    }

    /**
     * Show previous fragment in back stack, and set fragmentKey with restored fragment.
     */
    internal fun fragmentBack() {
        getCurrentFragment()?.let { communication.setFragmentKey(retrievedFragKeyFromClass(it::class.java)) }
        setTitleActivity(activity, fragmentKey, isFrame2Visible)
        switchFrameSizeForTablet(frameLayout, fragmentKey, isFrame2Visible)
        fabFilter.adaptFabFilter(fragmentKey, isFrame2Visible)
    }

    // --------------
    // BUNDLE
    // --------------

    /**
     * Set bundle instance only if given is null.
     * @param bundle the bundle to set.
     */
    private fun setBundle(bundle: Bundle?): Bundle = bundle ?: Bundle()

    /**
     * Populate estate to fragment with bundle.
     * @param fragment the fragment instance to send estate.
     * @param estate the estate to populate.
     */
    private fun populateEstateToFragment(fragment: Fragment, estate: Estate) {
        fragment.arguments = setBundle(fragment.arguments)
        val bundleKey = if (fragment is EstateDetailFragment) ESTATE_DETAIL else ESTATE_NOTIFICATION_FOR_LIST
        fragment.arguments?.putParcelable(bundleKey, estate)
    }

    /**
     * Populate estate list to fragment with bundle.
     * @param fragment the fragment instance to send estate.
     */
    private fun populateEstateListToFragment(fragment: Fragment) {
        fragment.arguments = setBundle(fragment.arguments)
        fragment.arguments?.putParcelableArrayList(ESTATE_LIST_MAP,
                (get<ManageFiltersHelper>().getFilteredEstateList
                        ?: get<ManageFiltersHelper>().getFullEstateList) as ArrayList?)
    }

    /**
     * Set Maps Fragment Bundle to send data, populate estate list and set the map mode.
     * @param fragment the fragment instance to send data.
     */
    private fun setMapsFragmentBundle(fragment: Fragment) {
        fragment.arguments = setBundle(fragment.arguments)
        populateEstateListToFragment(fragment)
        fragment.arguments?.putInt(MAP_MODE, FULL_MODE)
    }

    // ----------------------------
    // BOTTOM SHEET FILTER FRAGMENT
    // ----------------------------

    /**
     * Configure and show bottom sheet filter fragment.
     * @param state the state to open the bottom sheet.
     */
    internal fun configureAndShowBottomSheetFilterFragment(state: Int) {
        fabFilter.fabFilterVisibility(true)
        configureAndShowFilterFragment()
        bottomSheet = BottomSheetBehavior.from(activity.activity_main_bottom_sheet)
        bottomSheet.state = state
        bottomSheet.addBottomSheetCallback(bottomSheetCallback)
    }

    /**
     * Configure and show filter fragment.
     */
    private fun configureAndShowFilterFragment() {
        var fragment = getFilterFragment()
        if (fragment == null) {
            fragment = FilterFragment()
            fm.beginTransaction()
                    .replace(R.id.activity_main_bottom_sheet, fragment, fragment.javaClass.simpleName)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit()
        }
        fragment.scrollToTop()
    }

    /**
     * Close bottom sheet filter fragment (hide).
     * @param toRemove true if remove fragment.
     */
    internal fun closeFilterFragment(toRemove: Boolean) {
        if (toRemove)
            fm.findFragmentById(R.id.activity_main_bottom_sheet)?.let { fm.beginTransaction().remove(it).commit() }

        bottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
        fabFilter.fabFilterVisibility(false)
    }

    // Return true if the bottom sheet is initialized
    internal val isBottomSheetInitialized get() = ::bottomSheet.isInitialized

    // Return true if the bottom sheet fragment is state expanded or half expanded
    internal val isExpanded get() =
            (bottomSheet.state == BottomSheetBehavior.STATE_HALF_EXPANDED || bottomSheet.state == BottomSheetBehavior.STATE_EXPANDED)

    // --------------
    // GETTERS
    // --------------

    // Try to get Fragment instance from current and back stack, if not found value was null.
    internal val estateListFragment
        get() = (fm.findFragmentByTag(EstateListFragment::class.java.simpleName) as EstateListFragment?)

    internal val estateDetailFragment
        get() = (fm.findFragmentByTag(EstateDetailFragment::class.java.simpleName) as EstateDetailFragment?)

    internal fun getMapsFragment() = (fm.findFragmentByTag(MapsFragment::class.java.simpleName) as MapsFragment?)

    // Try to get MapsFragment instance child of EstateDetailFragment.
    internal val mapsFragmentChildDetail
        get() = (getCurrentFragment()?.childFragmentManager
                ?.findFragmentById(R.id.fragment_estate_detail_container_map) as MapsFragment?)

    /**
     * Return the current fragment instance attached to frame layout 1.
     * @return the current fragment instance attached to frame layout 1.
     */
    private fun getCurrentFragment(): Fragment? = fm.findFragmentById(getFrame(fragmentKey, isFrame2Visible))

    /**
     * Get Filter Fragment instance.
     * @return the current filter fragment instance.
     */
    internal fun getFilterFragment(): FilterFragment? = fm.findFragmentById(R.id.activity_main_bottom_sheet) as FilterFragment?

    /**
     * Listener for bottom sheet, callback when state changed.
     */
    private val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onSlide(bottomSheet: View, slideOffset: Float) {}

        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN)
                fabFilter.fabFilterVisibility(false)
        }
    }
}