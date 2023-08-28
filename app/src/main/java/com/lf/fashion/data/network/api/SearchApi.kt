package com.lf.fashion.data.network.api

import com.lf.fashion.data.model.RandomPostResponse
import com.lf.fashion.data.model.SearchItemResult
import com.lf.fashion.data.model.SearchLookResult
import com.lf.fashion.data.model.SearchTerm
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchApi {

    @GET("search/rank")
    suspend fun getSearchTermRank() : List<SearchTerm>
    @GET("search/post?take=5")
    suspend fun getSearchResult(@Query("search") term : String): SearchLookResult

    @GET("search/clothes?take=5")
    suspend fun getItemSearchResult(@Query("search") term : String): SearchItemResult

}