package com.lf.fashion.data.model

import com.google.gson.annotations.SerializedName

data class SearchItemResult (
    val cursor : List<Long>?,
    val nextCursor : List<Long>?,
    val hasNext : Boolean,
    val size : Int,
    @SerializedName("search")
    val term : String,
    val clothes : List<Cloth>?
)
data class SearchLookResult(
    val cursor : List<Long>?,
    val nextCursor : List<Long>?,
    val hasNext : Boolean,
    val size : Int,
    @SerializedName("search")
    val term : String,
    val posts : List<Posts>?
)