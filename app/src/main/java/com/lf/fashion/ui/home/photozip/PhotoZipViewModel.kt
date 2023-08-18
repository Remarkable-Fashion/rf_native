package com.lf.fashion.ui.home.photozip

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lf.fashion.data.common.Event
import com.lf.fashion.data.network.Resource
import com.lf.fashion.data.repository.CommunicateRepository
import com.lf.fashion.data.repository.HomeRepository
import com.lf.fashion.data.response.MsgResponse
import com.lf.fashion.data.response.RandomPostResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PhotoZipViewModel @Inject constructor(
    private val homeRepository: HomeRepository,
    private val communicateRepository: CommunicateRepository
) :ViewModel(){
    private val _posts = MutableLiveData<Resource<RandomPostResponse>>()
    var posts: LiveData<Resource<RandomPostResponse>> = _posts

    private val _followResponse = MutableLiveData<Resource<MsgResponse>>()
    val followResponse = _followResponse

    fun getUserInfoAndStyle(userId: Int) {
        viewModelScope.launch {
            _posts.value = homeRepository.getPostByUserId(userId)

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