package org.desperu.realestatemanager.ui.main.estateDetail

import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import org.desperu.realestatemanager.R
import org.desperu.realestatemanager.base.BaseBindingFragment
import org.desperu.realestatemanager.databinding.FragmentEstateDetailBinding
import org.desperu.realestatemanager.model.Estate
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

    override fun configureDesign() {}

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
        mapFragment = childFragmentManager.findFragmentById(R.id.container_map)
        if (mapFragment == null) {
            mapFragment = MapsFragment()
            childFragmentManager.beginTransaction()
                    .add(R.id.container_map, mapFragment!!)
                    .commit()
        }
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