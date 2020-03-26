package org.desperu.realestatemanager.utils

import android.net.Uri
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import org.desperu.realestatemanager.model.Image
import org.desperu.realestatemanager.utils.Utils.convertPriceToPatternPrice

@BindingAdapter("refreshingSwipe")
fun setRefreshing(swipeRefreshLayout: SwipeRefreshLayout, refreshing: MutableLiveData<Boolean>?) {
    val parentActivity: AppCompatActivity? = swipeRefreshLayout.context as AppCompatActivity
    if (parentActivity != null && refreshing != null)
        refreshing.observe(parentActivity, Observer { value -> swipeRefreshLayout.isRefreshing = value })
}

@BindingAdapter("mutableVisibility")
fun setMutableVisibility(view: View, visibility: MutableLiveData<Int>?) {
    val parentActivity:AppCompatActivity? = view.context as AppCompatActivity?
    if(parentActivity != null && visibility != null)
        visibility.observe(parentActivity, Observer { value -> view.visibility = value })
}

@BindingAdapter("image")
fun setImage(imageView: ImageView, image: Image) {
    imageView.setImageURI(Uri.parse(image.imageUri))
}

@BindingAdapter("setItem")
fun setItem(spinner: Spinner, string: String?) {
    for(position in 0 until spinner.adapter.count) {
        if (spinner.getItemAtPosition(position).toString() == string)
            spinner.setSelection(position)
    }
}

@BindingAdapter("onItemSelected")
fun Spinner.setOnItemSelected(listener: AdapterView.OnItemSelectedListener) {
    onItemSelectedListener = listener
}

@BindingAdapter("priceText")
fun EditText.setPriceText(str: String?) {
    // Remove listener to prevent cycle round
    this.removeTextChangedListener(listenerSave)
    val cursorPosition = this.selectionStart
    var strLength = 0
    var str1 = ""
    // Convert price to pattern price
    if (str != null) {
        str1 = convertPriceToPatternPrice(str, false)
        strLength = str.length
    }
    // Set pattern price to edit text, cursor with corrected position if needed, and set listener
    this.setText(str1)
    this.setSelection(if (cursorPosition > 0) cursorPosition + str1.length - strLength else cursorPosition)
    this.addTextChangedListener(listenerSave)
}

lateinit var listenerSave: TextWatcher

@BindingAdapter("onTextChanged")
fun EditText.setOnTextChanged(listener: TextWatcher) {
    listenerSave = listener
    addTextChangedListener(listener)
}