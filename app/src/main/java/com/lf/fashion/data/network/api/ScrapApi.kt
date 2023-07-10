package com.lf.fashion.data.network.api

import com.lf.fashion.data.response.RandomPostResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ScrapApi {
    //cursorId=3&
    @GET("scrap?take=5")
    suspend fun getScrapPost(@Query("cursor") nextCursor : Int? = null): RandomPostResponse
}