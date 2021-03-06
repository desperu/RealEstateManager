package org.desperu.realestatemanager.ui.manageEstate

import android.app.Activity
import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.view.inputmethod.InputMethodManager.HIDE_NOT_ALWAYS
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_manage_estate.*
import kotlinx.android.synthetic.main.fragment_estate_sale.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.desperu.realestatemanager.R
import org.desperu.realestatemanager.base.BaseActivity
import org.desperu.realestatemanager.di.ViewModelFactory
import org.desperu.realestatemanager.model.Estate
import org.desperu.realestatemanager.notification.Notification
import org.desperu.realestatemanager.ui.main.MainActivity
import org.desperu.realestatemanager.ui.main.NEW_ESTATE
import org.desperu.realestatemanager.ui.manageEstate.fragment.ManageEstateFragment
import org.desperu.realestatemanager.ui.manageEstate.fragment.ManageEstateViewModel
import org.desperu.realestatemanager.utils.*
import org.desperu.realestatemanager.view.pageTransformer.MyPageTransformer

/**
 * The name of the argument for passing estate to this Activity.
 */
const val MANAGE_ESTATE: String = "manageEstate"

/**
 * Interface to allow communications with this activity.
 */
internal interface Communication {

    /**
     * Get the current view pager fragment instance.
     * @return the current ManageEstateFragment instance.
     */
    fun getCurrentViewPagerFragment(): ManageEstateFragment

    /**
     * Get the ManageViewModel instance.
     * @return the ManageViewModel instance.
     */
    fun getViewModel(): ManageEstateViewModel

    /**
     * Manage floating buttons visibility, setup with recycler scrolling.
     * @param toHide if true hide buttons, else show.
     */
    fun floatingVisibility(toHide: Boolean)
}

/**
 * Activity to manage estate with images and address.
 * @constructor Instantiates a new ManageEstateActivity.
 */
class ManageEstateActivity: BaseActivity(), Communication {

    // FOR DATA
    private var viewModel: ManageEstateViewModel? = null
    private lateinit var viewPager: ViewPager
    private lateinit var originalEstate: Estate

    /**
     * Companion object, used to redirect to this Activity.
     */
    companion object {
        /**
         * Redirects from an Activity to this Activity.
         * @param activity the activity use to perform redirection.
         * @param estate the estate to manage in this activity.
         */
        fun routeFromActivity(activity: AppCompatActivity, estate: Estate) {
            activity.startActivityForResult(Intent(activity, ManageEstateActivity::class.java).putExtra(MANAGE_ESTATE, estate), RC_ESTATE)
        }
    }

    // --------------
    // BASE METHODS
    // --------------

    override fun getActivityLayout(): Int = R.layout.activity_manage_estate

    override fun configureDesign() {
        configureToolBar()
        configureUpButton()
        saveOriginalEstate()
        configureViewPagerAndTabs()
    }

    // -----------------
    // CONFIGURATION
    // -----------------

    /**
     * Configure Tab layout and View pager.
     */
    private fun configureViewPagerAndTabs() {
        viewPager = activity_manage_estate_view_pager
        viewPager.adapter = ManageEstateAdapter(this, supportFragmentManager,
                FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT)
        viewPager.setPageTransformer(true, MyPageTransformer(viewPager))
        val tabLayout: TabLayout = activity_manage_estate_pager_tabs
        tabLayout.setupWithViewPager(viewPager)
        tabLayout.tabMode = TabLayout.MODE_FIXED
    }

    /**
     * Save the original estate state given to this activity for future comparison.
     */
    private fun saveOriginalEstate() {
        val givenEstate = getEstate()
        if (givenEstate != null) {
            originalEstate = givenEstate.copy()
            originalEstate.imageList = givenEstate.imageList.map { it.copy() }.toMutableList()
            originalEstate.address = givenEstate.address.copy()
        } else
            originalEstate = Estate()
    }

    // -----------------
    // METHODS OVERRIDE
    // -----------------

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_manage_estate_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
            R.id.activity_manage_estate_menu_valid -> saveEstateAndFinish()
        }

        return true
    }

    override fun onBackPressed() {
        if (hasEstateChanged())
            alertDialogSaveEstate()
        else {
            hideSoftKeyBoard()
            super.onBackPressed()
        }
    }

    // -----------------
    // ACTION
    // -----------------

    /**
     * On click add estate button.
     */
    @Suppress("unused_parameter")
    fun onClickAddEstate(v: View) { saveEstateAndFinish() }

    /**
     * On click add image.
     */
    @Suppress("unused_parameter")
    fun onClickAddImage(v: View) {
        if (viewPager.currentItem == ESTATE_IMAGE)
            getCurrentViewPagerFragment().onClickAddImage()
    }

    /**
     * Save managed estate in database, check sold date, show notification and toast to inform user,
     * send manage result to main activity and hide soft keyboard if visible, and finish this activity.
     */
    private fun saveEstateAndFinish() = lifecycleScope.launch(Dispatchers.Main) {
        if (!isSoldDateSet()) alertDialogNoSoldDate()
        else {
            viewModel?.createOrUpdateEstate()
            val estate = viewModel?.estate?.value
            estate?.let { showEstateNotif(it) }
            showToast(getString(R.string.activity_manage_estate_save_estate_message))
            setResult(RESULT_OK, Intent(baseContext, MainActivity::class.java)
                    .putExtra(NEW_ESTATE, estate))
            hideSoftKeyBoard()
            finish()
        }
    }

    /**
     * Show estate notification if enabled in settings.
     * @param estate the created or updated estate.
     */
    private fun showEstateNotif(estate: Estate) {
        val isNew = estate.id == 0L
        val notifEnabled =
                if (isNew) MySharedPreferences.getBoolean(this, NOTIFICATION_ENABLED, NOTIFICATION_DEFAULT)
                else !MySharedPreferences.getBoolean(this, DISABLE_UPDATE_NOTIFICATION, UPDATE_NOTIFICATION_DEFAULT)
        if (notifEnabled)
            Notification().createNotification(this, estate, isNew)
    }

    // -----------------
    // UI
    // -----------------

    /**
     * Show Toast message.
     * @param message the message text to show.
     */
    private fun showToast(message: String) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

    /**
     * Create alert dialog to handle back action from user, when the managed estate was modified.
     */
    private fun alertDialogSaveEstate() {
        val dialog: AlertDialog.Builder = AlertDialog.Builder(this, R.style.AlertDialogTheme)
        dialog.setTitle(R.string.alert_dialog_save_estate_title)
        dialog.setMessage(R.string.alert_dialog_save_estate_message)
        dialog.setPositiveButton(R.string.alert_dialog_save_estate_positive_button) { _, _ -> saveEstateAndFinish() }
        dialog.setNegativeButton(R.string.alert_dialog_save_estate_negative_button) { _, _ -> finish() }
        dialog.show()
    }

    /**
     * Create alert dialog when estate state is "Sold Out" and no sold date was register.
     */
    private fun alertDialogNoSoldDate() {
        val dialog: AlertDialog.Builder = AlertDialog.Builder(this, R.style.AlertDialogTheme)
        dialog.setTitle(R.string.alert_dialog_no_sold_date_title)
        dialog.setMessage(R.string.alert_dialog_no_sold_date_message)
        dialog.setPositiveButton(R.string.alert_dialog_no_sold_date_positive_button) { _, _ -> showSoldDatePicker() }
        dialog.setNegativeButton(R.string.alert_dialog_no_sold_date_negative_button) { _, _ ->
            viewModel?.estate?.value?.state = resources.getStringArray(R.array.estate_state_list)[0]
            saveEstateAndFinish()
        }
        dialog.setNeutralButton(R.string.alert_dialog_no_sold_date_neutral_button) { _, _ -> viewModel?.estate?.value?.state = ""; saveEstateAndFinish() }
        dialog.show()
    }

    /**
     * Show sold out date picker dialog.
     */
    private fun showSoldDatePicker() {
        viewPager.currentItem = ESTATE_SALE
        getCurrentViewPagerFragment().fragment_estate_sale_date_picker_sold_out_date.performClick()
    }

    /**
     * Hide soft keyboard if visible.
     */
    private fun hideSoftKeyBoard() {
        val inputMethodManager: InputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        if (inputMethodManager.isActive && currentFocus != null)
            inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, HIDE_NOT_ALWAYS)
    }

    /**
     * Switch visibility for floating buttons with animation.
     * @param toHide if must hide floating button.
     */
    override fun floatingVisibility(toHide: Boolean) {
        if (toHide) {
            activity_manage_estate_floating_add_estate.hide()
            activity_manage_estate_floating_add_image.hide()
        } else {
            activity_manage_estate_floating_add_estate.show()
            activity_manage_estate_floating_add_image.show()
        }
    }

    // -----------------
    // UTILS
    // -----------------

    /**
     * Check if estate has changed, between the original given to this activity
     * and the final estate, when user want leave manage activity.
     * @return true if original and final estates aren't equals, false otherwise.
     */
    private fun hasEstateChanged(): Boolean {
        viewModel?.bindDataInEstate()
        val finalEstate = viewModel?.estate?.value

        return compareValues(originalEstate, finalEstate) != EQUALS
    }

    /**
     * Check that the sold date is register if the estate state is "Sold Out".
     * @return true if not "Sold Out" or "Sold Out" with date set, false otherwise.
     */
    private fun isSoldDateSet() =
        if (viewModel?.estate?.value?.state == resources.getStringArray(R.array.estate_state_list)[1])
            viewModel?.soldDate?.get()?.isBlank() == false
        else true

    // --- GETTERS ---

    /**
     * Get Estate from intent.
     * @return the estate object retrieved from intent extra.
     */
    private fun getEstate() = intent.getParcelableExtra<Estate>(MANAGE_ESTATE)

    /**
     * Get the view model instance, set if null.
     * @return the ManageEstateViewModel instance.
     */
    override fun getViewModel(): ManageEstateViewModel {
        viewModel ?: run {
            viewModel = ViewModelProvider(this, ViewModelFactory(this)).get(ManageEstateViewModel::class.java)
            viewModel?.setEstate(getEstate())
        }
        return viewModel as ManageEstateViewModel
    }

    /**
     * Get the current view pager fragment instance.
     * @return the current fragment instance.
     */
    override fun getCurrentViewPagerFragment(): ManageEstateFragment =
            viewPager.adapter?.instantiateItem(viewPager, viewPager.currentItem) as ManageEstateFragment
}