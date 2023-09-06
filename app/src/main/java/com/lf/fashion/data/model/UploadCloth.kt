package com.lf.fashion.data.model

data class UploadCloth(
    val name : String,
    val category: String,
    var imageUrl: String,
    val price : Int,
    val color : String,
    val size: String,
    val brand: String,
    val reason : String?,
)