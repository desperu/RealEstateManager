package org.desperu.realestatemanager.ui.manageEstate.fragment

import androidx.appcompat.app.AppCompatActivity
import org.desperu.realestatemanager.ui.manageEstate.Communication

/**
 * The interface that allows communication for ManageEstateViewModel.
 */
interface ManageEstateVMCommunication {

    /**
     * Used to allow view model to update ui when an image item moved or is added in recycler,
     * so send the new item position to the fragment witch manage recycler.
     * @param position the position to scroll to.
     */
    fun scrollToNewItem(position: Int)

    /**
     * Used to allow view model to delete an image in storage.
     * So send the image uri to the fragment witch manage images.
     * @param imageUri the uri of the image to delete in storage.
     */
    fun deleteImageInStorage(imageUri: String)

    /**
     * Manage floating buttons visibility, setup with recycler scrolling.
     * @param toHide if true hide buttons, else show.
     */
    fun floatingVisibility(toHide: Boolean)
}

/**
 * Implementation of the ManageEstateVMCommunication.
 *
 * @property activity the Activity that is used to perform communication.
 *
 * @constructor Instantiates a new ManageEstateVMCommunicationImpl.
 *
 * @param activity the Activity that is used to perform communication to set.
 */
class ManageEstateVMCommunicationImpl(private val activity: AppCompatActivity): ManageEstateVMCommunication {

    /**
     * Get the current fragment of the activity from the view pager.
     */
    private fun getCurrentFragment() =
            (activity as Communication).getCurrentViewPagerFragment()

    /**
     * Used to allow view model to update ui when an image item moved or is added in recycler,
     * so send the new item position to the fragment witch manage recycler.
     * @param position the position to scroll to.
     */
    override fun scrollToNewItem(position: Int) =
            getCurrentFragment().scrollToNewItem(position)

    /**
     * Used to allow view model to delete an image in storage.
     * So send the image uri to the fragment witch manage images.
     * @param imageUri the uri of the image to delete in storage.
     */
    override fun deleteImageInStorage(imageUri: String) =
            getCurrentFragment().deleteImageInStorage(imageUri)

    /**
     * Manage floating buttons visibility, setup with recycler scrolling.
     * @param toHide if true hide buttons, else show.
     */
    override fun floatingVisibility(toHide: Boolean) = (activity as Communication).floatingVisibility(toHide)
}