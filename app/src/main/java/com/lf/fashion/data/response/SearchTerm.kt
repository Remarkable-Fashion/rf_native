package com.lf.fashion.data.response

import com.google.gson.annotations.SerializedName

data class SearchTerm(
    @SerializedName("key")
    val term: String,
    @SerializedName("doc_count")
    val docCount: Int,
    val changeIndicator: String
)