package com.lf.fashion.data.model

import com.google.gson.annotations.SerializedName

data class EditPost (
    val imgUrls: List<String>,
    val description: String,
    @SerializedName("sex")
    val gender: String,
    val tpos : List<Int>,
    val seasons : List<Int>,
    val styles : List<Int>,
    val height : Int ,
    val weight : Int,
    val isPublic : Boolean
)