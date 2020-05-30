package org.desperu.realestatemanager.ui.creditSimulator

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.databinding.ObservableLong
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.desperu.realestatemanager.utils.Utils.convertPatternPriceToString
import org.desperu.realestatemanager.utils.Utils.creditCalculus
import org.desperu.realestatemanager.utils.Utils.realCreditRate

/**
 * View Model witch provide data for credit simulator activity.
 *
 * @constructor Instantiates a new CreditSimulatorViewModel.
 */
class CreditSimulatorViewModel: ViewModel() {

    // FOR DATA
    private val amount = ObservableField<String>()
    private val contribution = ObservableField("0")
    val rate = ObservableField("1.0")
    val duration = ObservableInt(10)
    private val monthlyPayment = ObservableLong()
    private val creditCost = ObservableLong()
    private val realRate = ObservableField<String>()

    // -------------
    // LISTENER
    // -------------

    /**
     * Edit Text Listener for amount price edit text.
     */
    private val editAmountListener = object: TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            amount.set(s.toString())
            calculusCredit()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }

    /**
     * Edit Text Listener for contribution price edit text.
     */
    private val editContributionListener = object: TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            contribution.set(s.toString())
            calculusCredit()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }

    /**
     * Edit Text Listener for rate and duration edit text, to launch credit calculation when changed.
     */
    private val editTextListener = object: TextWatcher {
        override fun afterTextChanged(s: Editable?) { calculusCredit() }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }

    /**
     * Button on click listener, execute corresponding action, depends of view tag.
     */
    private val buttonListener = View.OnClickListener { v ->
        when (v.tag) {
            // Minus Year Duration.
            "minusYear"-> if (duration.get() > 2) {
                duration.set(duration.get() - 1)
                calculusCredit()
            }

            // Add Year Duration.
            "addYear" -> if (duration.get() < 80) {
                duration.set(duration.get() + 1)
                calculusCredit()
            }
        }
    }

    // -------------
    // UTILS
    // -------------

    /**
     * Calculate credit with given data, and set result in corresponding values.
     */
    private fun calculusCredit() = viewModelScope.launch(Dispatchers.Default) {
        // Convert string pattern to string and check if null or blank.
        val amount = amount.get()?.let { convertPatternPriceToString(it) }
        val contribution = contribution.get()?.let { convertPatternPriceToString(it) }
        if (!amount.isNullOrBlank() && contribution != null && !rate.get().isNullOrBlank()) {

            // Convert string values to number.
            val amountD = amount.toDouble()
            val contributionI = if (contribution.isBlank()) 0 else contribution.toInt()
            val rateD = rate.get()!!.toDouble()

            // Check that's needed values are over 0.
            if (amountD > 0 && rateD > 0 && duration.get() > 0) {
                val result = creditCalculus(duration.get(), amountD, rateD, contributionI)
                result["monthlyPayment"]?.toLong()?.let { monthlyPayment.set(it) }
                result["creditCost"]?.toLong()?.let { creditCost.set(it) }
                realRate.set(realCreditRate(amountD, creditCost.get().toDouble()))
            }
        }
    }

    // --- SETTERS ---

    internal fun setContribution(value: String) { contribution.set(value); calculusCredit() }

    internal fun resetValues() {
        amount.set("0")
        contribution.set("0")
        rate.set("1.0")
        duration.set(10)
        monthlyPayment.set(0L)
        creditCost.set(0L)
        realRate.set("0")
    }

    // --- GETTERS ---

    val getAmount = amount

    val getContribution = contribution

    val getMonthlyPayment = monthlyPayment

    val getCreditCost = creditCost

    val getRealRate = realRate

    val getEditAmountListener = editAmountListener

    val getEditContributionListener = editContributionListener

    val getEdiTextListener = editTextListener

    val getButtonListener = buttonListener
}