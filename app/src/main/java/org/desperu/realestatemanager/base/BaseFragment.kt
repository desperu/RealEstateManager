package org.desperu.realestatemanager.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import butterknife.ButterKnife
import icepick.Icepick


abstract class BaseFragment: Fragment() {

    // --------------
    // BASE METHODS
    // --------------

    protected abstract fun getFragmentLayout(): Int
    protected abstract fun configureDesign()

    // -----------------
    // METHODS OVERRIDE
    // -----------------

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(getFragmentLayout(), container, false)
        ButterKnife.bind(this, view)
        Icepick.restoreInstanceState(this, savedInstanceState)
        configureDesign()
        return view
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Icepick.saveInstanceState(this, outState)
    }
}