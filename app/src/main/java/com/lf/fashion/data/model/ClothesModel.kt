package com.lf.fashion.data.model

import com.google.gson.annotations.SerializedName

data class Cloth(
    val id : Int?,
    val name: String,
    val category: String,
    var imageUrl: String? = null,
    val price: Int,
    val color: String?,
    val size: String?,
    val brand : String?,
    @SerializedName("_count")
    val count : Count?, // 이 의상은 어때
    //val siteUrl : String?,  // 이 의상은 어때
    val reason : String?
)

data class RecommendCloth(
    val category: String,
    val hasNext : Boolean,
    val nextCursor:Int,
    val clothes : List<ClothPost>
)

data class ClothPost(
  //  val id: Int,
    var isFavorite: Boolean?,
    val isScrap: Boolean?,
    val isFollowing: Boolean?,
    val user: UserInfo,
    val clothesInfo : Cloth
)

