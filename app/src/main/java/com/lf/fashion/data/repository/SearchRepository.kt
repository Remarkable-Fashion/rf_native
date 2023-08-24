package com.lf.fashion.data.repository

import com.lf.fashion.data.network.SafeApiCall
import com.lf.fashion.data.network.api.SearchApi
import javax.inject.Inject

class SearchRepository @Inject constructor(private val searchApi: SearchApi) : SafeApiCall {
    suspend fun getSearchTermRank() =safeApiCall {
        searchApi.getSearchTermRank()
    }
    suspend fun getSearchResult(term : String) = safeApiCall{
        searchApi.getSearchResult(term)
    }
    suspend fun getItemSearchResult(term : String) = safeApiCall{
        searchApi.getItemSearchResult(term)
    }
}