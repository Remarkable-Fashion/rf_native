package com.lf.fashion.data.model

import com.google.gson.annotations.SerializedName

data class DeepLinkPost (
    val id: Int,
    var isFavorite: Boolean? = false,
    val isFollow: Boolean? = false,
    var isScrap: Boolean?, // default false 로 주게되면 scrap 모아보기에서 해제된 상태로 보이기 때문에 그냥 null로 받는다 .
    var isPublic : Boolean ? = true,
    val createdAt: String,
    val likeCount : Int ? = 0,
    val images: List<ImageUrl>,
    var user: UserInfo2?,
    )

data class UserInfo2(
    val id : Int,
    val name : String,
    val profile : DeepLinkProfile
)

data class DeepLinkProfile(
    val id : Int,
    val avartar : String
)

