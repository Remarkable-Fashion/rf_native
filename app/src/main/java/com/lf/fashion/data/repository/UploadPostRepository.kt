package com.lf.fashion.data.repository

import android.util.Log
import com.lf.fashion.TAG
import com.lf.fashion.data.model.UploadPost
import com.lf.fashion.data.network.SafeApiCall
import com.lf.fashion.data.network.api.UploadPostApi
import com.lf.fashion.ui.common.getMimeType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

class UploadPostRepository @Inject constructor(private val uploadPostApi: UploadPostApi) :SafeApiCall{

    suspend fun uploadPost(uploadPost: UploadPost) = safeApiCall{
        uploadPostApi.uploadPost(uploadPost)
    }

    suspend fun uploadPostImages(postImages: List<String>) = safeApiCall {
        val partBody : MutableList<MultipartBody.Part> = mutableListOf()

        for(image in postImages) {
            val file = File(image)
            val mimeType = getMimeType(file)
            val requestFile = file.asRequestBody(mimeType?.toMediaTypeOrNull())
            val part = MultipartBody.Part.createFormData("posts", file.name, requestFile)
            partBody.add(part)

        }
        uploadPostApi.uploadPostImages(partBody)

    }

    suspend fun uploadClothesImages(clothImages : List<String>) =safeApiCall {
        val partBody : MutableList<MultipartBody.Part> = mutableListOf()

        for(image in clothImages) {
            val file = File(image)
            val mimeType = getMimeType(file)
            val requestFile = file.asRequestBody(mimeType?.toMediaTypeOrNull())
            val part = MultipartBody.Part.createFormData("clothes", file.name, requestFile)
            partBody.add(part)

        }
        uploadPostApi.uploadClothImages(partBody)
    }


}