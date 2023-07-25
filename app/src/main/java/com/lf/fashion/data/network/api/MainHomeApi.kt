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
    suspend fun getPostInfoById(postId : Int) : PostInfo

    @GET("clothes/{id}/recommend")
    suspend fun getRecommendClothesInfo(postId: Int,@Query("category") category: String) : RecommendCloth
}

