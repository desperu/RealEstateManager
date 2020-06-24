package org.desperu.realestatemanager.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewParent
import com.google.android.gms.maps.MapView

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
        when (event.action) {

            MotionEvent.ACTION_DOWN -> if (null == mViewParent) {
                parent.requestDisallowInterceptTouchEvent(true)
            } else {
                mViewParent!!.requestDisallowInterceptTouchEvent(true)
            }

            MotionEvent.ACTION_UP -> if (null == mViewParent) {
                parent.requestDisallowInterceptTouchEvent(false)
            } else {
                mViewParent!!.requestDisallowInterceptTouchEvent(false)
            }

            else -> {}
        }
        return super.onInterceptTouchEvent(event)
    }
}