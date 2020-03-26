package org.desperu.realestatemanager.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_estate_list.*
import org.desperu.realestatemanager.R
import org.desperu.realestatemanager.base.BaseBindingFragment
import org.desperu.realestatemanager.databinding.FragmentEstateListBinding
import org.desperu.realestatemanager.injection.ViewModelFactory

class EstateListFragment: BaseBindingFragment() {

    // FOR DATA
    private lateinit var binding: FragmentEstateListBinding
    private lateinit var viewModel: EstateListViewModel

    // --------------
    // BASE METHODS
    // --------------

    override fun getBindingView(): View = configureViewModel(inflater, container)

    override fun configureDesign() {}

    override fun updateDesign() {
        configureSwipeRefresh()
    }


    // --------------
    // CONFIGURATION
    // --------------

    /**
     * Configure data binding, recycler view and view model.
     */
    private fun configureViewModel(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_estate_list, container, false)
        binding.fragmentRecyclerViewRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        viewModel = ViewModelProviders.of(this, ViewModelFactory(requireActivity() as MainActivity)).get(EstateListViewModel::class.java)
        binding.viewModel = viewModel
        return binding.root
    }

    /**
     * Configure swipe to refresh layout.
     */
    private fun configureSwipeRefresh() {
        fragment_recycler_view_swipe_refresh.setOnRefreshListener { viewModel.reloadEstateList() }
    }
}