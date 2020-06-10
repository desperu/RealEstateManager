package org.desperu.realestatemanager.ui.main

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.SearchView
import androidx.core.view.GravityCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
import com.facebook.stetho.Stetho
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
 * Interface to allow communications with this activity.
 */
interface MainCommunication {
    /**
     * Get the current fragment instance, from fragment manager.
     * @return the current Fragment instance.
     */
    fun getFilterFragment(): Fragment?

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

    // FOR DATA
    @JvmField @State var fragmentKey: Int = -1
    private val fm = supportFragmentManager
    private lateinit var bottomSheet: BottomSheetBehavior<View>
    private val isExpanded get() = bottomSheet.state == STATE_HALF_EXPANDED || bottomSheet.state == STATE_EXPANDED
    private var hasFilter = false
    private var query = ""

    // FOR INTENT
    private val fullEstateList: List<Estate>? get() = intent.getParcelableArrayListExtra(FULL_ESTATE_LIST)
    private val filteredEstateList: List<Estate>? get() = intent.getParcelableArrayListExtra(FILTERED_ESTATE_LIST)
    private val estateNotification get() = intent.getParcelableExtra<Estate>(ESTATE_NOTIFICATION)

    // Try to get Fragment instance from back stack, if not found value was null.
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
        Stetho.initializeWithDefaults(this) // TODO For test only, to remove
        configureToolBar()
        configureSearchView()
        configureDrawerLayout()
        configureNavigationView()
        configureAndShowFragment(FRAG_ESTATE_LIST, null)
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
    private fun configureSearchView() {
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

            // Populate estate to fragment with bundle if there's one.
            if (estate != null) populateEstateToFragment(fragment, estate)

            // Populate estate list to maps fragment with bundle.
            if (fragmentKey == FRAG_ESTATE_MAP) setMapsFragmentBundle(fragment)

            // Clear all back stack when recall Estate List Fragment,
            // because it's the root fragment of this activity.
            if (fragmentKey == FRAG_ESTATE_LIST)
                while (fm.backStackEntryCount > 0) fm.popBackStackImmediate()
            // TODO manage frame layout for tablet and landscape
            // If second frame layout is visible, put other fragments in it.
//            else frameLayout = activity_main_frame_layout2 ?: activity_main_frame_layout

            // Show fragment in corresponding container, add to back stack and set transition.
            fm.beginTransaction()
                    .replace(activity_main_frame_layout.id, fragment, fragment.javaClass.simpleName)
                    .addToBackStack(fragment.javaClass.simpleName)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit()
        }
    }

    /**
     * Populate estate to fragment with bundle.
     * @param fragment the fragment instance to send estate.
     * @param estate the estate to populate.
     */
    private fun populateEstateToFragment(fragment: Fragment, estate: Estate) {
        fragment.arguments = fragment.arguments ?: Bundle()
        fragment.arguments?.putParcelable(ESTATE_DETAIL, estate)
    }

    /**
     * Populate estate list to fragment with bundle.
     * @param fragment the fragment instance to send estate.
     * @param bundleKey the bundle key to use.
     */
    private fun populateEstateListToFragment(fragment: Fragment, bundleKey: String) {
        fragment.arguments = fragment.arguments ?: Bundle()
        fragment.arguments?.putParcelableArrayList(bundleKey, (filteredEstateList ?: fullEstateList) as ArrayList)
    }

    /**
     * Set Maps Fragment Bundle to send data, populate estate list and set the map mode.
     * @param fragment the fragment instance to send data.
     */
    private fun setMapsFragmentBundle(fragment: Fragment) {
        fragment.arguments = fragment.arguments ?: Bundle()
        populateEstateListToFragment(fragment, ESTATE_LIST_MAP)
        fragment.arguments?.putInt(MAP_MODE, FULL_MODE)
    }

    /**
     * Configure and show bottom sheet filter fragment.
     * @param state the state to open the bottom sheet.
     */
    private fun configureAndShowBottomSheetFilterFragment(state: Int) {
        fabFilterVisibility(true)
        var fragment: Fragment? = getFilterFragment()
        if (fragment == null) {
            fragment = FilterFragment()
            populateEstateListToFragment(fragment, FILTER_ESTATE_LIST)
            fm.beginTransaction()
                    .replace(activity_main_bottom_sheet.id, fragment, fragment.javaClass.simpleName)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit()
        }
        bottomSheet = from(activity_main_bottom_sheet)
        bottomSheet.state = state
        val callback = object : BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {}

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == STATE_HIDDEN)
                    fabFilterVisibility(false)
            }
        }
        bottomSheet.addBottomSheetCallback(callback)
        (fragment as FilterFragment?)?.scrollToTop()
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
        if (estateNotification != null) {
            configureAndShowFragment(FRAG_ESTATE_DETAIL, estateNotification)
            intent.removeExtra(ESTATE_NOTIFICATION)
        } else
            configureAndShowFragment(fragmentKey, null)
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
            R.id.activity_main_menu_drawer_credit -> showCreditSimulation()
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

    override fun onBackPressed() {
        // If drawer is open, close it.
        if (activity_main_drawer_layout.isDrawerOpen(GravityCompat.START))
            activity_main_drawer_layout.closeDrawer(GravityCompat.START)

        // If bottom sheet is state expanded , hide it.
        else if (::bottomSheet.isInitialized && isExpanded)
            closeFilterFragment(false)

        // If search view is shown, hide it.
        else if (toolbar_search_view != null && toolbar_search_view.isShown)
            switchSearchViewVisibility(false)

        // If map is expended in estate detail fragment, collapse it.
        else if (mapsFragmentChildDetail?.view?.fragment_maps_fullscreen_button?.tag == FULL_SIZE)
                MapMotionLayout(this, mapsFragmentChildDetail?.view).switchMapSize()

        // If current fragment is EstateListFragment, remove it and call super to finish activity.
        else if (fragmentKey == FRAG_ESTATE_LIST) {
            fm.popBackStackImmediate()
            super.onBackPressed()

        // Else show previous fragment in back stack, and set fragment field with restored fragment.
        } else {
            super.onBackPressed()
            getCurrentFragment()?.let { fragmentKey = retrievedFragKeyFromClass(it::class.java) }
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
    internal fun showEstateDetailFragment(estate: Estate) =
            configureAndShowFragment(FRAG_ESTATE_DETAIL, estate)

    /**
     * On click fab filter action.
     * @param v the clicked view (the fab).
     */
    fun onClickFilter(v: View) {
        if (v.tag == UNFILTERED)
            configureAndShowBottomSheetFilterFragment(STATE_HALF_EXPANDED)
        else {
            fullEstateList?.let { updateEstateList(it, false) }
            intent.removeExtra(FILTERED_ESTATE_LIST)
        }
    }

    /**
     * Apply the searched query term to filter the estate list, and send query term to corresponding fragment.
     * @param query the query term to search.
     */
    private fun onSearchTextChange(query: String) = lifecycleScope.launch(Dispatchers.Main) {
        this@MainActivity.query = query
        var searchedList = fullEstateList
        if (hasFilter) getFilterFragment()?.applyFilters(searchedList)
        else if (query.isNotBlank()) {
            searchedList = fullEstateList?.let { SearchHelper().applySearch(it, query) }
            searchedList?.let { updateEstateList(it); saveFilteredList(searchedList) }
        } else if (!hasFilter)
            fullEstateList?.let { estateListFragment?.updateEstateList(it) }
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
    private fun showCreditSimulation() =
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
            if (hasFilter) {
                saveFilteredList(filteredList)
                closeFilterFragment(false)
            }
            switchFabFilter(hasFilter)
            dispatchDataToFragment(filteredList)
        }
    }

    @Suppress("unchecked_cast")
    private fun dispatchDataToFragment(value: Any) = when (value) {
        is Estate -> updateEstate(value)
        is List<*> -> updateEstateList(value as List<Estate>)
        else -> throw IllegalArgumentException("Can't retrieve the data type !")
    }

    private fun updateEstate(estate: Estate) {
        TODO("To implement or not LOL")
    }

    /**
     * Try to update estate list in all fragments lists.
     * @param estateList the estate list to set.
     */
    private fun updateEstateList(estateList: List<Estate>) {
        estateListFragment?.updateEstateList(estateList)
        mapsFragment?.updateEstateList(estateList)
    }

    /**
     * Save the filtered list in intent data.
     * @param filteredList the filtered list to save.
     */
    private fun saveFilteredList(filteredList: List<Estate>) =
            intent.putParcelableArrayListExtra(FILTERED_ESTATE_LIST, filteredList as ArrayList)

    // -----------------
    // FILTERS
    // -----------------

    /**
     * Apply search and filters to the estate list, and show result.
     */
    private fun applyFilters() = lifecycleScope.launch(Dispatchers.Main) {
        var filteredList: List<Estate>? = null
        if (hasFilter)
            getFilterFragment()?.applyFilters(filteredList)
        if (query.isNotBlank())
            filteredList = fullEstateList?.let { SearchHelper().applySearch(it, query) }
        // updateEstateList()
    }

    // -----------------
    // UI
    // -----------------

    /**
     * Set title activity name for fragment.
     * @param titleFragment the fragment title.
     */
    private fun setTitleActivity(titleFragment: String) { title = titleFragment }

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
            val animation: Animation = AnimationUtils.loadAnimation(baseContext, R.anim.search_view_anim_hide)
            (toolbar_search_view as View).startAnimation(animation)
            toolbar_search_view.visibility = View.INVISIBLE
            toolbar_search_view.setQuery(null, true)
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

    /**
     * Properly position fab filter, depends of the current fragment.
     * @param toEnd true to align with parent end; false to align with parent start.
     */
    private fun fabFilterPosition(toEnd: Boolean) {
        activity_main_fab_filter.updateLayoutParams {
            this as RelativeLayout.LayoutParams
            addRule(if (toEnd) RelativeLayout.ALIGN_PARENT_END
                    else RelativeLayout.ALIGN_PARENT_START)
        }
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
            position?.let { estateListFragment?.scrollToNewItem(it) }

            // Update estate detail data, with it's view model.
            estateDetailFragment?.getViewModel?.updateEstate(newEstate)
            // Update estate bundle for estate detail fragment.
            newEstate?.let { estateDetailFragment?.let { it1 -> populateEstateToFragment(it1, it) } }
        }
    }

    /**
     * Return the current fragment instance attached to frame layout 1.
     * @return the current fragment instance attached to frame layout 1.
     */
    private fun getCurrentFragment(): Fragment? = fm.findFragmentById(R.id.activity_main_frame_layout)

    /**
     * Get Filter Fragment instance.
     * @return the current filter fragment instance.
     */
    override fun getFilterFragment(): FilterFragment? = fm.findFragmentById(R.id.activity_main_bottom_sheet) as FilterFragment?

    /**
     * Get the associated fragment class with the given fragment key.
     * @param fragmentKey the given fragment key from witch get the key.
     */
    @Suppress("unchecked_cast")
    private fun<T: Fragment> getFragClassFromKey(fragmentKey: Int): Class<T> = when (fragmentKey) {
        FRAG_ESTATE_LIST -> EstateListFragment::class.java as Class<T>
        FRAG_ESTATE_MAP -> MapsFragment::class.java as Class<T>
        FRAG_ESTATE_DETAIL -> EstateDetailFragment::class.java as Class<T>
        FRAG_ESTATE_FILTER -> FilterFragment::class.java as Class<T>
        else -> throw IllegalArgumentException("Fragment key not found : $fragmentKey")
    }

    /**
     * Retrieve the associated fragment key with the fragment class.
     * @param fragClass the given fragment class from witch retrieved the key.
     */
    private fun <T: Fragment> retrievedFragKeyFromClass(fragClass: Class<T>) = when (fragClass) {
        EstateListFragment::class.java -> FRAG_ESTATE_LIST
        MapsFragment::class.java -> FRAG_ESTATE_MAP
        EstateDetailFragment::class.java -> FRAG_ESTATE_DETAIL
        FilterFragment::class.java -> FRAG_ESTATE_FILTER
        else -> throw IllegalArgumentException("Fragment class not found : ${fragClass.simpleName}")
    }
}