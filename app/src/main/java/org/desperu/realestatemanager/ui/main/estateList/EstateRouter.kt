package org.desperu.realestatemanager.ui.main.estateList

import androidx.appcompat.app.AppCompatActivity
import org.desperu.realestatemanager.model.Estate
import org.desperu.realestatemanager.ui.main.MainCommunication
import org.desperu.realestatemanager.ui.manageEstate.ManageEstateActivity

/**
 * The estate router that allows redirection of the user.
 */
interface EstateRouter {

    /**
     * Redirects the user to the EstateDetail Fragment to show estate details.
     * @param estate the estate to show details in the EstateDetail Fragment.
     * @param isUpdate true if is call for an update, false for first launching data.
     */
    fun openEstateDetail(estate: Estate, isUpdate: Boolean)

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
     * Redirects the user to the EstateDetail Fragment to show estate details.
     * @param estate the estate to show details in the EstateDetail Fragment.
     * @param isUpdate true if is call for an update, false for first launching data.
     */
    override fun openEstateDetail(estate: Estate, isUpdate: Boolean) =
            (activity as MainCommunication).showEstateDetail(estate, isUpdate)

    /**
     * Redirects the user to the ManageEstate Activity to manage estate.
     * @param estate the estate to manage in the ManageEstate Activity.
     */
    override fun openManageEstate(estate: Estate) =
            ManageEstateActivity.routeFromActivity(activity, estate)
}