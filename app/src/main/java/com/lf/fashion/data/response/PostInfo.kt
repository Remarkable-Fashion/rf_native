package com.lf.fashion.data.response

import com.google.gson.annotations.SerializedName

//정보 보기에서 사용
data class PostInfo(
    val id: Int,
    val isFollow: Boolean?=false,
    val createdAt : String,
    val user: UserInfo,
    @SerializedName("_count")
    val count: Count,
    val styles : List<ChipInfo>,
    val description: String?,
    val place : String?,
    val clothes: List<Cloth>
    )
data class Cloth(
    val id : Int,
    val brand : String?,
    val imageUrl: String? = null,
    val category: String,
    val name: String,
    val price: Int,
    val color: String?,
    val size: String?,
    @SerializedName("_count")
    val count : Count?, // 이 의상은 어때
    val siteUrl : String?,  // 이 의상은 어때
    val reason : String?
)

data class RecommendCloth(
    val category: String,
    val hasNext : Boolean,
    val nextCursor:Int,
    val clothes : List<ClothPost>
)

data class ClothPost(
    val id: Int,
    val isFavorite: Boolean?,
    val isScrap: Boolean?,
    val isFollowing: Boolean?,
    val user: UserInfo,
    val clothesInfo : Cloth
)