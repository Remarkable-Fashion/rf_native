package com.lf.fashion.data.network.api

import com.lf.fashion.data.response.MsgResponse
import com.lf.fashion.data.response.MyInfo
import com.lf.fashion.data.response.RandomPostResponse
import retrofit2.http.*
import java.io.File

interface MyPageApi {
    @GET("user/me")
    suspend fun getMyPageInfo(): MyInfo

    @GET("post/me?take=21")
    suspend fun getMyPagePost(@Query("cursor") nextCursor: Int? = null): RandomPostResponse

    @FormUrlEncoded
    @PATCH("user/me/profile")
    suspend fun updateProfileInfo(
        @Field("avartar") profileImage: File?,
        @Field("sex") sex: String?,
        @Field("height") height: Int?,
        @Field("weight") weight: Int?,
        @Field("introduction") introduction : String?) : MsgResponse
}