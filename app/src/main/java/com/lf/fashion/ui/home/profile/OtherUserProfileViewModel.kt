package com.lf.fashion.ui.home.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lf.fashion.data.common.Event
import com.lf.fashion.data.network.Resource
import com.lf.fashion.data.network.api.CommunicateApi
import com.lf.fashion.data.repository.CommunicateRepository
import com.lf.fashion.data.repository.OtherUserInfoRepository
import com.lf.fashion.data.response.FollowerUserList
import com.lf.fashion.data.response.FollowingUserList
import com.lf.fashion.data.response.MsgResponse
import com.lf.fashion.data.response.OtherUserInfo
import com.lf.fashion.data.response.RandomPostResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OtherUserProfileViewModel @Inject constructor(
    private val otherUserInfoRepository: OtherUserInfoRepository,
    private val communicateRepository: CommunicateRepository
) : ViewModel() {
    private val _postResponse = MutableLiveData<Resource<RandomPostResponse>>()
    var postResponse: LiveData<Resource<RandomPostResponse>> = _postResponse

    private val _morePost = MutableLiveData<Resource<RandomPostResponse>>()
    var morePost: MutableLiveData<Resource<RandomPostResponse>> = _morePost

    private var _profileInfo = MutableLiveData<Resource<OtherUserInfo>>()
    val profileInfo: LiveData<Resource<OtherUserInfo>> = _profileInfo

    private val _followingList = MutableLiveData<Resource<FollowingUserList>>()
    val followingList = _followingList

    private val _followerList = MutableLiveData<Resource<FollowerUserList>>()
    val followerList = _followerList

    private val _startIndex = MutableLiveData<Int>()
    var startIndex: MutableLiveData<Int> = _startIndex

    fun getPostByUserId(userId: Int) {
        viewModelScope.launch {
            _postResponse.value =
                otherUserInfoRepository.getPostByUserId(userId)
        }
    }

    fun getMorePostList(userId: Int, nextCursor: Int) {
        viewModelScope.launch {
            _morePost.value = otherUserInfoRepository.getPostByUserId(userId, nextCursor)
        }
    }

    fun getProfileInfo(userId: Int) {
        viewModelScope.launch {
            _profileInfo.value =
                otherUserInfoRepository.getUserProfileInfo(userId)
        }
    }

    fun editClickedPostIndex(postIndex: Int) {
        _startIndex.value = postIndex
    }

    fun getFollowingList(userId: Int) {
        viewModelScope.launch {
            _followingList.value =
                otherUserInfoRepository.getFollowings(userId)
        }
    }

    fun getFollowerList(userId: Int) {
        viewModelScope.launch {
            _followerList.value =
                otherUserInfoRepository.getFollowers(userId)
        }
    }

    suspend fun changeLikesState(create: Boolean, postId: Int): Resource<MsgResponse> {
        return if (create) {
            communicateRepository.createLike(postId)
        } else {
            communicateRepository.deleteLike(postId)
        }

    }

    suspend fun changeScrapState(create: Boolean, postId: Int): Resource<MsgResponse> {
        return if (create) {
            communicateRepository.createScrap(postId)
        } else {
            communicateRepository.deleteScrap(postId)
        }
    }


}