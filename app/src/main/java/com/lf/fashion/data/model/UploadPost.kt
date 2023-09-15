package com.lf.fashion.data.model

import com.google.gson.annotations.SerializedName

data class UploadPost(
    val imgUrls: List<String>,
    val description: String,
    @SerializedName("sex")
    val gender: String,
    val tpos : List<Int>,
    val seasons : List<Int>,
    val styles : List<Int>,
    val clothes : List <UploadCloth>,
    val height : Int ,
    val weight : Int,
    val isPublic : Boolean
)