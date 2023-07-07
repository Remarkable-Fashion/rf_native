package com.lf.fashion.data.network.api

import com.lf.fashion.data.response.RandomPostResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchApi {

    @GET("search/post?take=2")
    suspend fun getSearchResult(@Query("search") term : String): List<RandomPostResponse>

    @GET("search/post?take=2")
    suspend fun getItemSearchResult(@Query("search") term : String): List<RandomPostResponse>

}