package org.desperu.realestatemanager.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import icepick.Icepick
import kotlinx.android.synthetic.main.toolbar.*


abstract class BaseActivity: AppCompatActivity() {

    // --------------------
    // BASE METHODS
    // --------------------

    protected abstract fun getActivityLayout(): Int
    protected abstract fun configureDesign()

    // --------------------
    // LIFE CYCLE
    // --------------------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(getActivityLayout())
        Icepick.restoreInstanceState(this, savedInstanceState)
        configureDesign()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Icepick.saveInstanceState(this, outState)
    }

    // --------------------
    // UI
    // --------------------

    protected open fun configureToolBar() {
        setSupportActionBar(toolbar)
    }

    protected open fun configureUpButton() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}