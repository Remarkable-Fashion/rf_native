package com.lf.fashion.data.network.api

import com.lf.fashion.data.response.MsgResponse
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.Path

interface CommunicateApi {

    @POST("post/{id}/favorite")
    suspend fun createLike(@Path("id") postId: Int): MsgResponse

    @DELETE("post/{id}/favorite")
    suspend fun deleteLike(@Path("id") postId : Int) : MsgResponse

    @POST("post/{id}/scrap")
    suspend fun createScrap(@Path("id") postId: Int) : MsgResponse

    @DELETE("post/{id}/scrap")
    suspend fun deleteScrap(@Path("id") postId: Int) : MsgResponse


    /*팔로잉 분리할지도 ? */
    @POST("user/following/{id}")
    suspend fun createFollowing(@Path("id") userId: Int) : MsgResponse

    @DELETE("user/following/{id}")
    suspend fun deleteFollowing(@Path("id") userId: Int) : MsgResponse

}