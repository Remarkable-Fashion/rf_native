package com.lf.fashion.data.network.api

import com.lf.fashion.data.model.PostInfo
import com.lf.fashion.data.model.RandomPostResponse
import com.lf.fashion.data.model.RecommendCloth
import retrofit2.http.*

interface MainHomeApi {
    @GET("post")
    suspend fun getRandomPost(
        @Query("take") take: Int,
        @Query("sex") sex: String,
        @Query("height") height: Int? = null,
        @Query("weight") weight: Int? = null,
        @Query("tpo") tpo: List<Int>? = null,
        @Query("season") season: List<Int>? = null,
        @Query("style") style: List<Int>? = null
    ): RandomPostResponse

    @GET("post/public")
    suspend fun getRandomPostPublic(
        @Query("take") take: Int,
        @Query("sex") sex: String,
        @Query("height") height: Int? = null,
        @Query("weight") weight: Int? = null,
        @Query("tpo") tpo: List<Int>? = null,
        @Query("season") season: List<Int>? = null,
        @Query("style") style: List<Int>? = null
    ): RandomPostResponse

    @GET("post/followings")
    suspend fun getFollowingPost(
        @Query("cursor") cursor: String?,
        @Query("take") take :Int,
        @Query("sex") sex: String,
        @Query("height") height: Int? = null,
        @Query("weight") weight: Int? = null,
        @Query("tpo") tpo: List<Int>? = null,
        @Query("season") season: List<Int>? = null,
        @Query("style") style: List<Int>? = null
    ) :RandomPostResponse

    @GET("post/{id}")
    suspend fun getPostInfoById(@Path("id") postId: Int): PostInfo

    @GET("clothes/{id}/recommend/top")
    suspend fun getRecommendTopClothes(
        @Path("id") postId: Int,
        @Query("category") category: String
    ): RecommendCloth

    @GET("clothes/{id}/recommend")
    suspend fun getRecommendClothesInfo(
        @Path("id") postId: Int,
        @Query("category") category: String,
    ): RecommendCloth

    @GET("post/user/{id}?take=20")
    suspend fun getPostByUserId(@Path("id") userId: Int): RandomPostResponse
}

