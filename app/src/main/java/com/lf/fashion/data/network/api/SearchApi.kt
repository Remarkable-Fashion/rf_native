package com.lf.fashion.data.network.api

import com.lf.fashion.data.response.RandomPostResponse
import retrofit2.http.GET

//TODO SEARCH 엔드포인트 미개발 ? ?
interface SearchApi {

    @GET("scrap?cursorId=3&take=5")
    suspend fun getSearchResult(): List<RandomPostResponse>
}