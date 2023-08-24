package com.lf.fashion.data.network.api

import com.lf.fashion.data.model.TokenResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface TokenRefreshApi {
   @FormUrlEncoded
    @POST("refresh_token.php")
    suspend fun refreshAccessToken(
        @Field("expired_token") expiredAccessToken: String?,
        @Field("refresh_token") refreshToken: String?
    ): TokenResponse
}