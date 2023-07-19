package com.lf.fashion.data.network.api

import com.lf.fashion.data.response.MsgResponse
import com.lf.fashion.data.response.RandomPostResponse
import retrofit2.http.*

interface MainHomeApi {
    @GET("post")
    suspend fun getRandomPost(@Query("sex") sex: String,@Query("take") take : Int): RandomPostResponse

    @GET("post/public")
    suspend fun getRandomPostPublic(@Query("sex") sex:String , @Query("take") take: Int) :RandomPostResponse

    @POST("post/{id}/favorite")
    suspend fun createLike(@Path("id") postId : Int) : MsgResponse
}

