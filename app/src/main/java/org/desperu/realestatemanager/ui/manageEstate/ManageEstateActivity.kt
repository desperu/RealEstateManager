package org.desperu.realestatemanager.ui.manageEstate

import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_manage_estate.*
import org.desperu.realestatemanager.R
import org.desperu.realestatemanager.base.BaseActivity
import org.desperu.realestatemanager.di.ViewModelFactory
import org.desperu.realestatemanager.model.Estate
import org.desperu.realestatemanager.ui.main.MainActivity
import org.desperu.realestatemanager.ui.main.NEW_ESTATE
import org.desperu.realestatemanager.utils.ESTATE_IMAGE
import org.desperu.realestatemanager.view.MyPageTransformer
import org.desperu.realestatemanager.view.ViewPagerAdapter

// FOR INTENT
const val MANAGE_ESTATE: String = "manageEstate"

class ManageEstateActivity: BaseActivity() {

    private var viewModel: ManageEstateViewModel? = null
    private lateinit var viewPager: ViewPager

    // --------------
    // BASE METHODS
    // --------------

    override fun getActivityLayout(): Int = R.layout.activity_manage_estate

    override fun configureDesign() {
        configureToolBar()
        configureUpButton()
        setViewModel()
        configureViewPagerAndTabs()
    }

    // -----------------
    // CONFIGURATION
    // -----------------

    /**
     * Get Estate from intent.
     */
    private fun getEstate() = intent.getParcelableExtra<Estate>(MANAGE_ESTATE)

    /**
     * Set view model instance.
     */
    private fun setViewModel() {
        if (viewModel == null) {
            viewModel = ViewModelProvider(this, ViewModelFactory(this)).get(ManageEstateViewModel::class.java)
            viewModel?.setEstate(getEstate())
        }
    }

    /**
     * Configure Tab layout and View pager.
     */
    private fun configureViewPagerAndTabs() {
        viewPager = activity_manage_estate_view_pager
        viewPager.adapter = ViewPagerAdapter(this, supportFragmentManager,
                FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT)
        viewPager.setPageTransformer(true, MyPageTransformer(viewPager))
        val tabLayout: TabLayout = activity_manage_estate_pager_tabs
        tabLayout.setupWithViewPager(viewPager)
        tabLayout.tabMode = TabLayout.MODE_FIXED
    }

    // -----------------
    // ACTION
    // -----------------

    /**
     * On click add estate button.
     */
    fun onClickAddEstate(v: View) {
        viewModel?.createOrUpdateEstate()
        showToast(getString(R.string.activity_manage_estate_create_estate_message))
        setResult(RESULT_OK, Intent(this, MainActivity::class.java)
                .putExtra(NEW_ESTATE, viewModel?.estate?.value))
        finish()
    }

    /**
     * On click add image.
     */
    fun onClickAddImage(v: View) {
        val currentItem = viewPager.currentItem
        val page = viewPager.adapter?.instantiateItem(viewPager, currentItem)
        if (currentItem == ESTATE_IMAGE && page != null)
            (page as ManageEstateFragment).onClickAddImage()
    }

    // --- GETTERS ---

    /**
     * Get view model instance.
     */
    fun getViewModel(): ManageEstateViewModel {
        if (viewModel == null) setViewModel()
        return viewModel as ManageEstateViewModel
    }

    // -----------------
    // UI
    // -----------------

    /**
     * Show Toast message.
     * @param message the message text to show.
     */
    private fun showToast(message: String) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}