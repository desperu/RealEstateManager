package org.desperu.realestatemanager.ui.main

import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import org.desperu.realestatemanager.R
import org.desperu.realestatemanager.base.BaseBindingFragment
import org.desperu.realestatemanager.databinding.FragmentEstateDetailBinding
import org.desperu.realestatemanager.model.Estate

/**
 * The name of the argument for passing estate to this Fragment.
 */
const val ESTATE_DETAIL: String = "estateDetail"

/**
 * Fragment to show estate details.
 */
class EstateDetailFragment: BaseBindingFragment() {

    // FOR DATA
    private lateinit var binding: FragmentEstateDetailBinding
    private lateinit var viewModel: EstateViewModel
    private var mapFragment: Fragment? = null

    // --------------
    // BASE METHODS
    // --------------

    override fun getBindingView(): View = configureViewModel()

    override fun configureDesign() {
        configureMapFragment()
    }

    override fun updateDesign() {}

    // -----------------
    // CONFIGURATION
    // -----------------

    /**
     * Get Estate from bundle.
     */
    private fun getEstate(): Estate? = arguments?.getParcelable(ESTATE_DETAIL)

    /**
     * Configure data binding with view model.
     */
    private fun configureViewModel(): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_estate_detail, container, false)
        viewModel = getEstate()?.let { EstateViewModel(it) }!!

        binding.viewModel = viewModel
//        configureImageRecycler()
        return binding.root
    }

    /**
     * Configure and show map fragment.
     */
    private fun configureMapFragment() {
        if (mapFragment == null) {
            mapFragment = childFragmentManager.findFragmentById(R.id.map)
            mapFragment = MapsFragment()
            childFragmentManager.beginTransaction()
                    .add(R.id.map, mapFragment!!)
                    .commit()
        }
    }
}