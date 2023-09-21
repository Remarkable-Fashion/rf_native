package com.lf.fashion.data.repository

import android.util.Log
import com.lf.fashion.TAG
import com.lf.fashion.data.model.UploadPost
import com.lf.fashion.data.network.SafeApiCall
import com.lf.fashion.data.network.api.EditPostApi
import com.lf.fashion.ui.common.getMimeType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

class EditPostRepository @Inject constructor(private val editPostApi: EditPostApi) : SafeApiCall {
    suspend fun editPost(uploadPost: UploadPost) = safeApiCall{
        editPostApi.editPost()
    }

    suspend fun uploadNewPostImages(postImages: List<String>) = safeApiCall {
        val partBody : MutableList<MultipartBody.Part> = mutableListOf()

        for(image in postImages) {
            val file = File(image)
            val mimeType = getMimeType(file)
            val requestFile = file.asRequestBody(mimeType?.toMediaTypeOrNull())
            val part = MultipartBody.Part.createFormData("posts", file.name, requestFile)
            partBody.add(part)
            Log.d(TAG, "uploadClothesImageRepo - mimeType $mimeType ");
            Log.d(TAG, "uploadClothesImageRepo - updateMyProfile: ${file.name}");
            Log.d(TAG, "uploadClothesImageRepo - file ?? : ${requestFile.contentType()}")
        }
        editPostApi.uploadNewPostImage(partBody)
    }
    suspend fun uploadClothesImages(clothImages : List<String>) =safeApiCall {
        val partBody : MutableList<MultipartBody.Part> = mutableListOf()

        for(image in clothImages) {
            val file = File(image)
            val mimeType = getMimeType(file)
            val requestFile = file.asRequestBody(mimeType?.toMediaTypeOrNull())
            val part = MultipartBody.Part.createFormData("clothes", file.name, requestFile)
            partBody.add(part)
            Log.d(TAG, "uploadClothesImageRepo - mimeType $mimeType ");
            Log.d(TAG, "uploadClothesImageRepo - updateMyProfile: ${file.name}");
            Log.d(TAG, "uploadClothesImageRepo - file ?? : ${requestFile.contentType()}")
        }
        editPostApi.uploadNewClothImages(partBody)
    }

    suspend fun getPostInfo(postId : Int) = safeApiCall {
        editPostApi.getPostInfoByPostId(postId)
    }
}