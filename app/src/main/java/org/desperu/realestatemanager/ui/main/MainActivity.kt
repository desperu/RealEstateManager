package org.desperu.realestatemanager.ui.main

import android.content.Intent
import android.os.Build
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.SearchView
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.facebook.stetho.Stetho
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar.*
import org.desperu.realestatemanager.R
import org.desperu.realestatemanager.base.BaseActivity
import org.desperu.realestatemanager.di.module.dbModule
import org.desperu.realestatemanager.di.module.repositoryModule
import org.desperu.realestatemanager.model.Estate
import org.desperu.realestatemanager.ui.manageEstate.MANAGE_ESTATE
import org.desperu.realestatemanager.ui.manageEstate.ManageEstateActivity
import org.desperu.realestatemanager.utils.RC_ESTATE
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.KoinContextHandler
import org.koin.core.context.startKoin

// FOR INTENT
const val NEW_ESTATE: String = "estateResult"

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    // FOR DATA
    private var fragment: Fragment? = Fragment()

    // --------------
    // BASE METHODS
    // --------------

    override fun getActivityLayout(): Int = R.layout.activity_main

    override fun configureDesign() {
        Stetho.initializeWithDefaults(this) // TODO For test only, to remove
        initKoin()
        configureToolBar()
        configureDrawerLayout()
        configureNavigationView()
        configureAndShowFragment(EstateListFragment::class.java)
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
     * Initializes the application, by adding strict mode and starting koin.
     */
    private fun initKoin() {
        if (KoinContextHandler.getOrNull() == null)
            startKoin {
                androidLogger()
                androidContext(this@MainActivity)
                modules(listOf(dbModule, repositoryModule))
            }
    }

//    /**
//     * Configure and show corresponding fragment.
//     * @param fragmentKey Key for fragment.
//     */
//    private fun configureAndShowFragment(fragmentKey: Int) {
//        var titleActivity = getString(R.string.title_activity_main)
//        val bundle = Bundle()
//        fragment = supportFragmentManager
//                .findFragmentById(R.id.activity_main_frame_layout)
//        if (currentFragment !== fragmentKey) {
//            when (fragmentKey) {
//                MAP_FRAGMENT -> {
//                    fragment = MapsFragment.newInstance()
//                    bundle.putStringArrayList(PLACE_ID_LIST_MAPS, placeIdList)
//                    if (this.bounds != null) bundle.putParcelable(CAMERA_POSITION, cameraPosition)
//                    if (this.queryTerm != null && !this.queryTerm.isEmpty()) bundle.putString(QUERY_TERM_MAPS, queryTerm)
//                    bundle.putBoolean(MapsFragment.IS_QUERY_FOR_RESTAURANT_MAPS, isQueryForRestaurant)
//                    fragment.setArguments(bundle)
//                }
//                LIST_FRAGMENT -> {
//                    fragment = RestaurantListFragment.newInstance()
//                    bundle.putStringArrayList(PLACE_ID_LIST_RESTAURANT_LIST, placeIdList)
//                    bundle.putParcelable(BOUNDS, bounds)
//                    if (this.queryTerm != null && !this.queryTerm.isEmpty()) bundle.putString(QUERY_TERM_LIST, queryTerm)
//                    bundle.putBoolean(IS_QUERY_FOR_RESTAURANT_LIST, isQueryForRestaurant)
//                    bundle.putParcelable(NEARBY_BOUNDS, nearbyBounds)
//                    bundle.putParcelable(USER_LOCATION, userLocation)
//                    fragment.setArguments(bundle)
//                }
//                WORKMATES_FRAGMENT -> {
//                    fragment = WorkmatesFragment.newInstance()
//                    if (this.queryTerm != null && !this.queryTerm.isEmpty()) bundle.putString(QUERY_TERM_WORKMATES, queryTerm)
//                    fragment.setArguments(bundle)
//                    titleActivity = getString(R.string.title_fragment_workmates)
//                }
//                CHAT_FRAGMENT -> {
//                    fragment = ChatFragment.newInstance()
//                    this.hideSearchViewIfVisible()
//                    titleActivity = getString(R.string.title_fragment_chat)
//                }
//            }
//            supportFragmentManager.beginTransaction()
//                    .replace(R.id.activity_main_frame_layout, fragment)
//                    .commit()
//            this.setTitleActivity(titleActivity)
//            if (toolbar.findViewById(R.id.activity_main_menu_search) != null) toolbar.findViewById(R.id.activity_main_menu_search).setVisibility(
//                    if (fragmentKey != CHAT_FRAGMENT) View.VISIBLE else View.GONE)
//        }
//        currentFragment = fragmentKey
//    }

//    /**
//     * Configure data binding for Navigation View Header with user info.
//     */
//    private fun configureDataBindingForHeader() {
//        // Enable Data binding for user info
//        if (activityMainNavHeaderBinding == null) {
//            val headerView: View = navigationView!!.getHeaderView(0)
//            activityMainNavHeaderBinding = ActivityMainNavHeaderBinding.bind(headerView)
//        }
//        userAuthViewModel = UserAuthViewModel()
//        activityMainNavHeaderBinding.setUserAuthViewModel(userAuthViewModel)
//    }

    // --------------
    // FRAGMENT
    // --------------

//    private fun configureAndShowMainFragment() {
//        mainFragment = supportFragmentManager.findFragmentById(R.id.frame_layout_main) as MainFragment?
//        if (mainFragment == null && findViewById<View?>(R.id.frame_layout_main) != null) {
//            mainFragment = MainFragment()
//            supportFragmentManager.beginTransaction()
//                    .add(R.id.frame_layout_main, mainFragment)
//                    .commit()
//        }
//    }
//
    private fun configureAndShowFragment(fragmentClass: Class<*>) {
        if (fragment?.javaClass != fragmentClass) {
            fragment = supportFragmentManager.findFragmentById(R.id.activity_main_frame_layout)

            when (fragmentClass) {
                EstateListFragment::class.java -> fragment = EstateListFragment()
            }

            supportFragmentManager.beginTransaction()
                    .replace(activity_main_frame_layout.id, fragment!!)
                    .commit()
        }
    }

//    private fun configureAndShowFragment(fragmentClass: Class<*>, @IdRes frameLayout: Int) {
//        when (fragmentClass) {
////            EstateListFragment()::class.java -> fragment as EstateListFragment
//            EstateListFragment::class.java -> fragment = estateListFragment as EstateListFragment
//        }
//
//        fragment = supportFragmentManager.findFragmentById(frameLayout) as EstateListFragment?
//        if (fragment == null && frameLayout != null) {
//            fragment = EstateListFragment().newInstance()
//            supportFragmentManager.beginTransaction()
//                    .add(frameLayout, fragment as EstateListFragment)
//                    .commit()
//        }
//    }

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
        // Handle SignIn Activity response on activity result.
//        this.handleResponseAfterSignIn(requestCode, resultCode, data)
        // Handle Manage Estate Activity response on activity result.
        this.handleResponseManageEstate(requestCode, resultCode, data)
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.activity_main_menu_drawer_estate_list -> configureAndShowFragment(EstateListFragment::class.java)
            R.id.activity_main_menu_drawer_estate_map -> this.showSettingsActivity()
            R.id.activity_main_menu_drawer_estate_new -> this.showManageEstateActivity(Estate())
//            R.id.activity_main_menu_bottom_map -> configureAndShowFragment(MAP_FRAGMENT)
//            R.id.activity_main_menu_bottom_list -> configureAndShowFragment(LIST_FRAGMENT)
//            R.id.activity_main_menu_bottom_workmates -> configureAndShowFragment(WORKMATES_FRAGMENT)
//            R.id.activity_main_menu_bottom_chat -> configureAndShowFragment(CHAT_FRAGMENT)
            else -> {}
        }
        activity_main_drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if (activity_main_drawer_layout.isDrawerOpen(GravityCompat.START))
            activity_main_drawer_layout.closeDrawer(GravityCompat.START)
        else if (toolbar_search_view != null && toolbar_search_view.isShown) {
            this.hideSearchViewIfVisible()
        } else super.onBackPressed()
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
            R.id.activity_main_menu_add -> {
                showManageEstateActivity(Estate())
                return true
            }
            R.id.activity_main_menu_update -> {
                return true
            }
            R.id.activity_main_menu_search -> {
                toolbar_search_view.visibility = View.VISIBLE
                toolbar_search_view.onActionViewExpanded()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // --------------------
    // ACTION
    // --------------------

//    fun onClickedMarker(id: String) { showRestaurantDetailActivity(id) }
//
//    fun onNewQuerySearch(isQueryForRestaurant: Boolean) { isQueryForRestaurant = isQueryForRestaurant }
//
//    fun saveNearbyBounds(nearbyBounds: RectangularBounds) { nearbyBounds = nearbyBounds }
//
//    fun onNewUserLocation(userLocation: Location) { userLocation = userLocation }
//
//    fun onNewPlacesIdList(placeIdList: ArrayList<String?>) { placeIdList = placeIdList }
//
//    fun onNewBounds(bounds: RectangularBounds) { bounds = bounds }
//
//    fun onNewCameraPosition(cameraPosition: CameraPosition) { cameraPosition = cameraPosition }
//
//    fun onItemClick(restaurantId: String) { showRestaurantDetailActivity(restaurantId) }
//
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
     * Start manage estate activity.
     * @param estate Estate to manage.
     */
    private fun showManageEstateActivity(estate: Estate) {
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
     * Set title activity name.
     * @param titleActivity Fragment title.
     */
    private fun setTitleActivity(titleActivity: String) {
        this.title = titleActivity
    }

    /**
     * Hide search view if visible, and clear query.
     */
    private fun hideSearchViewIfVisible() {
        if (toolbar_search_view != null && toolbar_search_view.isShown) {
            toolbar_search_view.visibility = View.GONE
            toolbar_search_view.setQuery(null, true)
        }
    }

    /**
     * Show Toast with corresponding message.
     * @param message Message to show.
     */
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    // -----------------
    // UTILS
    // -----------------

    private fun handleResponseManageEstate(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK && requestCode == RC_ESTATE) { // TODO to perform and comment
            val estateListFragment = (fragment as EstateListFragment)
            val position = estateListFragment.getViewModel().addOrUpdateEstate(data?.getParcelableExtra(NEW_ESTATE))
            position?.let { estateListFragment.scrollToNewItem(it) }
        }
    }
}