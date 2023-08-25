package com.lf.fashion.data.network.api

import com.lf.fashion.data.model.MsgResponse
import com.lf.fashion.data.model.UploadPost
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface UploadPostApi {

    @POST("post")
    suspend fun uploadPost(
        @Body post: UploadPost
    ): MsgResponse

    @Multipart
    @POST("post/image")
    suspend fun uploadPostImages(
        @Part posts: MutableList<MultipartBody.Part>
    ): MsgResponse

}