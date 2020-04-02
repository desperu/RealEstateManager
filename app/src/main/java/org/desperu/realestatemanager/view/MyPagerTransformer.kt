package org.desperu.realestatemanager.view

import android.view.View
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.activity_manage_estate.view.*
import org.desperu.realestatemanager.utils.ESTATE_IMAGE

class MyPagerTransformer(private val viewPager: ViewPager): ViewPager.PageTransformer {

    override fun transformPage(page: View, position: Float) {
        val pageWidth: Int = page.width
        val addEstate = viewPager.rootView.activity_manage_estate_floating_add_estate
        val addImage = viewPager.rootView.activity_manage_estate_floating_add_image

        when {
            position < -1 -> { // [-Infinity,-1[
                // This page is way off-screen to the left.
                page.alpha = 0F
            }

            position >= -1 && position < -0.1 -> { // [-1,-0.1[
                page.alpha = 1F
                addEstate.rotation = (1 + position) * pageWidth
                addEstate.translationX = ((1 + position) * 0.5 * pageWidth).toFloat()
                addEstate.translationY = ((1 + position) * 0.5 * pageWidth).toFloat()
                if (viewPager.currentItem == ESTATE_IMAGE) {
                    addImage.visibility = View.VISIBLE
                    addImage.rotation = (1 + position) * pageWidth
                    addImage.translationX = (-(1 + position) * 0.5 * pageWidth).toFloat()
                    addImage.translationY = ((1 + position) * 0.5 * pageWidth).toFloat()
                } else addImage?.visibility = View.GONE
            }

            position >= -0.1 && position <= 0.1 -> { // [-0.1,0.1]
                page.alpha = 1F
            }

            position > 0.1 && position <= 1 -> { // ]0.1,1]
                page.alpha = 1F
                addEstate.rotation = (1 - position) * pageWidth
                addEstate.translationX = ((1 - position) * 0.5 * pageWidth).toFloat()
                addEstate.translationY = ((1 - position) * 0.5 * pageWidth).toFloat()
                if (viewPager.currentItem == ESTATE_IMAGE) {
                    addImage.visibility = View.VISIBLE
                    addImage.rotation = (1 - position) * pageWidth
                    addImage.translationX = (-(1 - position) * 0.5 * pageWidth).toFloat()
                    addImage.translationY = ((1 - position) * 0.5 * pageWidth).toFloat()
                } else addImage?.visibility = View.GONE
            }

            else -> { // ]1,+Infinity]
                // This page is way off-screen to the right.
                page.alpha = 0F
            }
        }
    }
}