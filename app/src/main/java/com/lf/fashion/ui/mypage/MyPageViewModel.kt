package com.lf.fashion.ui.mypage

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lf.fashion.TAG
import com.lf.fashion.data.common.Event
import com.lf.fashion.data.common.UserDataStorePref
import com.lf.fashion.data.network.Resource
import com.lf.fashion.data.repository.CommunicateRepository
import com.lf.fashion.data.repository.MyPageRepository
import com.lf.fashion.data.model.FollowerUserList
import com.lf.fashion.data.model.FollowingUserList
import com.lf.fashion.data.model.MsgResponse
import com.lf.fashion.data.model.MyBlockUserList
import com.lf.fashion.data.model.MyInfo
import com.lf.fashion.data.model.Posts
import com.lf.fashion.data.model.Profile
import com.lf.fashion.data.model.RandomPostResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val myPageRepository: MyPageRepository,
    private val communicateRepository: CommunicateRepository,
    @ApplicationContext context: Context
) : ViewModel() {

    private var _savedLoginToken: MutableLiveData<String?> = MutableLiveData()
    val savedLoginToken: LiveData<String?> = _savedLoginToken
    private val userPreferences = UserDataStorePref(context)

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

    private val _deleteUser = MutableLiveData<Resource<MsgResponse>>()
    val deleteUser: LiveData<Resource<MsgResponse>> = _deleteUser

    var havetoRefresh = MutableLiveData<Boolean>()

    //새로 load 된 post 들까지 합쳐진 전체 itemList
    var allPostList = mutableListOf<Posts>()
    var recentResponse: RandomPostResponse? = null

    var myInfoChaged  = false

    init {
        getSavedLoginToken()
        if (!savedLoginToken.value.isNullOrEmpty()) {
            getPostList()
            getMyInfo()
        }
    }

    suspend fun getJWT(loginAccessToken: String,fcmToken : String): Resource<MsgResponse> {
        return myPageRepository.getJWT(loginAccessToken,fcmToken)
    }

    fun getSavedLoginToken() {
        viewModelScope.launch {
            _savedLoginToken.value = userPreferences.accessToken.first()
            // Log.d(TAG, "MyPageViewModel - getSavedLoginToken: ${savedLoginToken.value}");
        }
    }

    fun clearSavedLoginToken() {
        viewModelScope.launch {
            userPreferences.clearAccessTokenAndId()
            _savedLoginToken.value = null
        }
        //Log.d(TAG, "MyPageViewModel - clearSavedLoginToken: ${savedLoginToken.value}");
    }

    fun getMyInfo() {
        viewModelScope.launch {
            val response = myPageRepository.getMyInfo()
            _myInfo.value = if(response is Resource.Success) response.value else MyInfo(-1,
                Profile(null,null,null,null,null),null,null)
            // 로그인 만료시 home 에서 handle 처리는 해두었지만, 혹시 모를 401 오류를 대비한 빈값 세팅
        }
    }

    fun getPostList() {
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
        height: Int?,
        weight: Int?,
        name:String?,
        introText: String?
    ) {
        viewModelScope.launch {
            _updateProfileResponse.value = myPageRepository.updateMyProfile(
                profileImage,
                sex,
                height,
                weight,
                name,
                introText
            )
        }
    }
    fun getMyFollowings() {
        viewModelScope.launch {
            _myFollowings.value = myPageRepository.getMyFollowings()
        }
    }

    fun getMyFollowers() {
        viewModelScope.launch {
            _myFollowers.value = myPageRepository.getMyFollowers()
        }
    }

    fun getMyBlockUsers() {
        viewModelScope.launch {
            _myBlockUsers.value = myPageRepository.getMyBlockUser()
        }
    }

    suspend fun changeFollowingState(create: Boolean, userId: Int): Resource<MsgResponse> {
        val result = if (create) {
            communicateRepository.createFollowing(userId)
        } else {
            communicateRepository.deleteFollowing(userId)
        }

        return result

    }

    suspend fun changeBlockUserState(create: Boolean, userId: Int): Resource<MsgResponse> {
        val result: Resource<MsgResponse> = if (create) {
            communicateRepository.blockUser(userId)
        } else {
            communicateRepository.deleteBlock(userId)
        }

        return result
    }

    suspend fun deletePost(postId: Int): MsgResponse {
        val response = myPageRepository.deletePost(postId)
        return if (response is Resource.Success) return response.value
        else MsgResponse(false, "Resource Fail")
    }

    suspend fun changePostStatus(postInt: Int, status: Boolean): MsgResponse {
        val response = myPageRepository.updatePostStatus(postInt, !status)
        return if (response is Resource.Success) return response.value
        else MsgResponse(false, "Resource Fail")
    }

    suspend fun deleteFollowerById(userId: Int): MsgResponse {
        val response = myPageRepository.deleteFollowerByUserId(userId)
        return if(response is Resource.Success) return response.value
        else MsgResponse(false ,"Resource Fail")
    }

    fun deleteUser(){
        viewModelScope.launch {
            _deleteUser.value = myPageRepository.deleteUser()
        }
    }
}