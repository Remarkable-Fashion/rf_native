package com.lf.fashion.data.network.api

import com.lf.fashion.data.response.RandomPostResponse
import com.lf.fashion.data.response.SearchTerm
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchApi {

    @GET("search/rank")
    suspend fun getSearchTermRank() : List<SearchTerm>
    @GET("search/post?take=2")
    suspend fun getSearchResult(@Query("search") term : String): RandomPostResponse

    @GET("search/post?take=2")
    suspend fun getItemSearchResult(@Query("search") term : String): RandomPostResponse

}