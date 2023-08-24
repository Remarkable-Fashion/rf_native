package com.lf.fashion.ui.home

import com.lf.fashion.data.response.ImageUrl

interface PhotoClickListener {
    fun photoClicked(bool:Boolean,photo : List<ImageUrl>)
}