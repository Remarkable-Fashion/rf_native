package com.lf.fashion.data.repository

import com.lf.fashion.data.network.api.ScrapApi
import com.lf.fashion.data.network.api.SearchApi
import com.lf.fashion.data.response.RandomPostResponse
import javax.inject.Inject

class SearchRepository @Inject constructor(private val searchApi: SearchApi) {

    suspend fun getSearchResult() : List<RandomPostResponse>{
        return searchApi.getSearchResult()
    }

}