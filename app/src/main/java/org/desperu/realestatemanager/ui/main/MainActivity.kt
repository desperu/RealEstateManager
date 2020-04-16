package org.desperu.realestatemanager.ui.main

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.SearchView
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.facebook.stetho.Stetho
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar.*
import org.desperu.realestatemanager.R
import org.desperu.realestatemanager.base.BaseActivity
import org.desperu.realestatemanager.model.Estate
import org.desperu.realestatemanager.ui.manageEstate.MANAGE_ESTATE
import org.desperu.realestatemanager.ui.manageEstate.ManageEstateActivity
import org.desperu.realestatemanager.utils.RC_ESTATE

/**
 * The name of the argument for passing the new or updated estate to this Activity.
 */
const val NEW_ESTATE: String = "newEstate"

/**
 * Activity to show estate list, estate details, and maps fragment.
 */
class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    // FOR DATA
    private val fm = supportFragmentManager
    private var fragment: Fragment? = null

    // --------------
    // BASE METHODS
    // --------------

    override fun getActivityLayout(): Int = R.layout.activity_main

    override fun configureDesign() {
        Stetho.initializeWithDefaults(this) // TODO For test only, to remove
        configureToolBar()
        configureDrawerLayout()
        configureNavigationView()
        configureAndShowFragment(EstateListFragment::class.java, null)
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

    // --------------
    // FRAGMENT
    // --------------

    /**
     * Configure and show fragments, with back stack management to restore instance.
     * @param fragmentClass the fragment class to show.
     * @param estate the estate to show in estate detail or maps.
     */
    private fun <T: Fragment> configureAndShowFragment(fragmentClass: Class<T>, estate: Estate?) {
        if (fragment?.javaClass != fragmentClass || estate != null) {

            // Restore instance from back stack if there's one,
            // else create a new instance for asked fragment.
            fragment = fm.findFragmentByTag(fragmentClass.simpleName) ?: fragmentClass.newInstance()

            // Populate estate to fragment with bundle if there's one.
            if (estate != null) populateEstateToFragment(fragment!!, estate)

            // Clear all back stack when recall Estate List Fragment,
            // because it's the root fragment of this activity.
            if (fragmentClass == EstateListFragment::class.java)
                while (fm.backStackEntryCount > 0) fm.popBackStackImmediate()
            // TODO manage frame layout for tablet and landscape
            // If second frame layout is visible, put other fragments in it.
//            else frameLayout = activity_main_frame_layout2 ?: activity_main_frame_layout

            // Show fragment in corresponding container, add to back stack and set transition.
            fm.beginTransaction()
                    .replace(activity_main_frame_layout.id, fragment!!, fragment?.javaClass?.simpleName)
                    .addToBackStack(fragment?.javaClass?.simpleName)
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
        val bundle = Bundle()
        bundle.putParcelable(ESTATE_DETAIL, estate)
        fragment.arguments = bundle
    }

    // -----------------
    // METHODS OVERRIDE
    // -----------------

    override fun onResume() {
        super.onResume()
//        if (this.isCurrentUserLogged()) {
//            configureAndShowFragment(if (this.currentFragment >= 0) currentFragment else MAP_FRAGMENT)
//            configureDataBindingForHeader()
//            this.loadOrCreateUserInFirestore()
//            enableNotifications()
//            this.manageResetBookedRestaurant()
//        } else this.startSignInActivity()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Handle Manage Estate Activity response on activity result.
        this.handleResponseManageEstate(requestCode, resultCode, data)
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.activity_main_menu_drawer_estate_list -> configureAndShowFragment(EstateListFragment::class.java, null)
            R.id.activity_main_menu_drawer_estate_map -> configureAndShowFragment(MapsFragment::class.java, null)
            R.id.activity_main_menu_drawer_estate_new -> showManageEstateActivity(null)
            else -> {}
        }
        activity_main_drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_main_menu, menu)
        toolbar_search_view.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
//                onSearchTextChange(query)
                return false
            }

            override fun onQueryTextChange(s: String): Boolean {
//                onSearchTextChange(s)
                return false
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.activity_main_menu_add -> showManageEstateActivity(null)
            R.id.activity_main_menu_update -> {}
            R.id.activity_main_menu_search ->
                toolbar_search_view.apply { visibility = View.VISIBLE; onActionViewExpanded() }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        // If drawer is open, close it.
        if (activity_main_drawer_layout.isDrawerOpen(GravityCompat.START))
            activity_main_drawer_layout.closeDrawer(GravityCompat.START)
        // If search view is shown, hide it.
        else if (toolbar_search_view != null && toolbar_search_view.isShown)
            hideSearchViewIfVisible()
        // If current fragment is EstateListFragment, remove it and call super to finish activity.
        else if (fragment?.javaClass == EstateListFragment::class.java) {
            fm.popBackStackImmediate()
            super.onBackPressed()
        // Else show previous fragment in back stack, and set fragment field with restored fragment.
        } else {
            super.onBackPressed()
            fragment = fm.findFragmentById(R.id.activity_main_frame_layout)
        }
    }

    // --------------------
    // ACTION
    // --------------------

    /**
     * Show EstateDetailFragment for the given estate.
     * @param estate the estate to show details.
     */
    internal fun showEstateDetailFragment(estate: Estate) = configureAndShowFragment(EstateDetailFragment::class.java, estate)

//    /**
//     * Manage click on Your Lunch button.
//     */
//    private fun onClickYourLunch() {
//        if (userDBViewModel.getUser().get() != null
//                && Objects.requireNonNull(userDBViewModel.getUser().get()).getBookedRestaurantId() != null)
//            showRestaurantDetailActivity(Objects.requireNonNull(userDBViewModel.getUser().get()).getBookedRestaurantId())
//        else Snackbar.make(drawerLayout, R.string.activity_main_message_no_booked_restaurant, Snackbar.LENGTH_SHORT).show()
//    }
//
//    /**
//     * Fetch restaurant on search text change, and send query term to corresponding fragment.
//     * @param query Query term to search.
//     */
//    private fun onSearchTextChange(query: String) {
//        this.queryTerm = query
//        when (fragment) {
//            is MapsFragment -> { isQueryForRestaurant = true
//                val mapsFragment: MapsFragment = this.fragment as MapsFragment
//                mapsFragment.onSearchQueryTextChange(query)
//            }
//            fragment.getClass() === RestaurantListFragment::class.java -> {
//                isQueryForRestaurant = true
//                val restaurantListFragment: RestaurantListFragment = this.fragment as RestaurantListFragment
//                restaurantListFragment.onSearchQueryTextChange(query)
//            }
//            fragment.getClass() === WorkmatesFragment::class.java -> {
//                isQueryForRestaurant = false
//                val workmatesFragment: WorkmatesFragment = this.fragment as WorkmatesFragment
//                workmatesFragment.onSearchQueryTextChange(query)
//            }
//        }
//    }

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
     * Start settings activity.
     */
    private fun showSettingsActivity() {
//        startActivity(Intent(this, SettingsActivity::class.java))
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
     * Hide search view if visible, and clear query.
     */
    private fun hideSearchViewIfVisible() { // TODO to perfect, if already check in parent function, submit = true why??
        if (toolbar_search_view != null && toolbar_search_view.isShown) {
            toolbar_search_view.visibility = View.GONE
            toolbar_search_view.setQuery(null, true)
        }
    }

    /**
     * Show Toast for given message.
     * @param message the given message to show.
     */
    private fun showToast(message: String) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

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
            // Try to get EstateListFragment instance from back stack, if not found value was null.
            val estateListFragment = (fm.findFragmentByTag(EstateListFragment::class.java.simpleName) as EstateListFragment?)
            // If an instance was found, add or update estate in list of view model and recycler view.
            val position = estateListFragment?.getViewModel()?.addOrUpdateEstate(data?.getParcelableExtra(NEW_ESTATE))
            // If an estate was added or updated, position is not null, scroll the recycler to the estate position.
            position?.let { estateListFragment.scrollToNewItem(it) }
        }
    }
}