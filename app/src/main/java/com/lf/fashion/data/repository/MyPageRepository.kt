package com.lf.fashion.data.repository

import android.util.Log
import android.webkit.MimeTypeMap
import com.lf.fashion.TAG
import com.lf.fashion.data.network.SafeApiCall
import com.lf.fashion.data.network.api.JWTApi
import com.lf.fashion.data.network.api.MyPageApi
import com.lf.fashion.data.response.MyInfo
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
            val requestFile = file.asRequestBody(mimeType?.toMediaTypeOrNull())
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
    // 파일 확장자로부터 MIME 타입을 추론하는 함수
    private fun getMimeType(file: File): String? {
        val extension = MimeTypeMap.getFileExtensionFromUrl(file.absolutePath)
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
    }
}