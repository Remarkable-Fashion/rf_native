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
import com.lf.fashion.data.repository.MyPageRepository
import com.lf.fashion.data.response.MsgResponse
import com.lf.fashion.data.response.MyInfo
import com.lf.fashion.data.response.RandomPostResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val myPageRepository: MyPageRepository,
    @ApplicationContext context: Context
) : ViewModel() {
    private var _savedLoginToken: MutableLiveData<String?> = MutableLiveData()
    val savedLoginToken: LiveData<String?> = _savedLoginToken
    private val userPreferences = PreferenceManager(context)

    private val _postResponse = MutableLiveData<Event<Resource<RandomPostResponse>>>()
    var postResponse: LiveData<Event<Resource<RandomPostResponse>>> = _postResponse

    private val _morePost = MutableLiveData<Event<Resource<RandomPostResponse>>>()
    var morePost: MutableLiveData<Event<Resource<RandomPostResponse>>> = _morePost

    private var _myInfo = MutableLiveData<MyInfo>()
    val myInfo: LiveData<MyInfo> = _myInfo

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
            userPreferences.clearAccessToken()
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
            _postResponse.value = Event(myPageRepository.getMyPost())
        }
    }

    fun getMorePostList(nextCursor: Int) {
        viewModelScope.launch {
            _morePost.value = Event(myPageRepository.getMyPost(nextCursor))

        }
    }
}