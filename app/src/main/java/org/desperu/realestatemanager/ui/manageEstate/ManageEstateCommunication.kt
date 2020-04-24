package org.desperu.realestatemanager.ui.manageEstate

import androidx.appcompat.app.AppCompatActivity

/**
 * The interface that allows communication for ManageEstateViewModel.
 */
interface ManageEstateCommunication {

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
}

/**
 * Implementation of the ManageEstateCommunication.
 *
 * @property activity the Activity that is used to perform communication.
 *
 * @constructor Instantiates a new ManageEstateCommunicationImpl.
 *
 * @param activity the Activity that is used to perform communication to set.
 */
class ManageEstateCommunicationImpl(private val activity: AppCompatActivity): ManageEstateCommunication {

    /**
     * Get the current fragment of the activity from the view pager.
     */
    private fun getCurrentFragment() = (activity as ManageEstateActivity)
            .getCurrentViewPagerFragment() as ManageEstateFragment

    /**
     * Used to allow view model to update ui when an image item moved or is added in recycler,
     * so send the new item position to the fragment witch manage recycler.
     * @param position the position to scroll to.
     */
    override fun scrollToNewItem(position: Int) {
        getCurrentFragment().scrollToNewItem(position)
    }

    /**
     * Used to allow view model to delete an image in storage.
     * So send the image uri to the fragment witch manage images.
     * @param imageUri the uri of the image to delete in storage.
     */
    override fun deleteImageInStorage(imageUri: String) {
        getCurrentFragment().deleteImageInStorage(imageUri)
    }
}