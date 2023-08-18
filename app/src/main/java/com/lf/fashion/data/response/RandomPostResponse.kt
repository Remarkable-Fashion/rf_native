package com.lf.fashion.data.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class RandomPostResponse(
    val msg: String? = null,
    val size : Int,
    val hasNext : Boolean? = true,
    val nextCursor : Int? = null,
    val posts : List<Posts>
    )
@Parcelize
data class Posts(
    val id: Int,
    var isFavorite: Boolean?=false,
    val isFollow: Boolean?=false,
    var isScrap: Boolean?, // default false 로 주게되면 scrap 모아보기에서 해제된 상태로 보이기 때문에 그냥 null로 받는다 .
    val createdAt : String,
    val images: List<ImageUrl>,
    var user: UserInfo?,
    @SerializedName("_count")
    val count: Count
):Parcelable

@Parcelize
data class ImageUrl (
    val url : String
        ):Parcelable
@Parcelize
data class UserInfo(
    val id : Int,
    val name : String,
    val profile : Profile,
    val followers : List<FollowIdSet>?
):Parcelable

@Parcelize
data class FollowIdSet(
    val followerId : Int,
    val followingId : Int
):Parcelable



