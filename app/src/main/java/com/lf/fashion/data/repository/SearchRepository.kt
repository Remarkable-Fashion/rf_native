package com.lf.fashion.data.repository

import com.lf.fashion.data.network.SafeApiCall
import com.lf.fashion.data.network.api.SearchApi
import javax.inject.Inject

class SearchRepository @Inject constructor(private val searchApi: SearchApi) : SafeApiCall {
    suspend fun getSearchTermRank() =safeApiCall {
        searchApi.getSearchTermRank()
    }
    suspend fun getSearchResult(term : String,
                                sex: String?=null,
                                height :Int?=null,
                                weight :Int?=null,
                                tpo : List<Int>?=null,
                                season : List<Int>?=null,
                                style :List<Int>?=null,
                                order: String
    ) = safeApiCall{
        searchApi.getSearchResult(term,sex,height, weight, tpo, season, style,order)
    }
    suspend fun getItemSearchResult(term : String,
                                    sex: String? = null,
                                    minPrice: Int? = null,
                                    maxPrice : Int?=null,
                                    color : List<String>?=null,
                                    order: String
    ) = safeApiCall{
        searchApi.getItemSearchResult(term,sex, minPrice, maxPrice,color,order)
    }
}