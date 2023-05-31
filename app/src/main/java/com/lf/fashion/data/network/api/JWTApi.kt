package com.lf.fashion.data.network.api

import com.lf.fashion.data.response.MsgResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface JWTApi {
    @GET("auth/kakao")
    suspend fun getJWT(@Query("accessToken") loginAccessToken: String): MsgResponse
}