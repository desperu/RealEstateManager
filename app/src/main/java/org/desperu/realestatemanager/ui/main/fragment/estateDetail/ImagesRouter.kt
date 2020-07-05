package org.desperu.realestatemanager.ui.main.fragment.estateDetail

import androidx.appcompat.app.AppCompatActivity
import org.desperu.realestatemanager.model.Image
import org.desperu.realestatemanager.ui.showImages.ShowImagesActivity

/**
 * The image router that allows redirection of the user.
 */
interface ImagesRouter {

    /**
     * Redirects the user to the Show Images Activity to show estate's images.
     * @param imageList the estate image list to show.
     * @param position the position of the clicked image in the list.
     */
    fun openShowImages(imageList: ArrayList<Image>, position: Int)
}

/**
 * Implementation of the Images Router.
 *
 * @property activity the Activity that is used to perform redirection.
 *
 * @constructor Instantiates a new ImagesRouter.
 *
 * @param activity the Activity that is used to perform redirection to set.
 */
class ImagesRouterImpl(private val activity: AppCompatActivity): ImagesRouter {

    /**
     * Redirects the user to the Show Images Activity to show estate's images.
     * @param imageList the estate image list to show.
     * @param position the position of the clicked image in the list.
     */
    override fun openShowImages(imageList: ArrayList<Image>, position: Int) =
            ShowImagesActivity.routeFromActivity(activity, imageList, position)
}