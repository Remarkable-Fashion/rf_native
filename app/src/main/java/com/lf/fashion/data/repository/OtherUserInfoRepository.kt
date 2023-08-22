package com.lf.fashion.data.repository

import com.lf.fashion.data.network.SafeApiCall
import com.lf.fashion.data.network.api.UserProfileApi
import javax.inject.Inject

class OtherUserInfoRepository @Inject constructor(
    private val userProfileApi: UserProfileApi
) :SafeApiCall{

    suspend fun getUserProfileInfo(userId : Int) = safeApiCall {
        userProfileApi.getUserProfileInfo(userId)
    }

    suspend fun getFollowings(userId : Int) = safeApiCall {
        userProfileApi.getMyFollowing(userId)
    }

    suspend fun getFollowers(userId : Int) = safeApiCall {
        userProfileApi.getMyFollowers(userId)
    }

    suspend fun getPostByUserId ( userId : Int,nextCursor : Int?=null) = safeApiCall {
        userProfileApi.getPostInfoByUserId(userId,nextCursor)
    }
}