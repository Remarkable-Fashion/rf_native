package com.lf.fashion.data.repository

import android.util.Log
import com.lf.fashion.TAG
import com.lf.fashion.data.model.Cloth
import com.lf.fashion.data.network.SafeApiCall
import com.lf.fashion.data.network.api.UploadClothesApi
import com.lf.fashion.ui.common.getMimeType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

class UploadClothesRepository @Inject constructor(private val uploadClothesApi: UploadClothesApi) :
    SafeApiCall {

    suspend fun uploadClothesInfo(pageClothesId: Int, clothesInfo: Cloth) =safeApiCall{
        uploadClothesApi.uploadClothes(pageClothesId, clothesInfo)
    }

    suspend fun uploadClothesImage(clothesImage: String) =safeApiCall {
        var partBody : MultipartBody.Part? = null

        val file = File(clothesImage)
        val mimeType = getMimeType(file)
        val requestFile = file.asRequestBody(mimeType?.toMediaTypeOrNull())
        partBody = MultipartBody.Part.createFormData("clothes" , file.name , requestFile)

        uploadClothesApi.uploadClothesImages(partBody)
    }

}