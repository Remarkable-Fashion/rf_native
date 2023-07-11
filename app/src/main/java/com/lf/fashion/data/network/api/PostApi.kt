package com.lf.fashion.data.network.api

import com.lf.fashion.data.response.MsgResponse
import com.lf.fashion.data.response.RandomPostResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface PostApi {
    @GET("post")
    suspend fun getRandomPost(@Query("sex") sex: String,@Query("take") take : Int): RandomPostResponse

    @GET("post/public")
    suspend fun getRandomPostPublic(@Query("sex") sex:String , @Query("take") take: Int) :RandomPostResponse

}

