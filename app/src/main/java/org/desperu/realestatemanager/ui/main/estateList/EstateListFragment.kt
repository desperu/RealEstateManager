package org.desperu.realestatemanager.ui.main.estateList

import android.content.res.Configuration
import android.view.View
import android.view.animation.AnimationUtils
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_estate_list.view.*
import org.desperu.realestatemanager.R
import org.desperu.realestatemanager.base.BaseBindingFragment
import org.desperu.realestatemanager.databinding.FragmentEstateListBinding
import org.desperu.realestatemanager.di.ViewModelFactory
import org.desperu.realestatemanager.model.Estate
import org.desperu.realestatemanager.ui.main.MainActivity

/**
 * The argument name for bundle to received the estate from notification to this Fragment.
 */
const val ESTATE_NOTIFICATION_FOR_LIST: String = "estateNotificationForList"

/**
 * Fragment to show estate list.
 *
 * @constructor Instantiates a new EstateListFragment.
 */
class EstateListFragment: BaseBindingFragment() {

    // FOR DATA
    private lateinit var binding: FragmentEstateListBinding
    private var viewModel: EstateListViewModel? = null

    // FOR INTENT
    private val estateNotification: Estate? get() = arguments?.getParcelable(ESTATE_NOTIFICATION_FOR_LIST)

    // --------------
    // BASE METHODS
    // --------------

    override fun getBindingView(): View = configureViewModel()

    override fun configureDesign() {}

    override fun updateDesign() {
        configureRecyclerView()
    }

    // --------------
    // CONFIGURATION
    // --------------

    /**
     * Configure data binding, recycler view and view model.
     */
    private fun configureViewModel(): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_estate_list, container, false)

        viewModel = ViewModelProvider(this, ViewModelFactory(requireActivity() as MainActivity)).get(EstateListViewModel::class.java)
        // For tablet (two frames) notification click.
        if (isFrame2Visible && estateNotification != null) showEstateNotification(estateNotification!!)
        binding.viewModel = viewModel
        return binding.root
    }

    /**
     * Configure recycler view, support large screen size and use specific interface.
     */
    private fun configureRecyclerView() {
        binding.fragmentEstateListRecyclerView.layoutManager =
                if (!isFrame2Visible) GridLayoutManager(activity, columnNumber)
                else LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        val controller = AnimationUtils.loadLayoutAnimation(activity, R.anim.layout_anim_fall_down)
        binding.fragmentEstateListRecyclerView.layoutAnimation = controller
        binding.fragmentEstateListRecyclerView.scheduleLayoutAnimation()
    }

    /**
     * Column number for the grid layout, depends of the screen orientation.
     */
    private val columnNumber: Int
        get() = when (resources.configuration.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> 2
            Configuration.ORIENTATION_LANDSCAPE -> 3
            else -> 2
        }

    // --------------
    // METHODS OVERRIDE
    // --------------

    override fun onResume() {
        super.onResume()
        // When comes back from map frag with filters, restore selected item for tablet mode.
        if (isFrame2Visible) viewModel?.restoreDetailForTablet()
    }

    // --------------
    // DATA
    // --------------

    /**
     * Add or update the new managed estate.
     * @param newEstate the new estate to show in the list.
     * @return the position of the affected item in the recycler view.
     */
    internal fun addOrUpdateEstate(newEstate: Estate): Int? = viewModel?.addOrUpdateEstate(newEstate)

    /**
     * Update Estate List in view model.
     * @param newEstateList the new estate list to show.
     */
    internal fun updateEstateList(newEstateList: List<Estate>) = viewModel?.updateEstateList(newEstateList)

    /**
     * Show the estate detail for notification click in tablet mode (two frames).
     * @param estate the estate to select and show.
     */
    private fun showEstateNotification(estate: Estate) {
        viewModel?.setEstateNotification(estate)
        arguments?.remove(ESTATE_NOTIFICATION_FOR_LIST)
    }

    // --------------
    // UI
    // --------------

    /**
     * Scroll to the new item position in recycler view.
     * @param position the position to scroll in the recycler view, if null must get position.
     * @param estate the estate to scroll to it's position.
     */
    internal fun scrollToNewItem(position: Int?, estate: Estate) {
        val checkedPosition =  position ?: viewModel?.getItemPosition(estate) as Int
        view?.rootView?.fragment_estate_list_recycler_view?.layoutManager?.scrollToPosition(checkedPosition)
        viewModel?.switchSelectedItem(estate)
    }

    /**
     * True if activity main frame layout is visible, false otherwise.
     */
    private val isFrame2Visible get() = activity?.activity_main_frame_layout2 != null
}