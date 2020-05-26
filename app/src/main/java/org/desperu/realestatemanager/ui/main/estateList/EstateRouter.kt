package org.desperu.realestatemanager.ui.main.estateList

import androidx.appcompat.app.AppCompatActivity
import org.desperu.realestatemanager.model.Estate
import org.desperu.realestatemanager.ui.main.FULL_ESTATE_LIST
import org.desperu.realestatemanager.ui.main.MainActivity
import org.desperu.realestatemanager.ui.manageEstate.ManageEstateActivity
import java.util.ArrayList

/**
 * The estate router that allows redirection of the user.
 */
interface EstateRouter {

    /**
     * Populate full estate list to main.
     * @param estateList the full estate list to populate.
     */
    fun populateEstateListToMain(estateList: List<Estate>)

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
     * Populate full estate list to main.
     * @param estateList the full estate list to populate.
     */
    override fun populateEstateListToMain(estateList: List<Estate>) {
        (activity as MainActivity).intent.putParcelableArrayListExtra(FULL_ESTATE_LIST, estateList as ArrayList)
    }

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
}