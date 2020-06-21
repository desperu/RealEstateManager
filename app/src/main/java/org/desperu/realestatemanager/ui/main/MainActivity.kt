package org.desperu.realestatemanager.ui.main

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.OrientationEventListener
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.SearchView
import androidx.core.view.GravityCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.*
import com.google.android.material.navigation.NavigationView
import icepick.State
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_maps.view.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.desperu.realestatemanager.R
import org.desperu.realestatemanager.base.BaseActivity
import org.desperu.realestatemanager.filter.SearchHelper
import org.desperu.realestatemanager.model.Estate
import org.desperu.realestatemanager.ui.creditSimulator.CreditSimulatorActivity
import org.desperu.realestatemanager.ui.main.estateDetail.ESTATE_DETAIL
import org.desperu.realestatemanager.ui.main.estateDetail.EstateDetailFragment
import org.desperu.realestatemanager.ui.main.estateList.ESTATE_NOTIFICATION_FOR_LIST
import org.desperu.realestatemanager.ui.main.estateList.EstateListFragment
import org.desperu.realestatemanager.ui.main.estateMap.ESTATE_LIST_MAP
import org.desperu.realestatemanager.ui.main.estateMap.MAP_MODE
import org.desperu.realestatemanager.ui.main.estateMap.MapsFragment
import org.desperu.realestatemanager.ui.main.filter.FILTER_ESTATE_LIST
import org.desperu.realestatemanager.ui.main.filter.FilterFragment
import org.desperu.realestatemanager.ui.manageEstate.MANAGE_ESTATE
import org.desperu.realestatemanager.ui.manageEstate.ManageEstateActivity
import org.desperu.realestatemanager.ui.settings.SettingsActivity
import org.desperu.realestatemanager.utils.*
import org.desperu.realestatemanager.view.MapMotionLayout
import java.lang.ref.WeakReference
import kotlin.IllegalArgumentException

/**
 * The argument name for intent to received the new or updated estate in this Activity.
 */
const val NEW_ESTATE: String = "newEstate"

/**
 * The argument name for intent to received the full estate list in this Activity.
 */
const val FULL_ESTATE_LIST: String = "fullEstateList"

/**
 * The argument name for intent to received the filtered estate list in this Activity.
 */
const val FILTERED_ESTATE_LIST: String = "filteredEstateList"

/**
 * The argument name for intent to received the estate from notification to this Activity.
 */
const val ESTATE_NOTIFICATION: String = "estateNotification"

/**
 * The argument name for intent to received the estate from notification to this Activity.
 */
const val SCREEN_ORIENTATION: String = "screenOrientation"

/**
 * Interface to allow communications with this activity.
 */
interface MainCommunication {
    /**
     * Get the current fragment instance, from fragment manager.
     * @return the current Fragment instance.
     */
    fun getFilterFragment(): FilterFragment?

    /**
     * Update estate list after filtered or unfiltered list.
     * @param estateList the new estate list to set.
     * @param hasFilter true if list has filter, false otherwise.
     */
    fun updateEstateList(estateList: List<Estate>, hasFilter: Boolean)

    /**
     * Close filter fragment.
     * @param toRemove true if remove fragment.
     */
    fun closeFilterFragment(toRemove: Boolean)
}

/**
 * Activity to show estate list, estate details, and maps fragment.
 * With option menu and drawer layout to navigate in application.
 *
 * @constructor Instantiates a new MainActivity.
 */
class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener, MainCommunication {

    // FOR UI
    @JvmField @State var fragmentKey: Int = NO_FRAG
    private val fm = supportFragmentManager
    private lateinit var bottomSheet: BottomSheetBehavior<View>
    private val isExpanded get() = bottomSheet.state == STATE_HALF_EXPANDED || bottomSheet.state == STATE_EXPANDED
    private val isFrame2Visible get() = activity_main_frame_layout2 != null

    // FOR DATA
    @JvmField @State var hasFilter = false
    @JvmField @State var query = ""

    // FOR INTENT
    private val fullEstateList: List<Estate>? get() = intent.getParcelableArrayListExtra(FULL_ESTATE_LIST)
    private val filteredEstateList: List<Estate>? get() = intent.getParcelableArrayListExtra(FILTERED_ESTATE_LIST)
    private val estateNotification get() = intent.getParcelableExtra<Estate>(ESTATE_NOTIFICATION)
//    private val screenOrientation get() = intent.getIntExtra(SCREEN_ORIENTATION, 0)

    // Try to get Fragment instance from current and back stack, if not found value was null.
    private val estateListFragment
        get() = (fm.findFragmentByTag(EstateListFragment::class.java.simpleName) as EstateListFragment?)

    private val estateDetailFragment
        get() = (fm.findFragmentByTag(EstateDetailFragment::class.java.simpleName) as EstateDetailFragment?)

    private val mapsFragment
        get() = (fm.findFragmentByTag(MapsFragment::class.java.simpleName) as MapsFragment?)

    // Try to get MapsFragment instance child of EstateDetailFragment.
    private val mapsFragmentChildDetail
        get() = (getCurrentFragment()?.childFragmentManager
                ?.findFragmentById(R.id.fragment_estate_detail_container_map) as MapsFragment?)

    // --------------
    // BASE METHODS
    // --------------

    override fun getActivityLayout(): Int = R.layout.activity_main

    override fun configureDesign() {
        //Stetho.initializeWithDefaults(this) // TODO For test only, to remove
        configureToolBar()
        configureSearchViewListener()
        configureDrawerLayout()
        configureNavigationView()
//        configureScreenOrientationListener()
    }

    // -----------------
    // CONFIGURATION
    // -----------------

    /**
     * Configure Drawer layout.
     */
    private fun configureDrawerLayout() {
        val toggle = ActionBarDrawerToggle(this, activity_main_drawer_layout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        activity_main_drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
    }

    /**
     * Configure Navigation Drawer.
     */
    private fun configureNavigationView() {
        // Support status bar for KitKat.
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT)
            activity_main_nav_view.setPadding(0, 0, 0, 0)
        activity_main_nav_view.setNavigationItemSelectedListener(this)
    }

    /**
     * Configure Search View Listener in toolbar.
     */
    private fun configureSearchViewListener() {
        toolbar_search_view.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                onSearchTextChange(query)
                return false
            }

            override fun onQueryTextChange(s: String): Boolean {
                onSearchTextChange(s)
                return false
            }
        })
    }

//    /**
//     * Configure screen orientation listener, to support recall frag from back stack after turn device error.
//     * TODO Error : FragmentManager has been destroyed.
//     * TODO use supportFragmentManager instead of fm and this function ...
//     */
//    private fun configureScreenOrientationListener() { // TODO Weak reference or remove listener in on Pause
//        val orientationEventListener = WeakReference(object : OrientationEventListener(this) {
//            override fun onOrientationChanged(orientation: Int) {
//                manageScreenOrientation(orientation)
//            }
//        })
//        orientationEventListener.get()?.enable()
//    }
//
//    private fun manageScreenOrientation(orientation: Int) {
//        if (screenOrientation != orientation) {
//            estateListFragment?.setScreenOrientation(true)
//            intent.putExtra(SCREEN_ORIENTATION, 0)
//        }
//    }

    // --------------
    // FRAGMENT
    // --------------

    /**
     * Configure and show fragments, with back stack management to restore instance.
     * @param fragmentKey the fragment key to show corresponding fragment.
     * @param estate the estate to show in estate detail or maps.
     */
    private fun configureAndShowFragment(fragmentKey: Int, estate: Estate?) { // TODO Bug when click on estate in list after turn tablet need to clear back stack to force recreate
        if (this.fragmentKey != fragmentKey || estate != null) {
            this.fragmentKey = fragmentKey

            // Get the fragment class from the fragment key
            val fragmentClass: Class<Fragment> = getFragClassFromKey(fragmentKey)

            // Restore instance from back stack if there's one,
            // else create a new instance for asked fragment.
            val fragment = fm.findFragmentByTag(fragmentClass.simpleName) ?: fragmentClass.newInstance()

            // Populate data to fragment with bundle.
            populateDataToFragment(fragmentKey, fragment, estate)

            // Clear all back stack when recall Estate List Fragment,
            // because it's the root fragment of this activity.
            if (fragmentKey == FRAG_ESTATE_LIST) clearAllBackStack()

            // Set the corresponding activity title.
            setTitleActivity(fragmentKey)

            // If the device is a tablet and asked fragment is maps, collapse list frame,
            // else set the list frame to original size.
            switchFrameSizeForTablet()

            // Adapt fab filter position or hide, depend of the asked fragment.
            adaptFabFilter(fragmentKey)

            // Apply the fragment transaction in the corresponding frame.
            fragmentTransaction(fragment, getFrame(fragmentKey))
        }
    }

    /**
     * Populate data to fragment with bundle.
     * @param fragmentKey the of the asked fragment.
     * @param fragment the fragment instance to send data.
     * @param estate the to send to fragment.
     */
    private fun populateDataToFragment(fragmentKey: Int, fragment: Fragment, estate: Estate?) {
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
//                .commitAllowingStateLoss()
        }
    }

    /**
     * Remove all fragments from the back stack.
     */
    private fun clearAllBackStack() { while (fm.backStackEntryCount > 0) fm.popBackStackImmediate() }

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
     * @param bundleKey the bundle key to use.
     */
    private fun populateEstateListToFragment(fragment: Fragment, bundleKey: String) {
        fragment.arguments = setBundle(fragment.arguments)
        fragment.arguments?.putParcelableArrayList(bundleKey, (filteredEstateList ?: fullEstateList) as ArrayList?)
    }

    /**
     * Set Maps Fragment Bundle to send data, populate estate list and set the map mode.
     * @param fragment the fragment instance to send data.
     */
    private fun setMapsFragmentBundle(fragment: Fragment) {
        fragment.arguments = setBundle(fragment.arguments)
        populateEstateListToFragment(fragment, ESTATE_LIST_MAP)
        fragment.arguments?.putInt(MAP_MODE, FULL_MODE)
    }

    // ----------------------------
    // BOTTOM SHEET FILTER FRAGMENT
    // ----------------------------

    /**
     * Configure and show bottom sheet filter fragment.
     * @param state the state to open the bottom sheet.
     */
    private fun configureAndShowBottomSheetFilterFragment(state: Int) {
        fabFilterVisibility(true)
        configureAndShowFilterFragment()
        bottomSheet = from(activity_main_bottom_sheet)
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
            populateEstateListToFragment(fragment, FILTER_ESTATE_LIST)
            fm.beginTransaction()
                    .replace(activity_main_bottom_sheet.id, fragment, fragment.javaClass.simpleName)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit()
        }
        fragment.scrollToTop()
    }

    /**
     * Close bottom sheet filter fragment (hide).
     * @param toRemove true if remove fragment.
     */
    override fun closeFilterFragment(toRemove: Boolean) {
        if (toRemove)
            fm.findFragmentById(R.id.activity_main_bottom_sheet)?.let { fm.beginTransaction().remove(it).commit() }

        bottomSheet.state = STATE_HIDDEN
        fabFilterVisibility(false)
    }

    // -----------------
    // METHODS OVERRIDE
    // -----------------

    override fun onResume() { // TODO restore fragment when turn phone
        super.onResume()
        if (::bottomSheet.isInitialized) closeFilterFragment(false)
        if (estateNotification != null) {//            clearAllBackStack()
//            configureAndShowFragment(FRAG_ESTATE_LIST, null)
            if (isFrame2Visible) configureAndShowFragment(FRAG_ESTATE_LIST, estateNotification)
            else configureAndShowFragment(FRAG_ESTATE_DETAIL, estateNotification)
            intent.removeExtra(ESTATE_NOTIFICATION)
        } else
            configureAndShowFragment(
                    if (fragmentKey != NO_FRAG) fragmentKey
                    else FRAG_ESTATE_LIST,
                    null)
        // TODO restore search view state
        // TODO mistake with filter frag, it is state half expanded when turn phone if it is not null.
        // TODO show list when turn from map and fab filter position
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Handle Manage Estate Activity response on activity result.
        this.handleResponseManageEstate(requestCode, resultCode, data)
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.activity_main_menu_drawer_estate_list -> configureAndShowFragment(FRAG_ESTATE_LIST, null)
            R.id.activity_main_menu_drawer_estate_map -> configureAndShowFragment(FRAG_ESTATE_MAP, null)
            R.id.activity_main_menu_drawer_estate_new -> showManageEstateActivity(null)
            R.id.activity_main_menu_drawer_Search -> {
                switchSearchViewVisibility(true)
                configureAndShowBottomSheetFilterFragment(STATE_EXPANDED)
            }
            R.id.activity_main_menu_drawer_credit -> showCreditSimulatorActivity()
            R.id.activity_main_menu_drawer_settings -> showSettingsActivity()
            R.id.activity_main_drawer_about -> TODO("to implement")
            R.id.activity_main_drawer_help -> TODO("to implement")
        }
        activity_main_drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.activity_main_menu_add -> showManageEstateActivity(null)
            R.id.activity_main_menu_search -> switchSearchViewVisibility(false)
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() = when {
        // If drawer is open, close it.
        activity_main_drawer_layout.isDrawerOpen(GravityCompat.START) ->
            activity_main_drawer_layout.closeDrawer(GravityCompat.START)

        // If bottom sheet is state expanded , hide it.
        ::bottomSheet.isInitialized && isExpanded ->
            closeFilterFragment(false)

        // If search view is shown, hide it.
        toolbar_search_view != null && toolbar_search_view.isShown ->
            switchSearchViewVisibility(false)

        // If map is expended in estate detail fragment, collapse it.
        mapsFragmentChildDetail?.view?.fragment_maps_fullscreen_button?.tag == FULL_SIZE ->
            MapMotionLayout(this, mapsFragmentChildDetail?.view).switchMapSize()

        // If current fragment is EstateListFragment, remove it and call super to finish activity.
        fragmentKey == FRAG_ESTATE_LIST -> {
            clearAllBackStack()
            super.onBackPressed()
        }
        // If device is tablet and frag is not map frag, remove the two frags and call super to finish activity.
        isFrame2Visible && fragmentKey != FRAG_ESTATE_MAP -> {
            clearAllBackStack()
            super.onBackPressed()
        }
        // Else show previous fragment in back stack, and set fragment field with restored fragment.
        else -> {
            super.onBackPressed()
            getCurrentFragment()?.let { fragmentKey = retrievedFragKeyFromClass(it::class.java) }
            setTitleActivity(fragmentKey)
            switchFrameSizeForTablet()
            adaptFabFilter(fragmentKey)
        }
    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        // To check nav drawer item when fragment change.
        if (fragmentKey in 0..1)
            activity_main_nav_view.menu.getItem(fragmentKey).isChecked = true
        else
            activity_main_nav_view.checkedItem?.isChecked = false
    }

//    override fun onPause() {
//        super.onPause()
//        val savedState = getCurrentFragment()?.let { fm.saveFragmentInstanceState(it) }
//    }

    // --------------------
    // ACTION
    // --------------------

    /**
     * Show EstateDetailFragment for the given estate.
     * @param estate the estate to show details.
     */
    internal fun showEstateDetailFragment(estate: Estate) =
            configureAndShowFragment(FRAG_ESTATE_DETAIL, estate)

    /**
     * Show estate detail for tablet.
     * @param estate the estate to show details.
     * @param isUpdate true if is call for an update, false for first launching data.
     */
    internal fun showDetailForTablet(estate: Estate, isUpdate: Boolean) {
        if (isFrame2Visible) {
            if (getCurrentFragment() is EstateDetailFragment)
                estateDetailFragment?.updateEstate(estate)
            else if (!isUpdate)
                configureAndShowFragment(FRAG_ESTATE_DETAIL, estate)
            estateListFragment?.scrollToNewItem(null, estate)
        }
    }

    /**
     * On click fab filter action.
     * @param v the clicked view (the fab).
     */
    fun onClickFilter(v: View) {
        if (v.tag == UNFILTERED)
            configureAndShowBottomSheetFilterFragment(STATE_HALF_EXPANDED)
        else
            fullEstateList?.let { updateEstateList(it, false) }
    }

    /**
     * Listener for bottom sheet, callback when state changed.
     */
    private val bottomSheetCallback = object : BottomSheetCallback() {
        override fun onSlide(bottomSheet: View, slideOffset: Float) {}

        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == STATE_HIDDEN)
                fabFilterVisibility(false)
        }
    }

    /**
     * Apply the searched query term to filter the estate list, and send query term to corresponding fragment.
     * @param query the query term to search.
     */
    private fun onSearchTextChange(query: String) = lifecycleScope.launch(Dispatchers.Main) {
        if (this@MainActivity.query != query) {
            this@MainActivity.query = query
            var searchedList = fullEstateList
            when {
                hasFilter -> getFilterFragment()?.applyFilters(searchedList)
                query.isNotBlank() -> {
                    searchedList = fullEstateList?.let { SearchHelper().applySearch(it, query) }
                    searchedList?.let { updateEstateList(it) }
                }
                else -> fullEstateList?.let { updateEstateList(it) }
            }
        }
    }

    // -----------------
    // ACTIVITY
    // -----------------

    /**
     * Start manage estate activity to manage an existing estate or create a new.
     * @param estate Estate to manage, null for create new.
     */
    private fun showManageEstateActivity(estate: Estate?) {
        startActivityForResult(Intent(this, ManageEstateActivity::class.java).putExtra(MANAGE_ESTATE, estate), RC_ESTATE)
    }

    /**
     * Start Settings activity.
     */
    private fun showSettingsActivity() =
        startActivity(Intent(this, SettingsActivity::class.java))

    /**
     * Start Credit Simulation activity.
     */
    private fun showCreditSimulatorActivity() =
            startActivity(Intent(this, CreditSimulatorActivity::class.java))

    // -----------------
    // DATA
    // -----------------

    /**
     * Update estate list with new filtered or unfiltered estate list.
     * @param estateList the new estate list to set.
     * @param hasFilter true if list has filters, false otherwise.
     */
    override fun updateEstateList(estateList: List<Estate>, hasFilter: Boolean) {
        lifecycleScope.launch(Dispatchers.Main) {
            this@MainActivity.hasFilter = hasFilter
            var filteredList = estateList
            if (query.isNotBlank())
                filteredList = SearchHelper().applySearch(estateList, query)
            // TODO swipe refresh when have filter don't switch fab filter state
            if (hasFilter) closeFilterFragment(false)
            switchFabFilter(hasFilter)
            updateEstateList(filteredList)
        }
    }

    /**
     * Try to update estate list in all fragments lists.
     * @param estateList the estate list to set.
     */
    private fun updateEstateList(estateList: List<Estate>) {
        estateListFragment?.updateEstateList(estateList)
        mapsFragment?.updateEstateList(estateList)
        manageFilteredList(estateList)

    }

    /**
     * Manage the filtered list in intent data, save if is filtered,
     * remove intent data if is equal to full list.
     * @param filteredList the filtered list to manage.
     */
    private fun manageFilteredList(filteredList: List<Estate>) {
        if (filteredList != fullEstateList)
            intent.putParcelableArrayListExtra(FILTERED_ESTATE_LIST, filteredList as ArrayList)
        else
            intent.removeExtra(FILTERED_ESTATE_LIST)
    }

    // -----------------
    // UI
    // -----------------

    /**
     * Set the title activity name for the asked fragment.
     * @param fragmentKey the key of the asked fragment.
     */
    private fun setTitleActivity(fragmentKey: Int) {
        title = getString(when {
            fragmentKey == FRAG_ESTATE_MAP -> R.string.fragment_maps_name
            fragmentKey == FRAG_ESTATE_DETAIL && !isFrame2Visible-> R.string.fragment_estate_detail_name
            else -> R.string.app_name
        })
    }

    /**
     * Switch frame list size for tablet mode. If the device is a tablet and asked fragment is maps,
     * collapse list frame, else set the list frame to original size.
     */
    private fun switchFrameSizeForTablet() {
        if (isFrame2Visible)
            activity_main_frame_layout.updateLayoutParams<LinearLayout.LayoutParams> {
                weight = if (fragmentKey == FRAG_ESTATE_MAP) 0F else 1F
            }
    }

    /**
     * Show Toast for given message.
     * @param message the given message to show.
     */
    private fun showToast(message: String) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

    /**
     * Switch search view visibility, clear query and start animations.
     * @param fromDrawer true if called from menu drawer.
     */
    private fun switchSearchViewVisibility(fromDrawer: Boolean) {
        // Hide search view
        if (toolbar_search_view != null && toolbar_search_view.isShown && !fromDrawer) {
            toolbar_search_view.setQuery(null, true)
            val animation: Animation = AnimationUtils.loadAnimation(baseContext, R.anim.search_view_anim_hide)
            (toolbar_search_view as View).startAnimation(animation)
            toolbar_search_view.visibility = View.INVISIBLE
            animMenuItem(true)
        } else if (!toolbar_search_view.isShown){ // Show search view
            animMenuItem(false)
            toolbar_search_view.visibility = View.VISIBLE
            val animation: Animation = AnimationUtils.loadAnimation(baseContext, R.anim.search_view_anim_show)
            (toolbar_search_view as View).startAnimation(animation)
            toolbar_search_view.onActionViewExpanded()
        }
    }

    /**
     * Menu item animation, for show and hide, from, to right out of screen.
     * @param toShow {@code true} for show and {@code false} to hide menu item.
     */
    private fun animMenuItem(toShow: Boolean) {

        // Create animation object.
        val animRes = if (toShow) R.anim.menu_item_anim_show else R.anim.menu_item_anim_hide
        val animation: Animation = AnimationUtils.loadAnimation(this, animRes)

        // Get view for each menu item.
        val menuItemList = listOf<View?>(
                findViewById(R.id.activity_main_menu_search),
                findViewById(R.id.activity_main_menu_add),
                findViewById(R.id.activity_main_menu_update))

        // Set animation for each menu item.
        menuItemList.forEach { it?.startAnimation(animation) }
    }

    /**
     * Set fab filter visibility.
     * @param toHide if true hide fab filter, else show.
     */
    private fun fabFilterVisibility(toHide: Boolean) =
        if (toHide) activity_main_fab_filter.hide() else activity_main_fab_filter.show()

    /**
     * Switch fab filter state, normal (unfiltered) or filter when a filter is applied to the list.
     * @param hasFilter true if has filter applied.
     */
    @Suppress("deprecation")
    private fun switchFabFilter(hasFilter: Boolean) = activity_main_fab_filter.apply {
        if (hasFilter) {
            setImageDrawable(resources.getDrawable(R.drawable.ic_baseline_add_white_36))
            backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.bottomBarRed))
            rotation = 45F
            tag = FILTERED
        } else {
            setImageDrawable(resources.getDrawable(R.drawable.ic_baseline_filter_list_black_36))
            backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.filterBackground))
            rotation = 0F
            tag = UNFILTERED
        }
    }

    // TODO Create view !!!

    /**
     * Properly position fab filter, depends of the current fragment.
     * @param toEnd true to align with parent end; false to align with parent start.
     */
    private fun fabFilterPosition(toEnd: Boolean) {
        activity_main_fab_filter.updateLayoutParams<RelativeLayout.LayoutParams> {
            if (toEnd) {
                addRule(RelativeLayout.ALIGN_PARENT_END)
                removeRule(RelativeLayout.ALIGN_PARENT_START)
            } else {
                addRule(RelativeLayout.ALIGN_PARENT_START)
                removeRule(RelativeLayout.ALIGN_PARENT_END)
            }
        }
    }

    /**
     * Adapt the fab filter visibility and position, depends of the asked fragment key.
     * @param fragmentKey the asked fragment key.
     */
    private fun adaptFabFilter(fragmentKey: Int) = when {
        !isFrame2Visible && fragmentKey == FRAG_ESTATE_DETAIL -> fabFilterVisibility(true)
        !isFrame2Visible && fragmentKey == FRAG_ESTATE_MAP -> { fabFilterVisibility(false); fabFilterPosition(false) }
        isFrame2Visible -> fabFilterPosition(false)
        else -> { fabFilterVisibility(false); fabFilterPosition(true) }
    }

    // -----------------
    // UTILS
    // -----------------

    /**
     * Handle response when retrieve manage estate result, if estate list is shown,
     * add or update item in list and scroll to it.
     * @param requestCode Code of request.
     * @param resultCode Result code of request.
     * @param data Intent request result data.
     */
    private fun handleResponseManageEstate(requestCode: Int, resultCode: Int, data: Intent?) {
        // If result code and request code matches with manage estate result, populate new estate to the list.
        if (resultCode == RESULT_OK && requestCode == RC_ESTATE) {
            val newEstate: Estate? = data?.getParcelableExtra(NEW_ESTATE)

            // Add or update estate in list of view model and recycler view, if an instance was found.
            val position = newEstate?.let { estateListFragment?.addOrUpdateEstate(it) }
            // If an estate was added or updated, position is not null, scroll the recycler to the estate position.
            position?.let { estateListFragment?.scrollToNewItem(it, newEstate) }

            // Update estate detail data, with it's view model.
            newEstate?.let { estateDetailFragment?.updateEstate(it) }
        }
    }

    /**
     * Return the current fragment instance attached to frame layout 1.
     * @return the current fragment instance attached to frame layout 1.
     */
    private fun getCurrentFragment(): Fragment? = fm.findFragmentById(getFrame(fragmentKey))

    /**
     * Get Filter Fragment instance.
     * @return the current filter fragment instance.
     */
    override fun getFilterFragment(): FilterFragment? = fm.findFragmentById(R.id.activity_main_bottom_sheet) as FilterFragment?

    /**
     * Return the unique identifier of the corresponding frame layout.
     * @param fragmentKey the asked fragment key.
     * @return the corresponding frame layout.
     */
    private fun getFrame(fragmentKey: Int) =
            if (fragmentKey != FRAG_ESTATE_LIST && isFrame2Visible)
                R.id.activity_main_frame_layout2
            else
                R.id.activity_main_frame_layout

    /**
     * Get the associated fragment class with the given fragment key.
     * @param fragmentKey the given fragment key from witch get the key.
     * @return the corresponding fragment class.
     */
    @Suppress("unchecked_cast")
    private fun<T: Fragment> getFragClassFromKey(fragmentKey: Int): Class<T> = when (fragmentKey) {
        FRAG_ESTATE_LIST -> EstateListFragment::class.java as Class<T>
        FRAG_ESTATE_MAP -> MapsFragment::class.java as Class<T>
        FRAG_ESTATE_DETAIL -> EstateDetailFragment::class.java as Class<T>
        else -> throw IllegalArgumentException("Fragment key not found : $fragmentKey")
    }

    /**
     * Retrieve the associated fragment key with the fragment class.
     * @param fragClass the given fragment class from witch retrieved the key.
     * @return the corresponding fragment key.
     */
    private fun <T: Fragment> retrievedFragKeyFromClass(fragClass: Class<T>) = when (fragClass) {
        EstateListFragment::class.java -> FRAG_ESTATE_LIST
        MapsFragment::class.java -> FRAG_ESTATE_MAP
        EstateDetailFragment::class.java -> FRAG_ESTATE_DETAIL
        else -> throw IllegalArgumentException("Fragment class not found : ${fragClass.simpleName}")
    }
}