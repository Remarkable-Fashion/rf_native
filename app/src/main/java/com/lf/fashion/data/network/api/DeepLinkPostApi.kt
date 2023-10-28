package com.lf.fashion.data.network.api

import com.lf.fashion.data.model.DeepLinkPost
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface DeepLinkPostApi {
    @GET("post/{id}/deeplink")
    suspend fun getDeepLinkPost(@Path("id") postId : Int,
                             @Query("userId") userId : Int? = null) : DeepLinkPost

}