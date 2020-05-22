package org.desperu.realestatemanager.extension

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Context
import android.widget.TextView
import androidx.databinding.ObservableField
import org.desperu.realestatemanager.R
import org.desperu.realestatemanager.utils.Utils.intDateToString
import org.desperu.realestatemanager.utils.Utils.stringToDate
import java.util.*


/**
 * Create a DatePickerDialog, for associated picker text view, with given string date to set.
 * @param context the context from this function is called.
 * @param pickerView the associated picker text view.
 * @param date the given string date, to set DatePickerDialog.
 */
internal fun createDatePickerDialog(context: Context, pickerView: TextView, date: ObservableField<String>?) {
    val cal: Calendar = Calendar.getInstance()
    val dateValue = date?.get()
    if (!dateValue.isNullOrEmpty()) stringToDate(dateValue)?.let { cal.time = it }
    val year: Int = cal.get(Calendar.YEAR)
    val monthOfYear: Int = cal.get(Calendar.MONTH)
    val dayOfMonth: Int = cal.get(Calendar.DAY_OF_MONTH)
    DatePickerDialog(context, R.style.DatePickerDialogTheme, getOnDateSetListener(pickerView),
            year, monthOfYear, dayOfMonth)
            .show()
}

/**
 * Create and return OnDateSetListener, for the DatePickerDialog associated with the picker text view,
 * and set picker text when a date is selected.
 * @param pickerView the picker text view associated with the DatePickerDialog.
 * @return the listener created.
 */
private fun getOnDateSetListener(pickerView: TextView) = OnDateSetListener { _, year, month, dayOfMonth ->
    pickerView.text = intDateToString(dayOfMonth, month, year)
}