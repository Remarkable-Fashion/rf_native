package com.lf.fashion.data.repository

import com.lf.fashion.data.network.api.PostApi
import com.lf.fashion.data.network.api.test.ChipTestApi
import com.lf.fashion.data.network.api.test.PhotoTestApi
import com.lf.fashion.data.response.*
import javax.inject.Inject

class HomeRepository @Inject constructor(
    //private val photoTestApi : PhotoTestApi,
    private val postApi: PostApi,
    private val chipTestApi: ChipTestApi
) {


    /*   suspend fun getTestPostList() : List<Post> {
           return photoTestApi.getTestImages()
       }
   */
    suspend fun getChipInfo(): List<ChipInfo> {
        return chipTestApi.getFilterInfo()
    }

    suspend fun getUserInfoAndStyle(): TestUserInfo {
        return chipTestApi.getUserInfoAndStyle()
    }

    suspend fun getLookBook(): List<LookBook> {
        return chipTestApi.getLookBook()
    }


    /*dev */
    suspend fun getRandomPost(sex: String): List<RandomPostResponse> {
        return postApi.getRandomPost(sex)
    }

    suspend fun getRandomPostPublic(sex: String): List<RandomPostResponse> {
        return postApi.getRandomPostPublic(sex)
    }
}