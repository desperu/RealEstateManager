package org.desperu.realestatemanager.ui.main.estateList

import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.desperu.realestatemanager.model.Estate
import org.desperu.realestatemanager.ui.main.FULL_ESTATE_LIST
import org.desperu.realestatemanager.ui.main.MainActivity
import org.desperu.realestatemanager.ui.main.MainCommunication
import org.desperu.realestatemanager.ui.manageEstate.ManageEstateActivity
import java.util.ArrayList

/**
 * The estate router that allows redirection of the user.
 */
interface EstateRouter {

    /**
     * True if activity main frame layout 2 is visible, false otherwise.
     */
    fun isFrame2Visible(): Boolean

    /**
     * Populate full estate list to main.
     * @param estateList the full estate list to populate.
     */
    fun populateEstateListToMain(estateList: List<Estate>)

    /**
     * Show first estate of the list if the device is a tablet.
     * @param estate the estate to show details.
     * @param isUpdate true if is call for an update, false for first launching data.
     */
    fun showDetailForTablet(estate: Estate, isUpdate: Boolean)

    /**
     * Redirects the user to the EstateDetail Fragment to show estate details.
     * @param estate the estate to show details in the EstateDetail Fragment.
     */
    fun openEstateDetail(estate: Estate)

    /**
     * Redirects the user to the ManageEstate Activity to manage estate.
     * @param estate the estate to manage in the ManageEstate Activity.
     */
    fun openManageEstate(estate: Estate)

    /**
     * Used to allow view model to update estate list.
     * @param estateList the new estate list to set.
     */
    fun updateEstateList(estateList: List<Estate>)
}

/**
 * Implementation of the EstateRouter.
 *
 * @property activity the Activity that is used to perform redirection.
 *
 * @constructor Instantiates a new EstateRouterImpl.
 *
 * @param activity the Activity that is used to perform redirection to set.
 */
class EstateRouterImpl(private val activity: AppCompatActivity): EstateRouter {

    /**
     * True if activity main frame layout 2 is visible, false otherwise.
     */
    override fun isFrame2Visible() = activity.activity_main_frame_layout2 != null

    /**
     * Populate full estate list to main.
     * @param estateList the full estate list to populate.
     */
    override fun populateEstateListToMain(estateList: List<Estate>) {
        (activity as MainActivity).intent.putParcelableArrayListExtra(FULL_ESTATE_LIST, estateList as ArrayList)
    }

    /**
     * Show first estate of the list if the device is a tablet.
     * @param estate the estate to show details.
     * @param isUpdate true if is call for an update, false for first launching data.
     */
    override fun showDetailForTablet(estate: Estate, isUpdate: Boolean) =
            (activity as MainActivity).showDetailForTablet(estate, isUpdate)

    /**
     * Redirects the user to the EstateDetail Fragment to show estate details.
     * @param estate the estate to show details in the EstateDetail Fragment.
     */
    override fun openEstateDetail(estate: Estate) =
            (activity as MainActivity).showEstateDetailFragment(estate)

    /**
     * Redirects the user to the ManageEstate Activity to manage estate.
     * @param estate the estate to manage in the ManageEstate Activity.
     */
    override fun openManageEstate(estate: Estate) =
            ManageEstateActivity.routeFromActivity(activity, estate)

    /**
     * Used to allow view model to update estate list.
     * @param estateList the new estate list to set.
     */
    override fun updateEstateList(estateList: List<Estate>) =
            (activity as MainCommunication).updateEstateList(estateList, false)
}