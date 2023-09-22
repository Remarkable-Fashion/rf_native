package com.lf.fashion.data.repository

import android.util.Log
import com.lf.fashion.TAG
import com.lf.fashion.data.network.SafeApiCall
import com.lf.fashion.data.network.api.JWTApi
import com.lf.fashion.data.network.api.MyPageApi
import com.lf.fashion.data.model.MyInfo
import com.lf.fashion.data.model.PostStatus
import com.lf.fashion.ui.common.getMimeType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

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

    suspend fun getMyFollowings() = safeApiCall {
        myPageApi.getMyFollowing()
    }
    suspend fun getMyFollowers() = safeApiCall {
        myPageApi.getMyFollowers()
    }
    suspend fun getMyBlockUser() = safeApiCall {
        myPageApi.getMyBlockUser()
    }

    suspend fun updateMyProfile(
        profileImagePath: String?,
        sex: String?,
        height: String?,
        weight: String?,
        introduction: String?
    ) = safeApiCall {
        val sexRequestBody = sex?.toRequestBody("text/plain".toMediaTypeOrNull())
        val heightRequestBody = height?.toRequestBody("text/plain".toMediaTypeOrNull())
        val weightRequestBody = weight?.toRequestBody("text/plain".toMediaTypeOrNull())
        val introductionRequestBody = introduction?.toRequestBody("text/plain".toMediaTypeOrNull())

       var partBody : MultipartBody.Part? = null
        profileImagePath?.let {
            val file = File(profileImagePath)
            // 파일 확장자를 기반으로 MIME 타입을 추론하여 전달
            val mimeType = getMimeType(file)
            val requestFile = file.asRequestBody(mimeType.toMediaTypeOrNull())
            partBody = MultipartBody.Part.createFormData("avartar", file.name, requestFile)


            Log.d(TAG, "MyPageRepository - mimeType $mimeType ");
            Log.d(TAG, "MyPageRepository - updateMyProfile: ${file.name}");
            Log.d(TAG, "MyPageRepository - file ?? : ${requestFile.contentType()}");
        }

        myPageApi.updateProfileInfo(
            partBody,
            sexRequestBody,
            heightRequestBody,
            weightRequestBody,
            introductionRequestBody
        )
    }

    suspend fun deletePost(postId : Int) = safeApiCall {
        myPageApi.deletePost(postId)
    }

    suspend fun updatePostStatus(postId: Int,status : Boolean)=safeApiCall {
        myPageApi.updatePostStatus(postId, PostStatus(status))
    }

    suspend fun deleteFollowerByUserId(userId : Int)=safeApiCall{
        myPageApi.deleteFollowerById(userId)
    }
}