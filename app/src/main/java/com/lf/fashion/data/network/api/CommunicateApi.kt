package com.lf.fashion.data.network.api

import com.lf.fashion.data.model.DeclareInfo
import com.lf.fashion.data.model.MsgResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.Path

interface CommunicateApi {

    @POST("post/{id}/favorite")
    suspend fun createLike(@Path("id") postId: Int): MsgResponse

    @DELETE("post/{id}/favorite")
    suspend fun deleteLike(@Path("id") postId: Int): MsgResponse

    @POST("post/{id}/scrap")
    suspend fun createScrap(@Path("id") postId: Int): MsgResponse

    @DELETE("post/{id}/scrap")
    suspend fun deleteScrap(@Path("id") postId: Int): MsgResponse


    /*팔로잉 분리할지도 ? */
    @POST("user/following/{id}")
    suspend fun createFollowing(@Path("id") userId: Int): MsgResponse

    @DELETE("user/following/{id}")
    suspend fun deleteFollowing(@Path("id") userId: Int): MsgResponse

    @POST("user/block/{id}")
    suspend fun blockUser(@Path("id") userId: Int): MsgResponse

    @DELETE("user/block/{id}")
    suspend fun deleteBlock(@Path("id") userId: Int): MsgResponse


    @POST("clothes/{id}/favorite")
    suspend fun createClothesLike(@Path("id") clothesId : Int) : MsgResponse

    @DELETE("clothes/{id}/favorite")
    suspend fun deleteClothesLike(@Path("id") clothesId : Int) : MsgResponse

    @POST("post/report")
    suspend fun declarePost(@Body declareInfo : DeclareInfo) : MsgResponse
}