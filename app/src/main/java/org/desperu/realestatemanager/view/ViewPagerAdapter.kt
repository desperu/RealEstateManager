package org.desperu.realestatemanager.view

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import org.desperu.realestatemanager.R
import org.desperu.realestatemanager.ui.manageEstate.EstateDataFragment
import org.desperu.realestatemanager.ui.manageEstate.ManageEstateViewModel
import org.desperu.realestatemanager.utils.numberOfPage

class ViewPagerAdapter(mContext: Context,
                       fm: FragmentManager?,
                       behavior: Int,
                       private val viewModel: ManageEstateViewModel) : FragmentPagerAdapter(fm!!, behavior) {

    private val tabTitles = mContext.resources.getStringArray(R.array.tab_titles)

    override fun getCount(): Int = numberOfPage

    override fun getItem(position: Int): Fragment {
        return when(position) {
            0 -> EstateDataFragment(viewModel)
            1 -> EstateDataFragment(viewModel)
            2 -> EstateDataFragment(viewModel)
            3 -> EstateDataFragment(viewModel)
            else -> EstateDataFragment(viewModel)
        }
    }

    override fun getPageTitle(position: Int): CharSequence? = tabTitles[position]
}