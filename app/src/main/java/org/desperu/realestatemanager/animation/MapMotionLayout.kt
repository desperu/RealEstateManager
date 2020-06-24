package org.desperu.realestatemanager.animation

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
class MapMotionLayout(private val context: Context, private val view: View?) {

    // Motion layout view object to perform animations.
    private val motionLayout = view?.rootView?.fragment_estate_detail_motion_layout

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
    @Suppress("deprecation")
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
     * Set needed measured constraints for the motion layout scene transition and apply them to the transition.
     * Set the final size of map view (full screen), and the translation Y to correct it's position in the scroll view.
     * And for others containers, set translation to animate them out of screen.
     */
    private fun setMeasuredConstraints() {
        val constraintEnd = motionLayout?.getConstraintSet(R.id.end)

        if (view != null) {

            // Set final size of map view (full screen size), with the first parent full screen view.
            // Because if we use MATCH_PARENT, there's ui mistakes when user interact with the map.
            val mainFrame = view.parent.parent.parent.parent as View
            constraintEnd?.constrainWidth(R.id.fragment_estate_detail_container_map, mainFrame.measuredWidth)
            constraintEnd?.constrainHeight(R.id.fragment_estate_detail_container_map, mainFrame.measuredHeight)

            // Set the map view position in the scroll view, for that the top of the map view
            // match the top of the screen fragment (the bottom of the app bar).
            val scrollPosition = view.rootView.fragment_estate_detail_scrollview.scrollY.toFloat()
            constraintEnd?.setTranslationY(R.id.fragment_estate_detail_container_map, scrollPosition)

            // To animate image container way off-screen to the top.
            val containerImagesBottom = view.rootView.fragment_estate_detail_container_images.bottom.toFloat()
            constraintEnd?.setTranslationY(R.id.fragment_estate_detail_container_images, - containerImagesBottom)

            // To animate address container way off-screen to the left.
            val containerAddressRight = view.rootView.fragment_estate_detail_container_address.right.toFloat()
            constraintEnd?.setTranslationX(R.id.fragment_estate_detail_container_address, - containerAddressRight)

            // To animate data container way off-screen to the bottom.
            val containerDataTop = view.rootView.fragment_estate_detail_container_data.top.toFloat()
            constraintEnd?.setTranslationY(R.id.fragment_estate_detail_container_data, containerDataTop)

            // To animate sale data container way off-screen to the bottom.
            val containerSaleDataTop = view.rootView.fragment_estate_detail_container_sale_data.top.toFloat()
            constraintEnd?.setTranslationY(R.id.fragment_estate_detail_container_sale_data, containerSaleDataTop)

            // Apply measured constraints to the motion layout transition.
            motionLayout?.setTransition(R.id.start, R.id.end)
        }
    }

    /**
     * Expend map view to full screen, switch scroll view height to allow full screen for child.
     */
    private fun expendMapView() = performAnimation {

        // Before animation, switch scroll view height to allow full screen for child.
        // Because if the scroll view height is lower than the device screen,
        // it's create an ui mistake when perform expend animation.
        view?.rootView?.fragment_estate_detail_scrollview?.updateLayoutParams { height = MATCH_PARENT }

        // Set needed measured constraints to the motion layout scene transition.
        setMeasuredConstraints()

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