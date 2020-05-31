package org.desperu.realestatemanager.view

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatTextView
import org.desperu.realestatemanager.R


/**
 * Custom text view to intercept save and be able to retrieved it's background drawable id.
 *
 * @param context the context from this CustomTextView is instantiate.
 * @param attrs the attribute set to apply at this view.
 * @param defStyle the default style to apply at this view.
 *
 * @constructor Instantiate a new CustomTextView.
 */
class CustomTextView @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null, defStyle: Int = 0)
    : AppCompatTextView(context, attrs, defStyle) {

    private var currentDrawableId: Int? = null

    init {
        // Get the TypedArray of the custom view.
        val a = context!!.obtainStyledAttributes(attrs, R.styleable.CustomTextView)

        // Get the value of the custom attribute "myBackground".
        val resId = a.getResourceId(R.styleable.CustomTextView_myBackground, 0)

        // Set the background of the view.
        setBackgroundResource(resId)

        // Recycle the attribute for future uses.
        a.recycle()
    }

    override fun setBackgroundResource(@DrawableRes resId: Int) {
        super.setBackgroundResource(resId)

        // Save the drawable resource id value.
        currentDrawableId = resId
    }

    /**
     * Get current drawable resource id of the view.
     * @return the resource id.
     */
    fun getDrawableId(): Int {
        return currentDrawableId ?: 0
    }

    /**
     * Compare the current drawable resource id with the given drawable resource id.
     * @param toDrawableId the drawable resource id to compare with.
     * @return true if ids are the same, false otherwise.
     */
    fun compareDrawableTo(toDrawableId: Int?): Boolean {
        if (toDrawableId == null || currentDrawableId != toDrawableId) {
            return false
        }

        return true
    }
}