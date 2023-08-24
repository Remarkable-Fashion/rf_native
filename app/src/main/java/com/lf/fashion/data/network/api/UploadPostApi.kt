package com.lf.fashion.data.network.api

import retrofit2.http.POST

interface UploadPostApi {

    @POST("post")
    suspend fun uploadPost()

    @POST("post/image")
    suspend fun uploadPostImages()

}