package com.lf.fashion.data.response

import com.google.gson.annotations.SerializedName

data class MyInfo (
    val profile : Profile,
    val name : String,
    @SerializedName("_count")
    val count : Count
        )