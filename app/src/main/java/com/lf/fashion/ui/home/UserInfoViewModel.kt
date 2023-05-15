package com.lf.fashion.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lf.fashion.data.repository.HomeRepository
import com.lf.fashion.data.response.UserInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserInfoViewModel @Inject constructor(private val repository: HomeRepository) : ViewModel() {
    private val _userInfo = MutableLiveData<UserInfo>()
    var userInfo: LiveData<UserInfo> = _userInfo

     fun getUserInfoAndStyle(){
        viewModelScope.launch {
            _userInfo.value = repository.getUserInfoAndStyle()
        }
    }
}