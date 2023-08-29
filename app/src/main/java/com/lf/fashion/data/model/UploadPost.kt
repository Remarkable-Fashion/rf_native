package com.lf.fashion.data.model

import com.google.gson.annotations.SerializedName

data class UploadPost(
    val imgUrls: List<String>,
    val title: String, // 추후 삭제
    val description: String,
    @SerializedName("sex")
    val gender: String,
    val tops : List<Int>,
    val seasons : List<Int>,
    val style : List<Int>,
    val clothes : List <UploadCloth>
    //val height : String ,
    //val weight : String ,
)