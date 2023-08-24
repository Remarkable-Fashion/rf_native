package com.lf.fashion.data.model

data class UploadCloth(
    val name : String,
    val category: String,
    val imageUrl: String,
    val price : String,
    val color : String,
    val size: String,
    val brand: String,
    val reason : String?,
    val siteUrl: String ="추후 삭제"
)