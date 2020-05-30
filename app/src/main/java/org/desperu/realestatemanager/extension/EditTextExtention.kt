package org.desperu.realestatemanager.extension

import android.text.TextWatcher
import android.widget.EditText
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import org.desperu.realestatemanager.utils.Utils.convertPriceToPatternPrice
import java.lang.ref.WeakReference


/**
 * Save Listener for price in manage estate, needed for custom setter,
 * use weak reference to prevent memory leak.
 */
private lateinit var listenerSave: WeakReference<TextWatcher>

/**
 * Set listener for edit text price.
 * @param listener the listener to set.
 */
@BindingAdapter("onTextChanged")
fun EditText.setOnTextChanged(listener: TextWatcher) {
    listenerSave = WeakReference(listener)
    addTextChangedListener(listener)
}

/**
 * Custom setter for price in manage estate.
 * @param str the string to convert and set.
 */
@BindingAdapter("priceText")
fun EditText.setPriceText(str: String?) {
    // Remove listener to prevent infinite loop
    removeTextChangedListener(listenerSave.get())
    if (str != null) {
        val cursorPosition = selectionStart
        // Convert price to pattern price
        val str1 = convertPriceToPatternPrice(str, false)
        // Set pattern price to edit text, cursor with corrected position if needed
        setText(str1)
        val newCursorPosition = cursorPosition + str1.length - str.length
        setSelection(getCursorPosition(cursorPosition, newCursorPosition, str1.length))
    }
    // Set listener
    addTextChangedListener(listenerSave.get())
}

/**
 * Get the new properly cursor position when convert price to pattern price.
 * @param cursorPosition the actual cursor position.
 * @param newCursorPosition the new cursor position to try to set.
 * @param strLength the length of the string.
 * @return the cursor position to set.
 */
private fun getCursorPosition(cursorPosition: Int, newCursorPosition: Int, strLength: Int): Int = when {
    newCursorPosition in 0..strLength -> newCursorPosition
    cursorPosition in 0..strLength -> cursorPosition
    else -> strLength
}

/**
 * Save Listener2 for contribution in credit simulator, needed for custom setter,
 * use weak reference to prevent memory leak.
 */
private lateinit var listenerSave2: WeakReference<TextWatcher>

/**
 * Set listener for edit text contribution in credit simulator.
 * @param listener the listener to set.
 */
@BindingAdapter("onTextChanged2")
fun EditText.setOnTextChanged2(listener: TextWatcher) {
    listenerSave2 = WeakReference(listener)
    addTextChangedListener(listener)
}

/**
 * Custom setter for contribution in credit simulator.
 * @param str the string to convert and set.
 */
@BindingAdapter("priceText2")
fun EditText.setPriceText2(str: String?) {
    // Remove listener to prevent infinite loop
    removeTextChangedListener(listenerSave2.get())
    if (str != null) {
        val cursorPosition = selectionStart
        // Convert price to pattern price
        val str1 = convertPriceToPatternPrice(str, false)
        // Set pattern price to edit text, cursor with corrected position if needed
        setText(str1)
        val newCursorPosition = cursorPosition + str1.length - str.length
        setSelection(getCursorPosition(cursorPosition, newCursorPosition, str1.length))
    }
    // Set listener
    addTextChangedListener(listenerSave2.get())
}

/**
 * Set listener3 for edit text rate and duration in credit simulator.
 * @param listener the listener to set.
 */
@BindingAdapter("onTextChanged3")
fun EditText.setOnTextChanged3(listener: TextWatcher) {
    addTextChangedListener(listener)
}

/**
 * Custom setter for number (Int), in Edit Text.
 * @param number the number to set.
 */
@BindingAdapter("android:text")
fun EditText.setNumber(number: Int) {
    setText(if (number == 0) "" else number.toString())
}

/**
 * Custom getter for number (Int), in Edit Text.
 * @return the integer value of the inserted text, 0 if blank.
 */
@InverseBindingAdapter(attribute = "android:text")
fun EditText.getNumber(): Int {
    val str = text.toString()
    return if (str.isBlank()) 0 else str.toInt()
}