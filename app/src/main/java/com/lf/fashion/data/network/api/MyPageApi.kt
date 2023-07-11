package com.lf.fashion.data.network.api

import com.lf.fashion.data.response.MyInfo
import com.lf.fashion.data.response.RandomPostResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MyPageApi {
    @GET("user/me")
    suspend fun getMyPageInfo() : MyInfo

    @GET("post/me?take=21")
    suspend fun getMyPagePost(@Query("cursor") nextCursor : Int?=null): RandomPostResponse

}