package com.lf.fashion.data.repository

import com.lf.fashion.data.network.api.SearchApi
import com.lf.fashion.data.response.RandomPostResponse
import javax.inject.Inject

class SearchRepository @Inject constructor(private val searchApi: SearchApi) {

    suspend fun getSearchResult(term : String) : List<RandomPostResponse>{
        return searchApi.getSearchResult(term)
    }
    suspend fun getItemSearchResult(term : String) : List<RandomPostResponse>{
        return searchApi.getItemSearchResult(term)
    }
}