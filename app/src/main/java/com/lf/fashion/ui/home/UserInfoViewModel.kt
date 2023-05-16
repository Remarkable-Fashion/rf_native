package com.lf.fashion.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lf.fashion.data.common.Event
import com.lf.fashion.data.repository.HomeRepository
import com.lf.fashion.data.response.LookBook
import com.lf.fashion.data.response.UserInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserInfoViewModel @Inject constructor(private val repository: HomeRepository) : ViewModel() {
    private val _userInfo = MutableLiveData<Event<UserInfo>>()
    var userInfo: LiveData<Event<UserInfo>> = _userInfo

    private val _lookBook = MutableLiveData<List<LookBook>>()
    var lookBook: LiveData<List<LookBook>> = _lookBook

    fun getUserInfoAndStyle() {
        viewModelScope.launch {
            _userInfo.value = Event(repository.getUserInfoAndStyle())

        }
    }

    fun getLookBook() {
        viewModelScope.launch {
            _lookBook.value = repository.getLookBook()
        }
    }
}