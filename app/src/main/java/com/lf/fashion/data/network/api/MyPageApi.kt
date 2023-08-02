package com.lf.fashion.data.network.api

import com.lf.fashion.data.response.MsgResponse
import com.lf.fashion.data.response.MyInfo
import com.lf.fashion.data.response.RandomPostResponse
import okhttp3.RequestBody
import retrofit2.http.*
import java.io.File

interface MyPageApi {
    @GET("user/me")
    suspend fun getMyPageInfo(): MyInfo

    @GET("post/me?take=21")
    suspend fun getMyPagePost(@Query("cursor") nextCursor: Int? = null): RandomPostResponse

    /*@FormUrlEncoded*/
    @Multipart
    @PATCH("user/me/profile")
    suspend fun updateProfileInfo(
        @Part("avartar") profileImage: File?,
        @Part("sex") sex: RequestBody?,
        @Part("height") height: RequestBody?,
        @Part("weight") weight: RequestBody?,
        @Part("introduction") introduction : RequestBody?) : MsgResponse
}