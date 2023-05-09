package com.lf.fashion.data.response

import android.os.Parcelable
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
