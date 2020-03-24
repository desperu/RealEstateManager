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

@BindingAdapter("onTextChanged")
fun EditText.setOnTextChanged(listener: TextWatcher) {
    addTextChangedListener(listener)
}