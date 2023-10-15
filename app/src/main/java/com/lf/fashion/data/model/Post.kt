package com.lf.fashion.data.model

import com.google.gson.annotations.SerializedName

//한개의 포스트+ 프로필 정보 : 딥링크를 통해 접근시 사용됩니다.
data class Post(
    val id: Int,
    val isFollow: Boolean? = false,
    val isScrap: Boolean? = false,
    var isFavorite: Boolean? = false,
    val user: UserInfo,
    val images: List<ImageUrl>,
    @SerializedName("_count")
    val count: Count
)