package com.lf.fashion.data.network.api

import com.lf.fashion.data.model.FollowerUserList
import com.lf.fashion.data.model.FollowingUserList
import com.lf.fashion.data.model.MyBlockUserList
import com.lf.fashion.data.model.OtherUserInfo
import com.lf.fashion.data.model.RandomPostResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface UserProfileApi {

    @GET("post/user/{id}?take=20")
    suspend fun getPostInfoByUserId(
        @Path("id") userId: Int,
        @Query("cursor") nextCursor: Int? = null
    ): RandomPostResponse

    @GET("user/{id}")
    suspend fun getUserProfileInfo(@Path("id") userId: Int): OtherUserInfo

    @GET("user/following/{id}")
    suspend fun getMyFollowing(@Path("id") userId: Int): FollowingUserList

    @GET("user/follower/{id}")
    suspend fun getMyFollowers(@Path("id") userId: Int): FollowerUserList

    @GET("user/block/{id}")
    suspend fun getMyBlockUser(@Path("id") userId: Int): MyBlockUserList
}