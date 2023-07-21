package com.lf.fashion.data.response

import com.google.gson.annotations.SerializedName

//정보보기에서 사용
data class PostInfo(
    val id: Int,
    val isFollow: Boolean?=false,
    val createdAt : String,
    val user: UserInfo,
    @SerializedName("_count")
    val count: Count,
    val style : List<ChipContents>,
    val introduce: String?,
    )
