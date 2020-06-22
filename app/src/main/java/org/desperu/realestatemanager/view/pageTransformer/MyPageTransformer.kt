package org.desperu.realestatemanager.view.pageTransformer

import android.view.View
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.activity_manage_estate.view.*
import org.desperu.realestatemanager.utils.ESTATE_IMAGE

/**
 * Rotation constant used to perform animation.
 */
private const val ROTATION = 1080

/**
 * Custom Page Transformer used to rotate fab buttons synchronously with page transition.
 *
 * @param viewPager the viewPager instance witch used this page transformer.
 *
 * @constructor Instantiates a new MyPageTransformer.
 *
 * @property viewPager the viewPager instance witch used this page transformer to set.
 */
class MyPageTransformer(private val viewPager: ViewPager): ViewPager.PageTransformer {

    override fun transformPage(page: View, position: Float) {
        val addEstate = viewPager.rootView.activity_manage_estate_floating_add_estate
        val addImage = viewPager.rootView.activity_manage_estate_floating_add_image

        when {
            position < -1 -> { // [-Infinity,-1[
                // This page is way off-screen to the left.
                page.alpha = 0F
            }

            position <= 1 -> { // [-1, 1]
                // This page is shown in screen.
                page.alpha = 1F

                if (!addEstate.isShown) addEstate.show()
                addEstate.rotation = (1 - position) * ROTATION

                if (!addImage.isShown) addImage.show()
                addImage.rotation = (1 - position) * ROTATION
                addImage.visibility = if (viewPager.currentItem == ESTATE_IMAGE) View.VISIBLE else View.GONE
            }

            else -> { // ]1,+Infinity]
                // This page is way off-screen to the right.
                page.alpha = 0F
            }
        }
    }
}