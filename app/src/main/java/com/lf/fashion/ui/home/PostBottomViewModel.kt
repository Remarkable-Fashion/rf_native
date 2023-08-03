package com.lf.fashion.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lf.fashion.data.network.Resource
import com.lf.fashion.data.network.api.CommunicateApi
import com.lf.fashion.data.repository.CommunicateRepository
import com.lf.fashion.data.response.MsgResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostBottomViewModel @Inject constructor(
    private val communicateRepository: CommunicateRepository
) : ViewModel() {

    private val _blockResponse = MutableLiveData<Resource<MsgResponse>>()
    val blockResponse = _blockResponse

    private val _scrapResponse = MutableLiveData<Resource<MsgResponse>>()
    val scrapResponse = _scrapResponse

    private val _followResponse = MutableLiveData<Resource<MsgResponse>>()
    val followResponse = _followResponse

    fun changeBlockUserState(create: Boolean, userId: Int) {
        viewModelScope.launch {
            if(create){
                _blockResponse.value = communicateRepository.blockUser(userId)
            }else{
                _blockResponse.value = communicateRepository.deleteBlock(userId)
            }
        }
    }

    fun changeScrapState(create : Boolean, postId : Int){
        viewModelScope.launch {
            if(create){
                _scrapResponse.value =  communicateRepository.createScrap(postId)
            }else{
                _scrapResponse.value = communicateRepository.deleteScrap(postId)
            }
        }
    }

    fun changeFollowingState(create : Boolean, userId : Int){
        viewModelScope.launch {
            if(create){
                followResponse.value = communicateRepository.createFollowing(userId)
            }else{
                followResponse.value = communicateRepository.deleteFollowing(userId)
            }
        }
    }
}