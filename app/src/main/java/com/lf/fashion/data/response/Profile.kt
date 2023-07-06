package com.lf.fashion.data.response

import com.google.gson.annotations.SerializedName

data class Profile(
    @SerializedName("avartar")
    val profileImage: String? = "https://blog.kakaocdn.net/dn/c3vWTf/btqUuNfnDsf/VQMbJlQW4ywjeI8cUE91OK/img.jpg",
    val height: Int?,
    val weight: Int?,
    val sex: String?,
    val introduction: String?
)