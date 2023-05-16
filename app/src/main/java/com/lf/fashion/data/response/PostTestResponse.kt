package com.lf.fashion.data.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

data class PostTestResponse (
    val postList : List<Post>
        )

@Parcelize
data class Post(
    val id : Int,
    val photo :  List<Photo>,  // navigation safeargs 사용을 위해 List<String> 이 아닌 사용자정의 객체 사용!
    val profile : String,
    val userId : String,
    val likes:String
) : Parcelable

@Parcelize
data class Photo(
    val id : String,
    val imageUrl : String
):Parcelable


data class ChipInfo(
    val id: String,
    val chips : List<ChipContents>
)
data class ChipContents(
    val text:String,
    val emoji : String?
)

data class UserInfo(
    @SerializedName("user_info")
    val modelInfo: ModelInfo,
    @SerializedName("clothes")
    val clothesInfo : List<ClothesInfo>

)
data class ModelInfo(
    val profile: String,
    @SerializedName("user_id")
    val userId : String,
    val height : String,
    val weight : String,
    val place : String,
    @SerializedName("style_chips")
    val styleChips : List<ChipContents>,
    val introduce : String
)
data class ClothesInfo(
    val category : String,
    val brand: String,
    val detail : String,
    val image : String,
    val name:String
)
data class LookBook(
    @SerializedName("user_id")
    val userId: String,
    val likes : String,
    val clothesInfo: ClothesInfo
)