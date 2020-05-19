package org.desperu.realestatemanager.ui.showImages

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import org.desperu.realestatemanager.model.Image

class ViewPagerAdapter(fm: FragmentManager,
                       behavior: Int) : FragmentPagerAdapter(fm, behavior) {

    private lateinit var imageList: List<Image>

    override fun getCount(): Int = if (::imageList.isInitialized) imageList.size else 0

    override fun getItem(position: Int): Fragment = ShowImageFragment.newInstance(imageList[position])

    /**
     * Update all item list.
     * @param newImageList the new image list to set.
     */
    internal fun updateImageList(newImageList: List<Image>) { imageList = newImageList }
}