package com.lf.fashion.data.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.io.File

@Parcelize
data class MyInfo(
    val id: Int,
    val profile: Profile,
    val name: String,
    @SerializedName("_count")
    val count: Count
):Parcelable


data class UpdateMyInfo(
    val avartar : File?,
    val sex : String?,
    val height : String?,
    val weight : String?,
    val introduction : String?
)
