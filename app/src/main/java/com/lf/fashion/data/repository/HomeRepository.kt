package com.lf.fashion.data.repository

import com.lf.fashion.data.network.SafeApiCall
import com.lf.fashion.data.network.api.MainHomeApi
import javax.inject.Inject

class HomeRepository @Inject constructor(
    private val postApi: MainHomeApi
) : SafeApiCall {


    suspend fun getRandomPost(
        take: Int,
        sex: String,
        height: Int?,
        weight: Int?,
        tpo: List<Int>?,
        season: List<Int>?,
        style: List<Int>?
    ) = safeApiCall {
        postApi.getRandomPost(take, sex, height, weight, tpo, season, style)
    }

    suspend fun getRandomPostPublic(
        take: Int,
        sex: String,
        height: Int?,
        weight: Int?,
        tpo: List<Int>?,
        season: List<Int>?,
        style: List<Int>?
    ) = safeApiCall {
        postApi.getRandomPostPublic(take, sex, height, weight, tpo, season, style)
    }

    suspend fun getFollowingPost(
        cursor : String,
        take: Int,
        sex: String,
        height: Int?,
        weight: Int?,
        tpo: List<Int>?,
        season: List<Int>?,
        style: List<Int>?
    ) = safeApiCall {
        postApi.getFollowingPost(cursor, take, sex, height, weight, tpo, season, style)
    }

    suspend fun getPostInfoByPostId(postId: Int) = safeApiCall {
        postApi.getPostInfoById(postId)
    }

    suspend fun getRecommendClothesInfo(postId: Int, category: String, orderMode: String) =
        safeApiCall {
            if (orderMode == "Best") {
                postApi.getRecommendTopClothes(postId, category)
            } else {
                postApi.getRecommendClothesInfo(postId, category)
            }
        }

}