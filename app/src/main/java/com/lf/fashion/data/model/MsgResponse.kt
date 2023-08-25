package com.lf.fashion.data.model

data class MsgResponse(
    val success : Boolean,
    val msg : String?,
    val imgUrls :List<String>?=null
)
