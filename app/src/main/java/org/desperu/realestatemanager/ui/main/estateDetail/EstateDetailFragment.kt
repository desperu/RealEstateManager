package org.desperu.realestatemanager.ui.main.estateDetail

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL
import org.desperu.realestatemanager.R
import org.desperu.realestatemanager.base.BaseBindingFragment
import org.desperu.realestatemanager.databinding.FragmentEstateDetailBinding
import org.desperu.realestatemanager.di.ViewModelFactory
import org.desperu.realestatemanager.model.Estate
import org.desperu.realestatemanager.ui.main.estateList.EstateRouter
import org.desperu.realestatemanager.ui.main.estateList.EstateRouterImpl
import org.desperu.realestatemanager.ui.main.estateMap.ESTATE_MAP
import org.desperu.realestatemanager.ui.main.estateMap.MAP_MODE
import org.desperu.realestatemanager.ui.main.estateMap.MapsFragment
import org.desperu.realestatemanager.utils.LITTLE_MODE


/**
 * The argument name for bundle, to received estate in this Fragment.
 */
const val ESTATE_DETAIL: String = "estateDetail"

/**
 * Fragment to show estate details.
 * @constructor Instantiates a new EstateDetailFragment.
 */
class EstateDetailFragment: BaseBindingFragment() {

    // FROM BUNDLE
    private val estate: Estate? get() = arguments?.getParcelable(ESTATE_DETAIL)

    // FOR DATA
    private lateinit var binding: FragmentEstateDetailBinding
    private var viewModel: EstateDetailViewModel? = null
    private var mapsFragment: Fragment? = null

    // --------------
    // BASE METHODS
    // --------------

    override fun getBindingView(): View = configureViewModel()

    override fun configureDesign() {
        setHasOptionsMenu(true) // Enable option menu for this fragment.
    }

    override fun updateDesign() {
        configureImageRecycler()
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
        viewModel = ViewModelProvider(this, ViewModelFactory(activity as AppCompatActivity)).get(EstateDetailViewModel::class.java)
        estate?.let { viewModel?.setEstate(it) }

        binding.viewModel = viewModel
        return binding.root
    }

    /**
     * Configure images recycler view.
     */
    private fun configureImageRecycler() {
        binding.fragmentEstateDetailRecyclerImages.layoutManager = LinearLayoutManager(context, HORIZONTAL, false)
    }

    /**
     * Configure and show maps fragment if google play services are available.
     */
    private fun configureAndShowMapFragment() {
        mapsFragment = childFragmentManager.findFragmentById(R.id.fragment_estate_detail_container_map)
        if (mapsFragment == null) {
            mapsFragment = MapsFragment()
            setMapsFragmentBundle()
            childFragmentManager.beginTransaction()
                    .replace(R.id.fragment_estate_detail_container_map, mapsFragment!!)
                    .commit()
        }
    }

    /**
     * Set Maps Fragment Bundle to send data, populate estate and set the map mode.
     */
    private fun setMapsFragmentBundle() {
        val bundle = Bundle()
        bundle.putParcelable(ESTATE_MAP, estate)
        bundle.putInt(MAP_MODE, LITTLE_MODE)
        mapsFragment?.arguments = bundle
    }

    // -----------------
    // METHODS OVERRIDE
    // -----------------

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.activity_main_menu_update).isVisible = true
        menu.findItem(R.id.activity_main_menu_search).isVisible = false
        super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.activity_main_menu_update) {
            estate?.let { (EstateRouterImpl(activity as AppCompatActivity) as EstateRouter).openManageEstate(it) }
        }

        return super.onOptionsItemSelected(item)
    }

    // --- GETTERS ---

    /**
     * Get the estate detail view model instance.
     */
    internal val getViewModel: EstateDetailViewModel by lazy { viewModel as EstateDetailViewModel }
}