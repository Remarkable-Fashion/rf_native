package com.lf.fashion.data.network.api

import com.lf.fashion.data.model.MsgResponse
import com.lf.fashion.data.model.PostInfo
import okhttp3.MultipartBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface EditPostApi {

    @PATCH("")
    fun editPost() : MsgResponse

    @Multipart
    @POST("post/image")
    suspend fun uploadNewPostImage(
        @Part posts: MutableList<MultipartBody.Part>
    ): MsgResponse

    @Multipart
    @POST("clothes/image")
    suspend fun uploadNewClothImages(
        @Part clothesImage :MutableList<MultipartBody.Part>
    ): MsgResponse

    @GET("post/{id}")
    suspend fun getPostInfoByPostId(
        @Path("id") postId : Int
    ) : PostInfo
}