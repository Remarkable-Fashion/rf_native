package com.lf.fashion.data.response

import com.google.gson.annotations.SerializedName

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
    var isFavorite: Boolean?,
    val isScrap: Boolean?,
    val isFollowing: Boolean?,
    val user: UserInfo,
    val clothesInfo : Cloth
)

data class UploadCloth(
    val name : String,
    val category: String,
    val imageUrl: String,
    val price : String,
    val color : String,
    val size: String,
    val brand: String,
    val reason : String?,
    // val siteUrl: String
)