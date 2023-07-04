package com.lf.fashion.data.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class RandomPostResponse(
    val msg: String? = null,
    val id: Int,
    val isFavorite: Boolean?=false,
    val isFollow: Boolean?=false,
    val isScrap: Boolean?, // default false 로 주게되면 scrap 모아보기에서 해제된 상태로 보이기 때문에 그냥 null로 받는다 .
    val createdAt : String,
    val images: List<ImageUrl>,
    val user: UserInfo?,
    @SerializedName("_count")
    val count: Count
)

@Parcelize
data class ImageUrl (
    val url : String
        ):Parcelable

data class UserInfo(
    val id : Int,
    val name : String,
    val profile : Profile,
    val followers : List<String>?
)



