package com.lf.fashion.data.network.api

import com.lf.fashion.data.response.RandomPostResponse
import retrofit2.http.GET

interface ScrapApi {
    @GET("scrap?cursorId=3&take=5")
    suspend fun getScrapPost(): List<RandomPostResponse>
}