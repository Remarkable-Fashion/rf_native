package com.lf.fashion.ui.home.photozip

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lf.fashion.data.network.Resource
import com.lf.fashion.data.repository.CommunicateRepository
import com.lf.fashion.data.repository.HomeRepository
import com.lf.fashion.data.repository.OtherUserInfoRepository
import com.lf.fashion.data.response.MsgResponse
import com.lf.fashion.data.response.OtherUserInfo
import com.lf.fashion.data.response.RandomPostResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PhotoZipViewModel @Inject constructor(
    private val otherUserInfoRepository: OtherUserInfoRepository,
    private val communicateRepository: CommunicateRepository
) :ViewModel(){
    private val _posts = MutableLiveData<Resource<RandomPostResponse>>()
    var posts: LiveData<Resource<RandomPostResponse>> = _posts

    private val _followResponse = MutableLiveData<Resource<MsgResponse>>()
    val followResponse = _followResponse

    private val _startIndex = MutableLiveData<Int>()
    var startIndex: MutableLiveData<Int> = _startIndex

    private val _changeLikeResponse = MutableLiveData<Resource<MsgResponse>>()
    var changeLikeResponse = _changeLikeResponse

    private val _scrapResponse = MutableLiveData<Resource<MsgResponse>>()
    val scrapResponse = _scrapResponse

    private var _profileInfo = MutableLiveData<Resource<OtherUserInfo>>()
    val profileInfo: LiveData<Resource<OtherUserInfo>> = _profileInfo
    fun getPostByUserId(userId: Int) {
        viewModelScope.launch {
            _posts.value = otherUserInfoRepository.getPostByUserId(userId)
        }
    }

    fun getProfileInfoByUserId(userId : Int) {
        viewModelScope.launch {
            _profileInfo.value =
                otherUserInfoRepository.getUserProfileInfo(userId)
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
    fun editClickedPostIndex(postIndex: Int) {
        _startIndex.value = postIndex
    }


    fun changeLikesState(create: Boolean, postId: Int) {
        viewModelScope.launch {
            if (create) {
                _changeLikeResponse.value = communicateRepository.createLike(postId)
            } else {
                _changeLikeResponse.value = communicateRepository.deleteLike(postId)
            }
        }
    }

    fun changeScrapState(create: Boolean, postId: Int) {
        viewModelScope.launch {
            if (create) {
                _scrapResponse.value = communicateRepository.createScrap(postId)
            } else {
                _scrapResponse.value = communicateRepository.deleteScrap(postId)
            }
        }
    }
}