package com.lf.fashion.ui.addPost

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lf.fashion.data.model.ChipInfo
import com.lf.fashion.data.model.Cloth
import com.lf.fashion.data.model.MsgResponse
import com.lf.fashion.data.model.UploadPost
import com.lf.fashion.data.network.Resource
import com.lf.fashion.data.repository.FilterRepository
import com.lf.fashion.data.repository.UploadPostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UploadPostViewModel @Inject constructor(
    private val filterRepository: FilterRepository,
    private val uploadPostRepository: UploadPostRepository
) : ViewModel() {
    private val _tpoChipList = MutableLiveData<List<ChipInfo>>()
    var tpoChipList: LiveData<List<ChipInfo>> = _tpoChipList

    private val _seasonChipList = MutableLiveData<List<ChipInfo>>()
    var seasonChipList: LiveData<List<ChipInfo>> = _seasonChipList

    private val _styleChipList = MutableLiveData<List<ChipInfo>>()
    var styleChipList: LiveData<List<ChipInfo>> = _styleChipList

    var selectedGender :String?=null

    val selectedTpos: MutableList<ChipInfo> = mutableListOf()
    val selectedSeasons : MutableList<ChipInfo> = mutableListOf()
    val selectedStyles : MutableList<ChipInfo> = mutableListOf()/*
    var tposTexts: MutableList<String> = mutableListOf()
    var seasonsTexts : MutableList<String> = mutableListOf()
    var stylesTexts : MutableList<String> = mutableListOf()
*/
    var selectedClothCategory : String? =null
    var selectedPostImages : MutableList<String > = mutableListOf()
    var uploadedClothes : MutableList<Cloth> = mutableListOf()


    init {
        getTPOChipsInfo()
        getSeasonChipsInfo()
        getStyleChipsInfo()
    }
    private fun getTPOChipsInfo(){
        viewModelScope.launch {
            _tpoChipList.value = filterRepository.getTPOChips()
        }
    }
    private fun getSeasonChipsInfo(){
        viewModelScope.launch {
            _seasonChipList.value = filterRepository.getSeasonChips()
        }
    }
    private fun getStyleChipsInfo(){
        viewModelScope.launch {
            _styleChipList.value = filterRepository.getStyleChips()
        }
    }

    suspend fun uploadPostInfo(uploadPost: UploadPost) : MsgResponse{
        val response = uploadPostRepository.uploadPost(uploadPost)
        return if(response is Resource.Success) response.value
        else MsgResponse(false , "Resource Fail")
    }

    suspend fun uploadPostImages(images : List<String>) : MsgResponse{
        val response = uploadPostRepository.uploadPostImages(images)
        return if(response is Resource.Success) response.value
        else MsgResponse(false ,"Resource Fail")
    }

    suspend fun uploadClothImages(images : List<String>):MsgResponse{
        val response = uploadPostRepository.uploadClothesImages(images)
        return if(response is Resource.Success) response.value
        else MsgResponse(false,"Resource Fail")
    }
}