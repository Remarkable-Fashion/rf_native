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
import com.lf.fashion.data.repository.MyPageRepository
import com.lf.fashion.data.response.MsgResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(private val myPageRepository: MyPageRepository,@ApplicationContext context: Context): ViewModel(){
    private var _savedLoginToken : MutableLiveData<String?> = MutableLiveData()
    val savedLoginToken : LiveData<String?> = _savedLoginToken
    private val userPreferences = PreferenceManager(context)

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
}