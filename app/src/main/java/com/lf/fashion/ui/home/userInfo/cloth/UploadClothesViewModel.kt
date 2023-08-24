package com.lf.fashion.ui.home.userInfo.cloth

import androidx.lifecycle.ViewModel
import com.lf.fashion.data.network.Resource
import com.lf.fashion.data.repository.UploadClothesRepository
import com.lf.fashion.data.response.MsgResponse
import com.lf.fashion.data.response.UploadCloth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UploadClothesViewModel @Inject constructor(private val uploadClothesRepository: UploadClothesRepository) :
    ViewModel() {

    var selectedCategory : String? =null
    suspend fun uploadClothesInfo(pageClothesId: Int, cloth: UploadCloth): MsgResponse {
        val response = uploadClothesRepository.uploadClothesInfo(pageClothesId, cloth)
        return if (response is Resource.Success) {
            response.value
        } else MsgResponse(false, "Resource Fail")
    }

    suspend fun uploadClothesImage(clothesImage: String) : MsgResponse{

        val response = uploadClothesRepository.uploadClothesImage(clothesImage)
        return if (response is Resource.Success){
            response.value
        }else MsgResponse(false , "Resource Fail")
    }
}