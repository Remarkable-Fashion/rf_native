package com.lf.fashion.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class OtherUserInfo(
    val id: Int,
    val profile: Profile,
    val name: String,
    @SerializedName("_count")
    val count: Count
):Parcelable

