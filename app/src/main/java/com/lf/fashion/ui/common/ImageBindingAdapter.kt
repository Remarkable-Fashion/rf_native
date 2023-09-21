package com.lf.fashion.ui.common

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.lf.fashion.data.common.TEST_IMAGE_URL

@BindingAdapter("imageUrl")
fun loadImage(view: ImageView, imageUrl: String?) {
    if(!imageUrl.isNullOrEmpty()){
        Glide.with(view)
            .load(TEST_IMAGE_URL+imageUrl)
            .into(view)
    }
}