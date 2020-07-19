package org.desperu.realestatemanager.ui.main

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.text.method.LinkMovementMethod
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_HALF_EXPANDED
import com.google.android.material.navigation.NavigationView
import icepick.State
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.desperu.realestatemanager.R
import org.desperu.realestatemanager.animation.MapMotionLayout
import org.desperu.realestatemanager.animation.ToolbarAnimHelper
import org.desperu.realestatemanager.base.BaseActivity
import org.desperu.realestatemanager.databinding.ToolbarBinding
import org.desperu.realestatemanager.di.ViewModelFactory
import org.desperu.realestatemanager.di.module.mainModule
import org.desperu.realestatemanager.filter.ManageFiltersHelper
import org.desperu.realestatemanager.model.Estate
import org.desperu.realestatemanager.ui.creditSimulator.CreditSimulatorActivity
import org.desperu.realestatemanager.ui.main.fragment.MainFragmentManager
import org.desperu.realestatemanager.ui.main.fragment.filter.FilterFragment
import org.desperu.realestatemanager.ui.manageEstate.MANAGE_ESTATE
import org.desperu.realestatemanager.ui.manageEstate.ManageEstateActivity
import org.desperu.realestatemanager.ui.settings.SettingsActivity
import org.desperu.realestatemanager.utils.*
import org.desperu.realestatemanager.view.FabFilterView
import org.koin.android.ext.android.get
import org.koin.core.parameter.parametersOf


/**
 * The arguments names for intent to received the data in this Activity.
 */
const val NEW_ESTATE: String = "newEstate" // For new or updated estate.
const val ESTATE_NOTIFICATION: String = "estateNotification" // For estate notification.

/**
 * Activity to show estate list, estate details, and maps fragment.
 * With option menu and drawer layout to navigate in application.
 *
 * @constructor Instantiates a new MainActivity.
 */
class MainActivity : BaseActivity(mainModule), NavigationView.OnNavigationItemSelectedListener, MainCommunication {

    // FOR UI
    @JvmField @State var fragmentKey: Int = NO_FRAG
    private val fm by lazy { MainFragmentManager(this, this as MainCommunication) }
    private val fabFilter: FabFilterView by lazy<FabFilterView> { activity_main_fab_filter }
    override val isFrame2Visible get() = activity_main_frame_layout2 != null

    // FOR DATA
    private val filters by lazy { get<ManageFiltersHelper>() }

    // FOR INTENT
    private val estateNotification get() = intent.getParcelableExtra<Estate>(ESTATE_NOTIFICATION)

    // --------------
    // BASE METHODS
    // --------------

    override fun getActivityLayout(): Int = R.layout.activity_main

    override fun configureDesign() {
        configureKoinDependency()
        configureToolBar()
        configureDrawerLayout()
        configureNavigationView()
        configureViewModel()
    }

    // -----------------
    // CONFIGURATION
    // -----------------

    /**
     * Configure koin dependency for main communication interface.
     */
    private fun configureKoinDependency() = get<MainCommunication> { parametersOf(this@MainActivity) }

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
     * Configure data binding with view model for the search view in toolbar.
     */
    private fun configureViewModel() {
        val binding: ToolbarBinding? = DataBindingUtil.bind(activity_main_toolbar)
        val viewModel = ViewModelProvider(this, ViewModelFactory(this)).get(ToolbarViewModel::class.java)

        binding?.viewModel = viewModel
    }

    // --------------
    // FRAGMENT
    // --------------

    /**
     * Return the fragment key value.
     * @return the fragment key value.
     */
    override fun getFragmentKey(): Int = fragmentKey

    /**
     * Set fragment key with the given value.
     * @param fragmentKey the fragment key value to set.
     */
    override fun setFragmentKey(fragmentKey: Int) { this.fragmentKey = fragmentKey }

    // -----------------
    // METHODS OVERRIDE
    // -----------------

    override fun onResume() {
        super.onResume()
        fm.resumeFragment(estateNotification)
        restoreSearchView()
        switchFabFilter(filters.hasFilters)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Handle Manage Estate Activity response on activity result.
        this.handleResponseManageEstate(requestCode, resultCode, data)
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.activity_main_menu_drawer_estate_list -> fm.configureAndShowFragment(FRAG_ESTATE_LIST, null)
            R.id.activity_main_menu_drawer_estate_map -> fm.configureAndShowFragment(FRAG_ESTATE_MAP, null)
            R.id.activity_main_menu_drawer_estate_new -> showManageEstateActivity()
            R.id.activity_main_menu_drawer_Search -> {
                switchSearchView(true, isReload = false)
                fm.configureAndShowBottomSheetFilterFragment(STATE_EXPANDED)
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
            R.id.activity_main_menu_search -> switchSearchView(false, isReload = false)
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() = when {
        // If drawer is open, close it.
        activity_main_drawer_layout.isDrawerOpen(GravityCompat.START) ->
            activity_main_drawer_layout.closeDrawer(GravityCompat.START)

        // If bottom sheet is state expanded , hide it.
        fm.isBottomSheetInitialized && fm.isExpanded ->
            closeFilterFragment(false)

        // If search view is shown, hide it.
        toolbar_search_view != null && toolbar_search_view.isShown ->
            switchSearchView(false, isReload = false)

        // If map is expended in estate detail fragment, collapse it.
        fm.mapsFragmentChildDetail?.view?.findViewById<ImageButton>(R.id.fragment_maps_fullscreen_button)?.tag == FULL_SIZE ->
            MapMotionLayout(this, fm.mapsFragmentChildDetail?.view).switchMapSize()

        // If current fragment is EstateListFragment, remove it and call super to finish activity.
        fragmentKey == FRAG_ESTATE_LIST -> {
            fm.clearAllBackStack()
            super.onBackPressed()
        }
        // If device is tablet and frag is not map frag, remove the two frags and call super to finish activity.
        isFrame2Visible && fragmentKey != FRAG_ESTATE_MAP -> {
            fm.clearAllBackStack()
            super.onBackPressed()
        }
        // Else show previous fragment in back stack.
        else -> fm.fragmentBack { super.onBackPressed() }
    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        // To check nav drawer item when fragment change.
        if (fragmentKey in 0..1)
            activity_main_nav_view.menu.getItem(fragmentKey).isChecked = true
        else
            activity_main_nav_view.checkedItem?.isChecked = false
    }

    override fun onPause() {
        super.onPause()
        // To prevent ui mistake when turn device and filter frag isn't null, it's shown in half expended mode if not remove.
        if (fm.isBottomSheetInitialized) closeFilterFragment(true)
    }

    // --------------------
    // ACTION
    // --------------------

    /**
     * On click fab filter action.
     * @param v the clicked view (the fab).
     */
    fun onClickFilter(v: View) {
        if (v.tag == UNFILTERED)
            fm.configureAndShowBottomSheetFilterFragment(STATE_HALF_EXPANDED)
        else {
            switchFabFilter(false)
            removeFilters(false)
        }
    }

    /**
     * Show EstateDetailFragment for the given estate, with tablet mode support.
     * @param estate the estate to show details.
     * @param isUpdate true if is call for an update, false for first launching data.
     */
    override fun showEstateDetail(estate: Estate, isUpdate: Boolean) = fm.showEstateDetail(estate, isUpdate)

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
    private fun showManageEstateActivity() =
        startActivityForResult(Intent(this, ManageEstateActivity::class.java)
                .putExtra(MANAGE_ESTATE, Estate()), RC_ESTATE)

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
     * Populate estate list to the koin instance of ManageFiltersHelper.
     * @param estateList the estate list to populate.
     */
    override fun populateEstateList(estateList: List<Estate>) {
        filters.setFullEstateList(estateList)
    }

    /**
     * Try to update estate list to all fragments lists.
     * @param estateList the estate list to populate.
     */
    override fun updateEstateList(estateList: List<Estate>) {
        fm.estateListFragment?.updateEstateList(estateList)
        fm.getMapsFragment()?.updateEstateList(estateList)
    }

    /**
     * Remove filters and reapply if there's a query.
     * @param isReload true if is called from swipe refresh to reload data from database.
     */
    override fun removeFilters(isReload: Boolean) {
        lifecycleScope.launch(Dispatchers.Main) { filters.removeFilters(isReload) }
    }

    // -----------------
    // UI
    // -----------------

    /**
     * Switch fab filter state, filtered or unfiltered.
     * @param hasFilter true if has filters, false otherwise.
     */
    override fun switchFabFilter(hasFilter: Boolean) {
        fabFilter.switchFabFilter(hasFilter)
    }

    /**
     * Switch search view visibility with animation.
     * @param fromDrawer true if is called from drawer, false otherwise.
     * @param isReload true if is called for a reload data, false otherwise.
     */
    override fun switchSearchView(fromDrawer: Boolean, isReload: Boolean) =
            ToolbarAnimHelper().switchSearchViewVisibility(this, toolbar_search_view, fromDrawer, isReload)

    /**
     * Restore search view state when resume activity.
     */
    private fun restoreSearchView() {
        val hasQuery = filters.getQuery.isNotBlank()
        if (hasQuery) {
            switchSearchView(false, isReload = false)
            toolbar_search_view.setQuery(filters.getQuery, false)
        }
    }

    /**
     * Close bottom sheet filter fragment (hide).
     * @param toRemove true if remove fragment.
     */
    override fun closeFilterFragment(toRemove: Boolean) = fm.closeFilterFragment(toRemove)

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
            val position = newEstate?.let { fm.estateListFragment?.addOrUpdateEstate(it) }
            // If an estate was added or updated, position is not null, scroll the recycler to the estate position.
            position?.let { fm.estateListFragment?.scrollToNewItem(it, newEstate) }

            // Update estate detail data, with it's view model.
            newEstate?.let { fm.estateDetailFragment?.updateEstate(it) }
        }
    }

    /**
     * Get Filter Fragment instance.
     * @return the current filter fragment instance.
     */
    override fun getFilterFragment(): FilterFragment? = fm.getFilterFragment()
}