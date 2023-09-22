package com.lf.fashion.data.network.api

import com.lf.fashion.data.model.Cloth
import com.lf.fashion.data.model.MsgResponse
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface UploadClothesApi {

    @POST("clothes/{id}/recommend")
    suspend fun uploadClothes(
        @Path("id") clothesId: Int,
        @Body cloth : Cloth
    ): MsgResponse

    @Multipart
    @POST("clothes/image")
    suspend fun uploadClothesImages(
        @Part clothesImage : MultipartBody.Part
    ): MsgResponse

}