package org.desperu.realestatemanager.ui.showImages

import android.content.Intent
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.activity_show_images.*
import org.desperu.realestatemanager.R
import org.desperu.realestatemanager.base.BaseActivity
import org.desperu.realestatemanager.model.Image
import org.desperu.realestatemanager.view.DepthPageTransformer


/**
 * The name of the argument to received image list for this Activity.
 */
const val IMAGE_LIST: String = "imageList"

/**
 * The name of the argument to received the position of the clicked image in the list.
 */
const val POSITION: String = "position"

/**
 * Activity to show estate's images.
 * @constructor Instantiates a new ShowImagesActivity.
 */
class ShowImagesActivity: BaseActivity() {

    // FROM BUNDLE
    private val imageList: List<Image>? get() = intent.getParcelableArrayListExtra(IMAGE_LIST)
    private val position: Int? get() = intent.getIntExtra(POSITION, 0)

    // FOR DATA
    private lateinit var viewPager: ViewPager
    private lateinit var mAdapter: ViewPagerAdapter

    /**
     * Companion object, used to redirect to this Activity.
     */
    companion object {
        /**
         * Redirects from an Activity to this Activity.
         * @param activity the activity use to perform redirection.
         * @param imageList the images list to show in this activity.
         * @param position the position of the clicked image in the list.
         */
        fun routeFromActivity(activity: AppCompatActivity, imageList: ArrayList<Image>, position: Int) {
            activity.startActivity(Intent(activity, ShowImagesActivity::class.java)
                    .putParcelableArrayListExtra(IMAGE_LIST, imageList)
                    .putExtra(POSITION, position)
            )
        }
    }

    // --------------
    // BASE METHODS
    // --------------

    override fun getActivityLayout(): Int = R.layout.activity_show_images

    override fun configureDesign() {
        drawBelowStatusBar()
        configureToolBar()
        configureUpButton()
        configureViewPager()
        updateViewPager()
    }

    // -----------------
    // CONFIGURATION
    // -----------------

    /**
     * For design, draw below status bar.
     */
    private fun drawBelowStatusBar() {
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
    }

    /**
     * Configure View pager with Depth Page Transformer.
     */
    private fun configureViewPager() {
        viewPager = activity_show_images_view_pager

        mAdapter = ViewPagerAdapter(supportFragmentManager,
                FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT)
        viewPager.adapter = mAdapter
        viewPager.setPageTransformer(true, DepthPageTransformer())


    }

    // -----------------
    // UI
    // -----------------

    /**
     * Update view pager data, and set current item.
     */
    private fun updateViewPager() {
        imageList?.let { mAdapter.updateImageList(it) }
        mAdapter.notifyDataSetChanged()
        position?.let { viewPager.currentItem = it }
    }

    /**
     * Show toolbar when click on fragment.
     * @param v the clicked view.
     */
    fun showToolbar(v: View) {
        activity_show_images_toolbar.visibility = View.VISIBLE
        Handler().postDelayed( { activity_show_images_toolbar.visibility = View.INVISIBLE }, 3000)
    }
}