package org.desperu.realestatemanager.extension

import android.net.Uri
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.databinding.BindingAdapter
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import org.desperu.realestatemanager.R
import org.desperu.realestatemanager.model.Estate
import org.desperu.realestatemanager.utils.Utils.convertPriceToPatternPrice
import org.desperu.realestatemanager.view.CustomSeekBarView
import org.desperu.realestatemanager.view.OnRangeChangeListener

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
 * Set on query text listener for the search view in the toolbar.
 * @param listener the listener to set for on query text changed.
 */
@BindingAdapter("onQueryText")
fun SearchView.setOnQueryText(listener: SearchView.OnQueryTextListener?) {
    setOnQueryTextListener(listener)
}

/**
 * Setter for image in image view.
 * @param imageUri the uri of image to set.
 */
@Suppress("deprecation")
@BindingAdapter("imageUri")
fun ImageView.setImageUri(imageUri: String?) {
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
    for(position in 1 until adapter.count) {
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
 * Custom setter for text view price with pattern, in estate item and detail.
 * @param price the price to convert with pattern and to set.
 */
@BindingAdapter("setPrice")
fun TextView.setPrice(price: Long) {
    text = if (price != 0L)
                convertPriceToPatternPrice(price.toString(), true)
           else
                resources.getString(R.string.fragment_estate_detail_text_no_data)
}

/**
 * Custom setter for title text view in estate detail (type and city).
 * @param estate the estate used to set title.
 */
@BindingAdapter("setTitle")
fun TextView.setTitle(estate: Estate?) {
    estate?.let {
        text = when {
            estate.type.isNotBlank() && estate.address.city.isNotBlank() ->
                resources.getString(R.string.fragment_estate_detail_text_title_at, estate.type, estate.address.city)
            estate.type.isNotBlank() -> estate.type
            estate.address.city.isNotBlank() -> estate.address.city
            else -> resources.getString(R.string.fragment_estate_detail_text_no_data)
        }
    }
}

/**
 * Custom setter for street number and name text view in estate detail (type and city).
 * @param estate the estate used to set title.
 */
@BindingAdapter("setStreet")
fun TextView.setStreet(estate: Estate?) {
    estate?.let {
        text = when {
            estate.address.streetNumber != 0 && estate.address.streetName.isNotBlank() ->
                "${estate.address.streetNumber}, ${estate.address.streetName}"
            estate.address.streetNumber != 0 && estate.address.streetName.isBlank() ->
                "${estate.address.streetNumber}"
            estate.address.streetNumber == 0 && estate.address.streetName.isNotBlank() ->
                estate.address.streetName
            else -> resources.getString(R.string.fragment_estate_detail_text_no_data)
        }
    }
}

/**
 * Set address value in text view, if value is blank or equal 0, hide the text view.
 * @param value the value to set.
 */
@BindingAdapter("setAddress")
fun TextView.setAddress(value: String?) {
    value?.let {
        if (value.isNotBlank() && value != "0") {
            visibility = View.VISIBLE
            text = value
        }
        else visibility = View.GONE
    }
}

/**
 * Set value in text view, if value is blank or equal 0, show text no data.
 * @param value the value to set.
 */
@BindingAdapter("setValue")
fun TextView.setValue(value: String?) {
    value?.let {
        text = if (value.isNotBlank() && value != "0")
                    if (tag == "surfaceDetail")
                        "$value " + resources.getString(R.string.fragment_estate_data_text_surface_unity)
                    else value
               else resources.getString(R.string.fragment_estate_detail_text_no_data)
    }
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

/**
 * Set the background color of the selected item estate for tablet mode.
 * @param isSelected true if this estate item is the selected item, false otherwise.
 */
@Suppress("deprecation")
@BindingAdapter("selectedColor")
fun View.setSelectedColor(isSelected: Boolean?) {
    if (isSelected != null)
        setBackgroundColor(resources.getColor(if (!isSelected) android.R.color.white else R.color.colorPink))
}

/**
 * Set the price text color of the selected item estate for tablet mode.
 * @param isSelected true if this estate item is the selected item, false otherwise.
 */
@Suppress("deprecation")
@BindingAdapter("selectedTextColor")
fun TextView.setSelectedTextColor(isSelected: Boolean?) {
    if (isSelected != null)
        setTextColor(resources.getColor(if (!isSelected) R.color.colorPink else android.R.color.white))
}

/**
 * Set on range changed listener for custom range seek bar view.
 * @param listener the listener to set.
 */
@BindingAdapter("onRangeChanged")
fun CustomSeekBarView.setRangeChangeListener(listener: OnRangeChangeListener) {
    setOnRangeChangeListener(listener)
}

/**
 * Set text watcher listener for text date in filter fragment.
 * @param listener the text watcher listener to set.
 */
@BindingAdapter("onTextChanged")
fun TextView.setOnTextChanged(listener: TextWatcher) {
    addTextChangedListener(listener)
}