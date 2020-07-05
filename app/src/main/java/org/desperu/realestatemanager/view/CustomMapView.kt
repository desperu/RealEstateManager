package org.desperu.realestatemanager.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewParent
import com.google.android.gms.maps.MapView
import kotlinx.android.synthetic.main.fragment_estate_detail.view.*

/**
 * Custom map view to intercept touch event when it's in scroll view, and consume touch event
 * when interact with it.
 *
 * @param context the context from this CustomMapView is instantiate.
 * @param attrs the attribute set to apply at this view.
 * @param defStyle the default style to apply at this view.
 *
 * @constructor Instantiate a new CustomMapView.
 */
class CustomMapView @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null, defStyle: Int = 0)
    : MapView(context, attrs, defStyle) {

    private var mViewParent: ViewParent? = null

    /**
     * Set the view parent of this custom map view.
     * @param viewParent the view parent of this view to set.
     */
    internal fun setViewParent(viewParent: ViewParent?) { //any ViewGroup
        mViewParent = viewParent
    }

    /**
     * Intercept on touch event to disable parent consumption, and give it to the map view.
     * @param event the motion event to intercept.
     * @return Return the super function returned value.
     */
    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        val isScrollChild = rootView.fragment_estate_detail_scrollview != null

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                // To allow open menu drawer when not scroll child.
                val toIntercept = if (!isScrollChild) event.rawX < 1f else true
                dispatchTouchEvent(toIntercept)
            }
            MotionEvent.ACTION_UP -> dispatchTouchEvent(false)
            else -> {}
        }
        return super.onInterceptTouchEvent(event)
    }

    /**
     * Dispatch intercepted touch event action.
     * @param toIntercept true if this map view must consume this event, false otherwise.
     */
    private fun dispatchTouchEvent(toIntercept: Boolean) =
            if (null == mViewParent)
                parent.requestDisallowInterceptTouchEvent(toIntercept)
            else
                mViewParent!!.requestDisallowInterceptTouchEvent(toIntercept)
}