package org.desperu.realestatemanager.ui.main

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.core.view.GravityCompat
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
import org.desperu.realestatemanager.animation.MapMotionLayout
import org.desperu.realestatemanager.animation.ToolbarAnimHelper
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
import org.desperu.realestatemanager.utils.MainUtils.getFragClassFromKey
import org.desperu.realestatemanager.utils.MainUtils.getFrame
import org.desperu.realestatemanager.utils.MainUtils.retrievedFragKeyFromClass
import org.desperu.realestatemanager.utils.MainUtils.setTitleActivity
import org.desperu.realestatemanager.utils.MainUtils.switchFrameSizeForTablet
import org.desperu.realestatemanager.view.FabFilterView


/**
 * The arguments names for intent to received the data in this Activity.
 */
const val NEW_ESTATE: String = "newEstate" // For new or updated estate.
const val FULL_ESTATE_LIST: String = "fullEstateList" // For full estate list.
const val FILTERED_ESTATE_LIST: String = "filteredEstateList" // For filtered estate list.
const val ESTATE_NOTIFICATION: String = "estateNotification" // For estate notification.

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
    private val fabFilter: FabFilterView by lazy<FabFilterView> { activity_main_fab_filter }
    private lateinit var bottomSheet: BottomSheetBehavior<View>
    private val isExpanded get() = bottomSheet.state == STATE_HALF_EXPANDED || bottomSheet.state == STATE_EXPANDED
    override val isFrame2Visible get() = activity_main_frame_layout2 != null

    // FOR DATA
    @JvmField @State var hasFilter = false
    @JvmField @State var query = ""

    // FOR INTENT
    // TODO get from view model list for better update and communication, for map !! but here for turn phone restore data view model not kill for that ??
    private val fullEstateList: List<Estate>? get() = intent.getParcelableArrayListExtra(FULL_ESTATE_LIST)
    private val filteredEstateList: List<Estate>? get() = intent.getParcelableArrayListExtra(FILTERED_ESTATE_LIST)
    private val estateNotification get() = intent.getParcelableExtra<Estate>(ESTATE_NOTIFICATION)

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

    // --------------
    // FRAGMENT
    // --------------

    /**
     * Configure and show fragments, with back stack management to restore instance.
     * @param fragmentKey the fragment key to show corresponding fragment.
     * @param estate the estate to show in estate detail or maps.
     */
    private fun configureAndShowFragment(fragmentKey: Int, estate: Estate?) {
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
            setTitleActivity(this, fragmentKey, isFrame2Visible)

            // If the device is a tablet and asked fragment is maps, collapse list frame,
            // else set the list frame to original size.
            switchFrameSizeForTablet(activity_main_frame_layout, fragmentKey, isFrame2Visible)

            // Adapt fab filter position or hide, depend of the asked fragment.
            fabFilter.adaptFabFilter(fragmentKey, isFrame2Visible)

            // Apply the fragment transaction in the corresponding frame.
            fragmentTransaction(fragment, getFrame(fragmentKey, isFrame2Visible))
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
        fabFilter.fabFilterVisibility(true)
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
        fabFilter.fabFilterVisibility(false)
    }

    // -----------------
    // METHODS OVERRIDE
    // -----------------

    override fun onResume() {
        super.onResume()
        if (::bottomSheet.isInitialized) closeFilterFragment(false) // TODO not work, fab filter mistake and filter not saved when turn phone
        if (estateNotification != null) {
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
            R.id.activity_main_menu_drawer_estate_new -> showManageEstateActivity()
            R.id.activity_main_menu_drawer_Search -> {
                ToolbarAnimHelper().switchSearchViewVisibility(this, toolbar_search_view, true)
                configureAndShowBottomSheetFilterFragment(STATE_EXPANDED)
            }
            R.id.activity_main_menu_drawer_credit -> showCreditSimulatorActivity()
            R.id.activity_main_menu_drawer_settings -> showSettingsActivity()
            R.id.activity_main_drawer_about -> showAboutDialog()
            R.id.activity_main_drawer_help -> showHelpDocumentation()
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
            R.id.activity_main_menu_add -> showManageEstateActivity()
            R.id.activity_main_menu_search -> ToolbarAnimHelper().switchSearchViewVisibility(this, toolbar_search_view, false)
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
            ToolbarAnimHelper().switchSearchViewVisibility(this, toolbar_search_view, false)

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
        // Else show previous fragment in back stack, and set fragmentKey with restored fragment.
        else -> {
            super.onBackPressed()
            getCurrentFragment()?.let { fragmentKey = retrievedFragKeyFromClass(it::class.java) }
            setTitleActivity(this, fragmentKey, isFrame2Visible)
            switchFrameSizeForTablet(activity_main_frame_layout, fragmentKey, isFrame2Visible)
            fabFilter.adaptFabFilter(fragmentKey, isFrame2Visible)
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

    // --------------------
    // ACTION
    // --------------------

    /**
     * Show EstateDetailFragment for the given estate.
     * @param estate the estate to show details.
     */
    override fun showEstateDetailFragment(estate: Estate) =
            configureAndShowFragment(FRAG_ESTATE_DETAIL, estate)

    /**
     * Show estate detail for tablet.
     * @param estate the estate to show details.
     * @param isUpdate true if is call for an update, false for first launching data.
     */
    override fun showDetailForTablet(estate: Estate, isUpdate: Boolean) {
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
                fabFilter.fabFilterVisibility(false)
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
                    searchedList?.let { populateEstateList(it) }
                }
                else -> fullEstateList?.let { populateEstateList(it) }
            }
        }
    }

    /**
     * Show about dialog.
     */
    private fun showAboutDialog() {
        val dialog: AlertDialog = AlertDialog.Builder(this)
                .setTitle("${getString(R.string.activity_main_dialog_about_title)} ${getString(R.string.app_name)}")
                .setMessage(R.string.activity_main_dialog_about_message)
                .setPositiveButton(R.string.activity_main_dialog_about_positive_button, null)
                .show()
        dialog.findViewById<TextView>(android.R.id.message)?.movementMethod = LinkMovementMethod.getInstance()
    }

    /**
     * Show help documentation.
     */
    private fun showHelpDocumentation() {
        val browserIntent = Intent(Intent.ACTION_VIEW)
        browserIntent.setDataAndType(Uri.parse(DOCUMENTATION_URL), "text/html")
        startActivity(browserIntent)
    }

    // -----------------
    // ACTIVITY
    // -----------------

    /**
     * Start manage estate activity to manage an existing estate or create a new.
     */
    private fun showManageEstateActivity() {
        startActivityForResult(Intent(this, ManageEstateActivity::class.java).putExtra(MANAGE_ESTATE, Estate()), RC_ESTATE)
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
     * Populate estate list to this activity with intent.
     * @param estateList the estate list to populate.
     */
    override fun populateEstateListToMain(estateList: List<Estate>) {
        intent.putParcelableArrayListExtra(FULL_ESTATE_LIST, estateList as java.util.ArrayList)
    }

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
            if (hasFilter) closeFilterFragment(false)
            fabFilter.switchFabFilter(hasFilter)
            populateEstateList(filteredList)
        }
    }

    /**
     * Try to populate estate list to all fragments lists.
     * @param estateList the estate list to populate.
     */
    private fun populateEstateList(estateList: List<Estate>) {
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

    /**
     * Return the current fragment instance attached to frame layout 1.
     * @return the current fragment instance attached to frame layout 1.
     */
    private fun getCurrentFragment(): Fragment? = fm.findFragmentById(getFrame(fragmentKey, isFrame2Visible))

    /**
     * Get Filter Fragment instance.
     * @return the current filter fragment instance.
     */
    override fun getFilterFragment(): FilterFragment? = fm.findFragmentById(R.id.activity_main_bottom_sheet) as FilterFragment?
}