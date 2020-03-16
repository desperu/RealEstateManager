package org.desperu.realestatemanager.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_recycler_view.*
import org.desperu.realestatemanager.R
import org.desperu.realestatemanager.databinding.FragmentRecyclerViewBinding
import org.desperu.realestatemanager.injection.ViewModelFactory
import org.desperu.realestatemanager.ui.EstateListViewModel

class EstateListFragment: Fragment() {

    // FOR DATA
    private lateinit var binding: FragmentRecyclerViewBinding
    private lateinit var viewModel: EstateListViewModel

    // --------------
    // BASE METHODS
    // --------------

//    override fun getFragmentLayout(): Int { return R.layout.fragment_recycler_view }
//
//    override fun configureDesign() {
//        configureViewModel()
//    }


    fun newInstance(): EstateListFragment { return EstateListFragment() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        configureViewModel(inflater, container)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        configureSwipeRefresh()
    }

    // --------------
    // CONFIGURATION
    // --------------

    /**
     * Configure data binding, recycler view and view model.
     */
    private fun configureViewModel(inflater: LayoutInflater, container: ViewGroup?) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_recycler_view, container, false)
        binding.fragmentRecyclerViewRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        viewModel = ViewModelProviders.of(this, ViewModelFactory(requireActivity() as MainActivity)).get(EstateListViewModel::class.java)
        binding.viewModel = viewModel
    }

    /**
     * Configure swipe to refresh layout.
     */
    private fun configureSwipeRefresh() {
        fragment_recycler_view_swipe_refresh.setOnRefreshListener { viewModel.reloadEstateList() }
    }
}