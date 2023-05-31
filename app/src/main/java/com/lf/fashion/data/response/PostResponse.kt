package com.lf.fashion.data.response

import com.google.gson.annotations.SerializedName

data class PostResponse(
    @SerializedName("_id")
    val id: String,
    val mysqlId: Int,
    val userId: Int,
    val profileImage: String?, // 추가 요구
    val likes : String?, // 추가요구
    val title: String,
    val description: String,
    val images: List<PostImage>,
    val clothes: List<String>?,
    val tpo: String,
    val season: String,
    val style: String,
    val isPublic: Boolean,
    val sex: String
)

data class PostImage(
    val id : Int,
    val url : String,
    val postId : Int
)