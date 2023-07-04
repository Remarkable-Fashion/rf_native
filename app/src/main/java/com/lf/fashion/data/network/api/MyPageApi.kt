package com.lf.fashion.data.network.api

import com.lf.fashion.data.response.MyInfo
import com.lf.fashion.data.response.RandomPostResponse
import retrofit2.http.GET

interface MyPageApi {
    @GET("user/me")
    suspend fun getMyPageInfo() : MyInfo

    @GET("user/me?take=5")
    suspend fun getMyPagePost(): List<RandomPostResponse>

}