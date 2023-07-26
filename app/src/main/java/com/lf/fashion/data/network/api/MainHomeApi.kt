package com.lf.fashion.data.network.api

import com.lf.fashion.data.response.MsgResponse
import com.lf.fashion.data.response.PostInfo
import com.lf.fashion.data.response.RandomPostResponse
import com.lf.fashion.data.response.RecommendCloth
import retrofit2.http.*

interface MainHomeApi {
    @GET("post")
    suspend fun getRandomPost(
        @Query("sex") sex: String,
        @Query("take") take: Int
    ): RandomPostResponse

    @GET("post/public")
    suspend fun getRandomPostPublic(
        @Query("sex") sex: String,
        @Query("take") take: Int
    ): RandomPostResponse

    @GET("post/{id}")
    suspend fun getPostInfoById(@Path("id") postId : Int) : PostInfo

    @GET("clothes/{id}/recommend/top") //@Query("category") category: String
    suspend fun getRecommendTopClothes(@Path("id") postId: Int) : RecommendCloth

    @GET("clothes/{id}/recommend") //@Query("category") category: String
    suspend fun getRecommendClothesInfo(@Path("id") postId: Int) : RecommendCloth
}

