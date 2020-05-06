package org.desperu.realestatemanager.ui.main.estateDetail

import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import org.desperu.realestatemanager.R
import org.desperu.realestatemanager.base.BaseBindingFragment
import org.desperu.realestatemanager.databinding.FragmentEstateDetailBinding
import org.desperu.realestatemanager.model.Estate
import org.desperu.realestatemanager.ui.main.estateList.EstateRouter
import org.desperu.realestatemanager.ui.main.estateList.EstateRouterImpl
import org.desperu.realestatemanager.ui.main.estateList.EstateViewModel
import org.desperu.realestatemanager.ui.main.estateMap.MapsFragment


/**
 * The argument name for bundle, to received estate to this Fragment.
 */
const val ESTATE_DETAIL: String = "estateDetail"

/**
 * Interface to share the view model instance of this fragment.
 */
internal interface ShareViewModel {
    /**
     * Get the view model instance of this fragment.
     * @return the EstateViewModel instance.
     */
    fun getViewModel(): EstateViewModel?
}

/**
 * Fragment to show estate details.
 */
class EstateDetailFragment: BaseBindingFragment(), ShareViewModel {

    // FOR DATA
    private lateinit var binding: FragmentEstateDetailBinding
    private var viewModel: EstateViewModel? = null
    private var mapFragment: Fragment? = null

    // --------------
    // BASE METHODS
    // --------------

    override fun getBindingView(): View = configureViewModel()

    override fun configureDesign() {
        setHasOptionsMenu(true) // Enable option menu for this fragment.
    }

    override fun updateDesign() {
        configureAndShowMapFragment()
    }

    // -----------------
    // CONFIGURATION
    // -----------------

    /**
     * Configure data binding with view model.
     */
    private fun configureViewModel(): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_estate_detail, container, false)
        viewModel = getViewModel()

        binding.viewModel = viewModel
//        configureImageRecycler()
        return binding.root
    }

    /**
     * Configure and show maps fragment if google play services are available.
     */
    private fun configureAndShowMapFragment() {
        mapFragment = childFragmentManager.findFragmentById(R.id.fragment_estate_detail_container_map)
        if (mapFragment == null) {
            mapFragment = MapsFragment()
            childFragmentManager.beginTransaction()
                    .replace(R.id.fragment_estate_detail_container_map, mapFragment!!)
                    .commit()
        }
    }

    // -----------------
    // METHODS OVERRIDE
    // -----------------

    override fun onPrepareOptionsMenu(menu: Menu) {
        Handler().postDelayed( {
            menu.findItem(R.id.activity_main_menu_update).isVisible = true
            super.onPrepareOptionsMenu(menu)
        }, 450)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.activity_main_menu_update) {
            getEstate()?.let { (EstateRouterImpl(activity as AppCompatActivity) as EstateRouter).openManageEstate(it) }
        }

        return super.onOptionsItemSelected(item)
    }

    // --- GETTERS ---

    /**
     * Get Estate from bundle.
     */
    private fun getEstate(): Estate? = arguments?.getParcelable(ESTATE_DETAIL)

    /**
     * Get the view model instance, set if null.
     * @return the EstateViewModel instance.
     */
    override fun getViewModel(): EstateViewModel? {
        viewModel ?: run {
            viewModel = getEstate()?.let { EstateViewModel(it) }
        }
        return viewModel
    }
}