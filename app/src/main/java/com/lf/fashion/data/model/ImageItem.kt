package com.lf.fashion.data.model

import android.net.Uri

data class ImageItem(
    var uri: Uri?,
    var isChecked: Boolean,
    var checkCount : String
)