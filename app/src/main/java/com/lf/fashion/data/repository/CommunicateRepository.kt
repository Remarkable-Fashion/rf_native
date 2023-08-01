package com.lf.fashion.data.repository

import com.lf.fashion.data.network.SafeApiCall
import com.lf.fashion.data.network.api.CommunicateApi
import javax.inject.Inject

class CommunicateRepository @Inject constructor(
    private val comApi: CommunicateApi
) : SafeApiCall {


    suspend fun createLike(postId : Int) = safeApiCall {
        comApi.createLike(postId)
    }

    suspend fun deleteLike(postId : Int)=safeApiCall {
        comApi.deleteLike(postId)
    }

    suspend fun createScrap(postId: Int) =safeApiCall {
        comApi.createScrap(postId)
    }

    suspend fun deleteScrap(postId: Int) = safeApiCall {
        comApi.deleteScrap(postId)
    }

    suspend fun createFollowing(userId: Int) =safeApiCall {
        comApi.createFollowing(userId)
    }

    suspend fun deleteFollowing(userId: Int) = safeApiCall {
        comApi.deleteFollowing(userId)
    }

    suspend fun blockUser(userId: Int) = safeApiCall {
        comApi.blockUser(userId)
    }

    suspend fun deleteBlock(userId: Int) = safeApiCall {
        comApi.deleteBlock(userId)
    }
}