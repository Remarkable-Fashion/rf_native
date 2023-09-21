package com.lf.fashion.data.model

import com.google.gson.annotations.SerializedName

//정보 보기에서 사용
data class PostInfo(
    val id: Int,
    val isFollow: Boolean? = false,
    val isScrap: Boolean? = false,
    val isPublic: Boolean? = false,
    val createdAt: String,
    val user: UserInfo,
    @SerializedName("_count")
    val count: Count,
    val styles: List<ChipInfo>?,
    val description: String?,
    // val place : String?,
    val clothes: List<Cloth>,
    @SerializedName("sex")
    val gender : String,
    val height : Int,
    val weight : Int,
    val tpos : List<ChipInfo>?,
    val season : List<ChipInfo>?
)
