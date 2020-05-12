package org.desperu.realestatemanager.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Handler
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.fragment_estate_detail.view.*
import kotlinx.android.synthetic.main.fragment_maps.view.*
import kotlinx.coroutines.launch
import org.desperu.realestatemanager.R
import org.desperu.realestatemanager.ui.main.MainActivity
import org.desperu.realestatemanager.utils.FULL_SIZE
import org.desperu.realestatemanager.utils.LITTLE_SIZE

/**
 * Class of Map View with Motion Layout animation.
 *
 * @param context the context from this method is called.
 * @param view the view that contains map view and motion layout.
 *
 * @constructor launch the animation to expend or collapse the map view.
 *
 * @property context the context from this method is called to set.
 * @property view the view that contains map view and motion layout to set.
 */
@Suppress("deprecation")
class MapMotionLayout(private val context: Context, private val view: View?) {

    // Motion layout view object to perform animations.
    private val motionLayout = view?.rootView?.motion_layout

    // FOR DATA
    private val button = view?.fragment_maps_fullscreen_button
    private val toFullSize = button?.tag == LITTLE_SIZE

    /**
     * Switch asked animation, expend or collapse map view, and switch button state.
     */
    internal fun switchMapSize() {
        if (toFullSize) expendMapView()
        else collapseMapView()

        switchButtonState()
    }

    /**
     * Switch button icon and tag, associated with view animation.
     */
    private fun switchButtonState() {
        val drawable: Drawable
        val tag: String

        // Switch button icon and tag value when animate view.
        if (toFullSize) { // Expend
            drawable = context.resources.getDrawable(R.drawable.ic_baseline_fullscreen_exit_black_24)
            tag = FULL_SIZE
        } else { // Collapse
            drawable = context.resources.getDrawable(R.drawable.ic_baseline_fullscreen_black_24)
            tag = LITTLE_SIZE
        }

        button?.setImageDrawable(drawable)
        button?.tag = tag
    }

    /**
     * Expend map view to full screen, switch scroll view height to allow full screen for child.
     */
    private fun expendMapView() = performAnimation {

        // Before animation, switch scroll view height to allow full screen for child.
        view?.rootView?.fragment_estate_detail_scrollview?.updateLayoutParams { height = MATCH_PARENT }

        // Start expend animation with motion layout.
        motionLayout?.transitionToState(R.id.end)
    }

    /**
     * Collapse map view to fragment of screen, restore scroll view height to allow scroll.
     */
    private fun collapseMapView() = performAnimation {

        // Start collapse animation with motion layout.
        motionLayout?.transitionToState(R.id.start)

        // At the end of animation, restore scroll view height to allow scroll.
        Handler().postDelayed( {
            view?.rootView?.fragment_estate_detail_scrollview?.updateLayoutParams { height = WRAP_CONTENT }
        }, context.resources.getInteger(R.integer.map_anim_duration).toLong())
    }

    /**
     * Convenience method to launch a coroutine in MainActivity's lifecycleScope
     * (to start animating transitions in MotionLayout).
     */
    private inline fun performAnimation(crossinline block: suspend () -> Unit) {
        (context as MainActivity).lifecycleScope.launch {
            block()
        }
    }
}