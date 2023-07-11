package com.lf.fashion.data.repository

import com.lf.fashion.data.network.SafeApiCall
import com.lf.fashion.data.network.api.SearchApi
import com.lf.fashion.data.response.RandomPostResponse
import javax.inject.Inject

class SearchRepository @Inject constructor(private val searchApi: SearchApi) : SafeApiCall {

    suspend fun getSearchResult(term : String) = safeApiCall{
        searchApi.getSearchResult(term)
    }
    suspend fun getItemSearchResult(term : String) = safeApiCall{
        searchApi.getItemSearchResult(term)
    }
}