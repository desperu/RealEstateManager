package org.desperu.realestatemanager.extension

import android.net.Uri
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import org.desperu.realestatemanager.R
import org.desperu.realestatemanager.model.Estate
import org.desperu.realestatemanager.utils.Utils.convertPriceToPatternPrice
import java.lang.ref.WeakReference

/**
 * Set visibility for the associated view.
 * @param show if true set visible, else set gone.
 */
@BindingAdapter("show")
fun View.setVisibility(show: Boolean?) {
    visibility = if (show != null && show) View.VISIBLE else View.GONE
}

/**
 * Set on click listener for view.
 * @param listener the listener to set for on click.
 */
@BindingAdapter("onClick")
fun View.onClick(listener: View.OnClickListener) {
    setOnClickListener(listener)
}

/**
 * Set on long click listener for view.
 * @param listener the listener to set for on long click.
 */
@BindingAdapter("onLongClick")
fun View.onLongClick(listener: View.OnLongClickListener) {
    setOnLongClickListener(listener)
}

/**
 * Setter for image in image view.
 * @param imageUri the uri of image to set.
 */
@Suppress("deprecation")
@BindingAdapter("imageUri")
fun ImageView.setImageUri(imageUri: String?) { // TODO use glide if error persist
    if (!imageUri.isNullOrBlank())
        setImageURI(Uri.parse(imageUri))
    else
        setImageDrawable(resources.getDrawable(R.drawable.no_image))
}

/**
 * Setter for spinners.
 * @param string the string to set in spinner.
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
 * @param listener the listener to set for item selected.
 */
@BindingAdapter("onItemSelected")
fun Spinner.setOnItemSelected(listener: OnItemSelectedListener) {
    onItemSelectedListener = listener
}

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
        setSelection(if (cursorPosition > 0 && newCursorPosition <= str1.length) newCursorPosition else cursorPosition)
    }
    // Set listener
    addTextChangedListener(listenerSave.get())
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

/**
 * Custom setter for text view price with pattern, in estate item.
 * @param price the price convert with pattern to set.
 */
@BindingAdapter("setPrice")
fun TextView.setPrice(price: Long) {
    text = convertPriceToPatternPrice(price.toString(), true)
}

/**
 * Set a marker on the map, and animate camera to this point.
 * @param estate the estate to set on the map.
 */
@BindingAdapter("setMarker")
fun MapView.setMarker(estate: Estate?) {
    estate?.let {
        addMarker(it)
        val estatePosition = LatLng(estate.address.latitude, estate.address.longitude)
        animateCamera(estatePosition)
    }
}

/**
 * Set a marker for each estate in the list.
 * @param estateList the given estate list to add each on the map.
 */
@BindingAdapter("setMarkerList")
fun MapView.setMarkerList(estateList: List<Estate>?) {
    getMapAsync {
        val latLngBounds = it.projection.visibleRegion.latLngBounds
        val estateListToShow = estateList?.filter { estate -> latLngBounds.contains(LatLng(estate.address.latitude, estate.address.longitude)) }
        estateListToShow?.forEach { estate -> addMarker(estate) }
    }
}

/**
 * Set background color for item in estate list, depends of estate type.
 * @param type the estate type of the item.
 */
@Suppress("deprecation")
@BindingAdapter("setBackgroundColor")
fun View.setBackgroundColor(type: String?) {
    val typeList = resources.getStringArray(R.array.estate_type_list)
    setBackgroundColor(resources.getColor(
            when (type) {
                typeList[0] -> R.color.colorFlat
                typeList[1] -> R.color.colorDuplex
                typeList[2] -> R.color.colorHouse
                typeList[3] -> R.color.colorPenthouse
                else -> R.color.colorNoType
            }
    ))
}