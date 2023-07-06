package com.lf.fashion.data.network.api

import com.lf.fashion.data.response.MsgResponse
import com.lf.fashion.data.response.RandomPostResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface PostApi {
    @GET("post?take=5")
    suspend fun getRandomPost(@Query("sex") sex: String): List<RandomPostResponse>

    @GET("post/public?take=5")
    suspend fun getRandomPostPublic(@Query("sex") sex:String) :List<RandomPostResponse>

}

