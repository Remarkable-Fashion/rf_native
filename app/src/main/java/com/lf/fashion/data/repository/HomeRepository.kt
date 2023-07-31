package com.lf.fashion.data.repository

import com.lf.fashion.data.network.SafeApiCall
import com.lf.fashion.data.network.api.ChipApi
import com.lf.fashion.data.network.api.MainHomeApi
import com.lf.fashion.data.response.*
import javax.inject.Inject

class HomeRepository @Inject constructor(
    private val postApi: MainHomeApi,
    private val chipApi: ChipApi
) : SafeApiCall {


    suspend fun getRandomPost(sex: String, take: Int) = safeApiCall {
        postApi.getRandomPost(sex, take)
    }

    suspend fun getRandomPostPublic(sex: String, take: Int) = safeApiCall {
        postApi.getRandomPostPublic(sex, take)
    }

    /*테스트를 위해 우선 postId 1로 하드코딩 ! */
    suspend fun getPostInfoByPostId(postId: Int) = safeApiCall {
        postApi.getPostInfoById(1)
    }

    suspend fun getRecommendClothesTop3(postId: Int, category: String) = safeApiCall {
        postApi.getRecommendTopClothes(1)

    }

    suspend fun getRecommendClothesInfo(postId: Int, category: String) = safeApiCall {
        postApi.getRecommendClothesInfo(1)
    }

    //filter fragment
    suspend fun getTPOChips(): List<ChipInfo> {
        return chipApi.getTPOChips()
    }

    suspend fun getSeasonChips(): List<ChipInfo> {
        return chipApi.getSeasonChips()
    }

    suspend fun getStyleChips(): List<ChipInfo> {
        return chipApi.getStyleChips()
    }
}