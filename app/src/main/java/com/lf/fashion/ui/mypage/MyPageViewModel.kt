package com.lf.fashion.ui.mypage

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lf.fashion.TAG
import com.lf.fashion.data.common.PreferenceManager
import com.lf.fashion.data.network.Resource
import com.lf.fashion.data.repository.HomeRepository
import com.lf.fashion.data.repository.MyPageRepository
import com.lf.fashion.data.response.MsgResponse
import com.lf.fashion.data.response.Post
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(private val myPageRepository: MyPageRepository,private val homeRepository: HomeRepository,@ApplicationContext context: Context): ViewModel(){
    private var _savedLoginToken : MutableLiveData<String?> = MutableLiveData()
    val savedLoginToken : LiveData<String?> = _savedLoginToken
    private val userPreferences = PreferenceManager(context)

    private val _postList = MutableLiveData<List<Post>>()
    var postList: LiveData<List<Post>> = _postList

    suspend fun getJWT(loginAccessToken : String) : Resource<MsgResponse> {
        return myPageRepository.getJWT(loginAccessToken)
    }

    suspend fun getSavedLoginToken (){
        viewModelScope.launch {
            _savedLoginToken.value = userPreferences.accessToken.first()
            Log.d(TAG, "MyPageViewModel - getSavedLoginToken: ${savedLoginToken.value}");
        }
    }
    fun clearSavedLoginToken(){
        viewModelScope.launch {
            userPreferences.clearAccessToken()
            _savedLoginToken.value = null
        }
        Log.d(TAG, "MyPageViewModel - clearSavedLoginToken: ${savedLoginToken.value}");
    }

     fun getPostList() {
        Log.d(TAG, "suspend getPostList 호출 ")
        viewModelScope.launch {
            _postList.value = homeRepository.getTestPostList()
        }
    }
}