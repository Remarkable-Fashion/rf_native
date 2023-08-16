package com.lf.fashion.ui.mypage

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lf.fashion.TAG
import com.lf.fashion.data.common.Event
import com.lf.fashion.data.common.PreferenceManager
import com.lf.fashion.data.network.Resource
import com.lf.fashion.data.repository.CommunicateRepository
import com.lf.fashion.data.repository.MyPageRepository
import com.lf.fashion.data.response.FollowerUserList
import com.lf.fashion.data.response.FollowingUserList
import com.lf.fashion.data.response.MsgResponse
import com.lf.fashion.data.response.MyBlockUserList
import com.lf.fashion.data.response.MyInfo
import com.lf.fashion.data.response.RandomPostResponse
import com.lf.fashion.data.response.UpdateMyInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val myPageRepository: MyPageRepository,
    private val communicateRepository: CommunicateRepository,
    @ApplicationContext context: Context
) : ViewModel() {

    private var _savedLoginToken: MutableLiveData<String?> = MutableLiveData()
    val savedLoginToken: LiveData<String?> = _savedLoginToken
    private val userPreferences = PreferenceManager(context)

    private val _postResponse = MutableLiveData<Resource<RandomPostResponse>>()
    var postResponse: LiveData<Resource<RandomPostResponse>> = _postResponse

    private val _morePost = MutableLiveData<Event<Resource<RandomPostResponse>>>()
    var morePost: MutableLiveData<Event<Resource<RandomPostResponse>>> = _morePost

    private var _myInfo = MutableLiveData<MyInfo>()
    val myInfo: LiveData<MyInfo> = _myInfo


    private val _startIndex = MutableLiveData<Int>()
    var startIndex: MutableLiveData<Int> = _startIndex

    private val _changeLikeResponse = MutableLiveData<Resource<MsgResponse>>()
    var changeLikeResponse = _changeLikeResponse

    private val _scrapResponse = MutableLiveData<Resource<MsgResponse>>()
    val scrapResponse = _scrapResponse

    private val _updateProfileResponse = MutableLiveData<Resource<MsgResponse>>()
    val updateProfileResponse = _updateProfileResponse

    private val _myFollowings = MutableLiveData<Resource<FollowingUserList>>()
    val myFollowings = _myFollowings

    private val _myFollowers = MutableLiveData<Resource<FollowerUserList>>()
    val myFollowers = _myFollowers

    private val _myBlockUsers = MutableLiveData<Resource<MyBlockUserList>>()
    val myBlockUsers = _myBlockUsers

    suspend fun getJWT(loginAccessToken: String): Resource<MsgResponse> {
        return myPageRepository.getJWT(loginAccessToken)
    }

    suspend fun getSavedLoginToken() {
        viewModelScope.launch {
            _savedLoginToken.value = userPreferences.accessToken.first()
            Log.d(TAG, "MyPageViewModel - getSavedLoginToken: ${savedLoginToken.value}");
        }
    }

    fun clearSavedLoginToken() {
        viewModelScope.launch {
            userPreferences.clearAccessTokenAndId()
            _savedLoginToken.value = null
        }
        Log.d(TAG, "MyPageViewModel - clearSavedLoginToken: ${savedLoginToken.value}");
    }

    fun getMyInfo() {
        viewModelScope.launch {
            _myInfo.value = myPageRepository.getMyInfo()
        }
    }

    fun getPostList() {
        Log.d(TAG, "suspend getPostList 호출 ")
        viewModelScope.launch {
            _postResponse.value = myPageRepository.getMyPost()
        }
    }

    fun getMorePostList(nextCursor: Int) {
        viewModelScope.launch {
            _morePost.value = Event(myPageRepository.getMyPost(nextCursor))

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

    fun updateMyProfile(
        profileImage: String?,
        sex: String?,
        height: String?,
        weight: String?,
        introText: String?
    ) {
        viewModelScope.launch {
            _updateProfileResponse.value = myPageRepository.updateMyProfile(
                profileImage,
                sex,
                height,
                weight,
                introText
            )
        }
    }

    fun getMyFollowings(){
        viewModelScope.launch {
            _myFollowings.value = myPageRepository.getMyFollowings()
        }
    }
    fun getMyFollowers(){
        viewModelScope.launch {
            _myFollowers.value = myPageRepository.getMyFollowers()
        }
    }
    fun getMyBlockUsers(){
        viewModelScope.launch {
            _myBlockUsers.value = myPageRepository.getMyBlockUser()
        }
    }
}