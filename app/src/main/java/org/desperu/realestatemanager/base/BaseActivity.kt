package org.desperu.realestatemanager.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import butterknife.ButterKnife
import icepick.Icepick
import org.desperu.realestatemanager.R


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
        ButterKnife.bind(this) //Configure ButterKnife
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
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
    }

    protected open fun configureUpButton() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}