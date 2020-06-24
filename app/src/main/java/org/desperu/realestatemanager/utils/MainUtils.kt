package org.desperu.realestatemanager.utils

import android.content.Context
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import org.desperu.realestatemanager.R
import org.desperu.realestatemanager.ui.main.estateDetail.EstateDetailFragment
import org.desperu.realestatemanager.ui.main.estateList.EstateListFragment
import org.desperu.realestatemanager.ui.main.estateMap.MapsFragment

/**
 * Main utils object witch provide utils for main activity.
 */
object MainUtils {

    // -----------------
    // FRAME
    // -----------------

    /**
     * Return the unique identifier of the corresponding frame layout.
     * @param fragmentKey the asked fragment key.
     * @param isFrame2Visible true if frame 2 is not null.
     * @return the corresponding frame layout.
     */
    internal fun getFrame(fragmentKey: Int, isFrame2Visible: Boolean) =
            if (fragmentKey != FRAG_ESTATE_LIST && isFrame2Visible)
                R.id.activity_main_frame_layout2
            else
                R.id.activity_main_frame_layout

    /**
     * Switch frame list size for tablet mode. If the device is a tablet and asked fragment is maps,
     * collapse list frame, else set the list frame to original size.
     * @param frame the frame to switch size.
     * @param fragmentKey the asked fragment key.
     * @param isFrame2Visible true if frame 2 is not null.
     */
    internal fun switchFrameSizeForTablet(frame: FrameLayout, fragmentKey: Int, isFrame2Visible: Boolean) {
        if (isFrame2Visible)
            frame.updateLayoutParams<LinearLayout.LayoutParams> {
                weight = if (fragmentKey == FRAG_ESTATE_MAP) 0F else 1F
            }
    }

    // -----------------
    // FRAGMENT
    // -----------------

    /**
     * Get the associated fragment class with the given fragment key.
     * @param fragmentKey the given fragment key from witch get the key.
     * @return the corresponding fragment class.
     */
    @Suppress("unchecked_cast")
    internal fun <T: Fragment> getFragClassFromKey(fragmentKey: Int): Class<T> = when (fragmentKey) {
        FRAG_ESTATE_LIST -> EstateListFragment::class.java as Class<T>
        FRAG_ESTATE_MAP -> MapsFragment::class.java as Class<T>
        FRAG_ESTATE_DETAIL -> EstateDetailFragment::class.java as Class<T>
        else -> throw IllegalArgumentException("Fragment key not found : $fragmentKey")
    }

    /**
     * Retrieve the associated fragment key with the fragment class.
     * @param fragClass the given fragment class from witch retrieved the key.
     * @return the corresponding fragment key.
     */
    internal fun <T: Fragment> retrievedFragKeyFromClass(fragClass: Class<T>) = when (fragClass) {
        EstateListFragment::class.java -> FRAG_ESTATE_LIST
        MapsFragment::class.java -> FRAG_ESTATE_MAP
        EstateDetailFragment::class.java -> FRAG_ESTATE_DETAIL
        else -> throw IllegalArgumentException("Fragment class not found : ${fragClass.simpleName}")
    }

    // -----------------
    // UI
    // -----------------

    /**
     * Set the title activity name for the asked fragment.
     * @param context the context from this function is called.
     * @param fragmentKey the key of the asked fragment.
     * @param isFrame2Visible true if frame 2 is not null.
     */
    internal fun setTitleActivity(context: Context, fragmentKey: Int, isFrame2Visible: Boolean) {
        val activity = (context as AppCompatActivity)
        activity.title = activity.getString(when {
            fragmentKey == FRAG_ESTATE_MAP -> R.string.fragment_maps_name
            fragmentKey == FRAG_ESTATE_DETAIL && !isFrame2Visible-> R.string.fragment_estate_detail_name
            else -> R.string.app_name
        })
    }
}