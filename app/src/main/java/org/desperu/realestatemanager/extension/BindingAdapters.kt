package org.desperu.realestatemanager.extension

import android.net.Uri
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import org.desperu.realestatemanager.model.Image
import org.desperu.realestatemanager.utils.Utils.convertPriceToPatternPrice

/**
 * Observe value for stop refreshing animation.
 */
@BindingAdapter("refreshingSwipe")
fun setRefreshing(swipeRefreshLayout: SwipeRefreshLayout, refreshing: MutableLiveData<Boolean>?) {
    val parentActivity: AppCompatActivity? = swipeRefreshLayout.context as AppCompatActivity
    if (parentActivity != null && refreshing != null)
        refreshing.observe(parentActivity, Observer { value -> swipeRefreshLayout.isRefreshing = value })
}

/**
 * Observe value for view visibility.
 */
@BindingAdapter("mutableVisibility")
fun setMutableVisibility(view: View, visibility: MutableLiveData<Int>?) {
    val parentActivity: AppCompatActivity? = view.context as AppCompatActivity?
    if (parentActivity != null && visibility != null)
        visibility.observe(parentActivity, Observer { value -> view.visibility = value })
}

/**
 * Set on click listener for view.
 */
@BindingAdapter("onClick")
fun View.onClick(listener: View.OnClickListener) {
    setOnClickListener(listener)
}

/**
 * Set on long click listener for view.
 */
@BindingAdapter("onLongClick")
fun View.onLongClick(listener: View.OnLongClickListener) {
    setOnLongClickListener(listener)
}

/**
 * Setter for image in image view.
 */
@BindingAdapter("image")
fun ImageView.setImage(image: Image) {
    setImageURI(Uri.parse(image.imageUri))
}

/**
 * Setter for spinners.
 */
@BindingAdapter("setItem")
fun Spinner.setItem(string: String?) {
    for(position in 0 until adapter.count) {
        if (getItemAtPosition(position).toString() == string)
            setSelection(position)
    }
}

/**
 * Set listener for spinners.
 */
@BindingAdapter("onItemSelected")
fun Spinner.setOnItemSelected(listener: AdapterView.OnItemSelectedListener) {
    onItemSelectedListener = listener
}

/**
 * Save Listener for price in manage estate, needed for custom setter.
 */
private lateinit var listenerSave: TextWatcher

/**
 * Set listener for edit text price.
 */
@BindingAdapter("onTextChanged")
fun EditText.setOnTextChanged(listener: TextWatcher) {
    listenerSave = listener
    addTextChangedListener(listener)
}

/**
 * Custom setter for price in manage estate.
 */
@BindingAdapter("priceText")
fun EditText.setPriceText(str: String?) {
    // Remove listener to prevent infinite loop
    removeTextChangedListener(listenerSave)
    if (str != null) {
        val cursorPosition = selectionStart
        // Convert price to pattern price
        val str1 = convertPriceToPatternPrice(str, false)
        val strLength: Int = str.length
        // Set pattern price to edit text, cursor with corrected position if needed
        setText(str1)
        val newCursorPosition = cursorPosition + str1.length - strLength
        setSelection(if (cursorPosition > 0 && newCursorPosition <= str1.length) newCursorPosition else cursorPosition)
    }
    // Set listener
    addTextChangedListener(listenerSave)
}

/**
 * Custom setter for number (Int), in Edit Text.
 */
@BindingAdapter("android:text")
fun EditText.setNumber(number: Int) {
    setText(if (number == 0) "" else number.toString())
}

/**
 * Custom getter for number (Int), in Edit Text.
 */
@InverseBindingAdapter(attribute = "android:text")
fun EditText.getNumber(): Int {
    val str = text.toString()
    return if (str.isBlank()) 0 else str.toInt()
}

/**
 * Custom setter for text view price with pattern, in estate item.
 */
@BindingAdapter("setPrice")
fun TextView.setPrice(price: Long) {
    text = convertPriceToPatternPrice(price.toString(), true)
}