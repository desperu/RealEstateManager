package org.desperu.realestatemanager.ui.manageEstate

import android.view.View
import android.widget.Toast
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_manage_estate.*
import org.desperu.realestatemanager.R
import org.desperu.realestatemanager.base.BaseActivity
import org.desperu.realestatemanager.injection.ViewModelFactory
import org.desperu.realestatemanager.utils.ESTATE_ID
import org.desperu.realestatemanager.view.ViewPagerAdapter

class ManageEstateActivity: BaseActivity() {

    private var viewModel: ManageEstateViewModel? = null

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
     * Get Estate Id from intent.
     */
    private fun getEstateId(): Long = intent.getLongExtra(ESTATE_ID, 0)

    /**
     * Set view model instance.
     */
    private fun setViewModel() {
        if (viewModel == null) {
            viewModel = ViewModelProviders.of(this, ViewModelFactory(this)).get(ManageEstateViewModel::class.java)
            viewModel?.setEstate(getEstateId())
        }
    }

    /**
     * Configure Tab layout and View pager.
     */
    private fun configureViewPagerAndTabs() {
        activity_manage_estate_view_pager.adapter = ViewPagerAdapter(this, supportFragmentManager,
                FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT)
        val tabLayout: TabLayout = activity_manage_estate_pager_tabs
        tabLayout.setupWithViewPager(activity_manage_estate_view_pager)
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
        this.finishAfterTransition() // TODO to check and perform
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
     */
    private fun showToast(message: String) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}