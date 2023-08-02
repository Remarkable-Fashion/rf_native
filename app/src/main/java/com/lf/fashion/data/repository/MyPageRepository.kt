package com.lf.fashion.data.repository

import com.lf.fashion.data.network.SafeApiCall
import com.lf.fashion.data.network.api.JWTApi
import com.lf.fashion.data.network.api.MyPageApi
import com.lf.fashion.data.response.MsgResponse
import com.lf.fashion.data.response.MyInfo
import com.lf.fashion.data.response.RandomPostResponse
import com.lf.fashion.data.response.UpdateMyInfo
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject
import kotlin.math.log

class MyPageRepository @Inject constructor(
    private val jwtApi: JWTApi,
    private val myPageApi: MyPageApi
) : SafeApiCall {

    suspend fun getJWT(loginAccessToken: String) = safeApiCall {
        jwtApi.getJWT(loginAccessToken)
    }

    suspend fun getMyInfo(): MyInfo {
        return myPageApi.getMyPageInfo()
    }

    suspend fun getMyPost(nextCursor: Int? = null) = safeApiCall {
        myPageApi.getMyPagePost(nextCursor)
    }

    suspend fun updateMyProfile(
        profileImage: File?,
        sex: String?,
        height: String?,
        weight: String?,
        introduction: String?
    ) = safeApiCall {
        val sexRequestBody = sex?.toRequestBody("text/plain".toMediaTypeOrNull())
        val heightRequestBody = height?.toRequestBody("text/plain".toMediaTypeOrNull())
        val weightRequestBody =weight?.toRequestBody("text/plain".toMediaTypeOrNull())
        val introductionRequestBody = introduction?.toRequestBody("text/plain".toMediaTypeOrNull())

        myPageApi.updateProfileInfo(
            profileImage,
            sexRequestBody,
            heightRequestBody,
            weightRequestBody,
            introductionRequestBody
        )
    }
}