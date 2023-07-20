package com.lf.fashion.data.repository

import com.lf.fashion.data.network.SafeApiCall
import com.lf.fashion.data.network.api.CommunicateApi
import com.lf.fashion.data.network.api.ScrapApi
import com.lf.fashion.data.response.RandomPostResponse
import javax.inject.Inject

class ScrapRepository @Inject constructor(private val scrapApi: ScrapApi,private val comApi: CommunicateApi ) :SafeApiCall{

    suspend fun getScrapPosts(nextCursor: Int? = null) = safeApiCall {
        scrapApi.getScrapPost(nextCursor)
    }

    suspend fun createLike(postId : Int) = safeApiCall {
        comApi.createLike(postId)
    }

    suspend fun deleteLike(postId : Int)=safeApiCall {
        comApi.deleteLike(postId)
    }
}