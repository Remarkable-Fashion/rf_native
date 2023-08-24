package com.lf.fashion.data.response

import android.net.Uri

data class ImageItem(
    var uri: Uri?,
    var isChecked: Boolean,
    var checkCount : String
)