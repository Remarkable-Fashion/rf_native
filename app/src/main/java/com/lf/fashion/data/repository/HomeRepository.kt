package com.lf.fashion.data.repository

import com.lf.fashion.data.network.SafeApiCall
import com.lf.fashion.data.network.api.ChipApi
import com.lf.fashion.data.network.api.MainHomeApi
import com.lf.fashion.data.response.*
import javax.inject.Inject

class HomeRepository @Inject constructor(
    private val postApi: MainHomeApi
) : SafeApiCall {


    suspend fun getRandomPost(sex: String, take: Int) = safeApiCall {
        postApi.getRandomPost(sex, take)
    }

    suspend fun getRandomPostPublic(sex: String, take: Int) = safeApiCall {
        postApi.getRandomPostPublic(sex, take)
    }

    suspend fun getPostInfoByPostId(postId: Int) = safeApiCall {
        postApi.getPostInfoById(postId)
    }

    suspend fun getRecommendClothesTop3(postId: Int, category: String) = safeApiCall {
        postApi.getRecommendTopClothes(1)

    }

    suspend fun getRecommendClothesInfo(postId: Int, category: String) = safeApiCall {
        postApi.getRecommendClothesInfo(1)
    }
    suspend fun getPostByUserId(userId : Int) = safeApiCall {
        postApi.getPostByUserId(userId)
    }

}