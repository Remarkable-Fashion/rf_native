package com.lf.fashion.data.response

data class PostTestResponse (
    val postList : List<Post>
        )
data class Post(
    val id : Int,
    val photo :  List<String>,
    val profile : String,
    val userId : String,
    val likes:String
)
