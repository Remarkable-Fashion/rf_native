package com.lf.fashion.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Count(
    var favorites : Int?=0,
    var followers : Int?=0,
    var following : Int?=0,
    var posts :Int?=0
):Parcelable