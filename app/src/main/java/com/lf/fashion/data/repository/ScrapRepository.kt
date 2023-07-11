package com.lf.fashion.data.repository

import com.lf.fashion.data.network.SafeApiCall
import com.lf.fashion.data.network.api.ScrapApi
import com.lf.fashion.data.response.RandomPostResponse
import javax.inject.Inject

class ScrapRepository @Inject constructor(private val scrapApi: ScrapApi ) :SafeApiCall{

    suspend fun getScrapPosts(nextCursor: Int? = null) = safeApiCall {
        scrapApi.getScrapPost(nextCursor)
    }

}