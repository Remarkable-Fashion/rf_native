package com.lf.fashion.ui.home

import com.lf.fashion.data.response.ImageUrl
import com.lf.fashion.data.response.Photo

interface PhotoClickListener {
    fun photoClicked(bool:Boolean,photo : List<ImageUrl>)
}