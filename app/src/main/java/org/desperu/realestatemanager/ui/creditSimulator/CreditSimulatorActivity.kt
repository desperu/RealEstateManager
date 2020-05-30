package org.desperu.realestatemanager.ui.creditSimulator

import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import org.desperu.realestatemanager.R
import org.desperu.realestatemanager.base.BaseBindingActivity
import org.desperu.realestatemanager.databinding.ActivityCreditSimulatorBinding
import org.desperu.realestatemanager.di.ViewModelFactory
import org.desperu.realestatemanager.utils.Utils

/**
 * Activity to simulate credit.
 *
 * @constructor Instantiates a new CreditSimulatorActivity.
 */
class CreditSimulatorActivity: BaseBindingActivity() {

    // FOR DATA
    private lateinit var binding: ActivityCreditSimulatorBinding
    private var viewModel: CreditSimulatorViewModel? = null

    // --------------
    // BASE METHODS
    // --------------

    override fun getBindingView(): View = configureViewModel()

    override fun configureDesign() {
        configureToolBar()
        configureUpButton()
    }

    // -----------------
    // CONFIGURATION
    // -----------------

    /**
     * Configure data binding with view model.
     */
    private fun configureViewModel(): View {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_credit_simulator)
        viewModel = ViewModelProvider(this, ViewModelFactory(this)).get(CreditSimulatorViewModel::class.java)

        binding.viewModel = viewModel
        return binding.root
    }

    // -----------------
    // METHODS OVERRIDE
    // -----------------

    override fun onUserInteraction() {
        super.onUserInteraction()
        val amount = viewModel?.getAmount?.get()?.let { Utils.convertPatternPriceToString(it) }
        val contribution = viewModel?.getContribution?.get()?.let { Utils.convertPatternPriceToString(it) }
        if (!amount.isNullOrBlank() && !contribution.isNullOrBlank()
                && contribution.toLong() > amount.toLong())
            alertDialogContributionOver()
    }

    // --------------------
    // UI
    // --------------------

    /**
     * Create alert dialog when the credit contribution is over the credit amount.
     */
    private fun alertDialogContributionOver() {
        val dialog: AlertDialog.Builder = AlertDialog.Builder(this, R.style.AlertDialogTheme)
        dialog.setTitle(R.string.activity_credit_simulator_dialog_title)
        dialog.setMessage(R.string.activity_credit_simulator_dialog_message)
        dialog.setPositiveButton(R.string.activity_credit_simulator_dialog_positive_button) { dialog2, _ ->
            viewModel?.setContribution("0")
            dialog2.cancel()
        }
        dialog.setNegativeButton(R.string.activity_credit_simulator_dialog_negative_button) { dialog3, _ ->
            viewModel?.resetValues()
            dialog3.cancel()
        }
        dialog.show()
    }
}