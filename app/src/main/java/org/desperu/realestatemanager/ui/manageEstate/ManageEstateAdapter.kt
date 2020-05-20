package org.desperu.realestatemanager.ui.manageEstate

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import org.desperu.realestatemanager.R
import org.desperu.realestatemanager.ui.manageEstate.fragment.ManageEstateFragment
import org.desperu.realestatemanager.utils.numberOfPage

class ManageEstateAdapter(mContext: Context,
                          fm: FragmentManager?,
                          behavior: Int) : FragmentPagerAdapter(fm!!, behavior) {

    private val tabTitles = mContext.resources.getStringArray(R.array.tab_titles)

    override fun getCount(): Int = numberOfPage

    override fun getItem(position: Int): Fragment = ManageEstateFragment.newInstance(position)

    override fun getPageTitle(position: Int): CharSequence? = tabTitles[position]
}