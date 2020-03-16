package org.desperu.realestatemanager.utils

import android.net.Uri
import android.widget.ImageView
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
        refreshing.observe(parentActivity, Observer { value -> swipeRefreshLayout.isRefreshing = value } )
}

@BindingAdapter("image")
fun setImage(imageView: ImageView, image: Image) {
    imageView.setImageURI(Uri.parse(image.imageUri))
}