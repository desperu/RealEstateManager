package org.desperu.realestatemanager.ui.main.estateList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_estate_list.*
import kotlinx.android.synthetic.main.fragment_estate_list.view.*
import org.desperu.realestatemanager.R
import org.desperu.realestatemanager.base.BaseBindingFragment
import org.desperu.realestatemanager.databinding.FragmentEstateListBinding
import org.desperu.realestatemanager.di.ViewModelFactory
import org.desperu.realestatemanager.ui.main.MainActivity
import org.desperu.realestatemanager.view.enableSwipe

/**
 * Fragment to show estate list.
 * @constructor Instantiates a new EstateListFragment.
 */
class EstateListFragment: BaseBindingFragment() {

    // FOR DATA
    private lateinit var binding: FragmentEstateListBinding
    private var viewModel: EstateListViewModel? = null

    // --------------
    // BASE METHODS
    // --------------

    override fun getBindingView(): View = configureViewModel(inflater, container)

    override fun configureDesign() {}

    override fun updateDesign() {
        configureSwipeRefresh()
        configureSwipeToDeleteForRecycler()
    }

    // --------------
    // CONFIGURATION
    // --------------

    /**
     * Configure data binding, recycler view and view model.
     */
    private fun configureViewModel(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_estate_list, container, false)
        binding.fragmentEstateListRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        viewModel = ViewModelProvider(this, ViewModelFactory(requireActivity() as MainActivity)).get(EstateListViewModel::class.java)
        binding.viewModel = viewModel
        return binding.root
    }

    /**
     * Configure swipe to refresh layout.
     */
    private fun configureSwipeRefresh() {
        fragment_estate_list_swipe_refresh.setOnRefreshListener { viewModel?.reloadEstateList() }
    }

    /**
     * Configure swipe to delete gesture for recycler view.
     */
    private fun configureSwipeToDeleteForRecycler() {
        @Suppress("UNCHECKED_CAST")
        viewModel?.getEstateListAdapter?.let {
            enableSwipe(this, it, viewModel?.getEstateVMList as MutableList<Any>)
                    .attachToRecyclerView(fragment_estate_list_recycler_view)
        }
    }

    // --------------
    // UI
    // --------------

    /**
     * Scroll to the new item position of recycler view.
     * @param position the position to scroll in the recycler view.
     */
    internal fun scrollToNewItem(position: Int) {
        view?.rootView?.fragment_estate_list_recycler_view?.layoutManager?.scrollToPosition(position)
    }

    // --- GETTERS ---

    internal fun getViewModel(): EstateListViewModel = viewModel as EstateListViewModel
}