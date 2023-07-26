package com.lf.fashion.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lf.fashion.data.common.Event
import com.lf.fashion.data.network.Resource
import com.lf.fashion.data.repository.HomeRepository
import com.lf.fashion.data.response.LookBook
import com.lf.fashion.data.response.PostInfo
import com.lf.fashion.data.response.RecommendCloth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserInfoViewModel @Inject constructor(private val repository: HomeRepository) : ViewModel() {
    private val _userInfo = MutableLiveData<Event<Resource<PostInfo>>>()
    var userInfo: LiveData<Event<Resource<PostInfo>>> = _userInfo

    private val _lookBook = MutableLiveData<Resource<RecommendCloth>>()
    var lookBook: LiveData<Resource<RecommendCloth>> = _lookBook

    private val _topLook = MutableLiveData<Resource<RecommendCloth>>()
    var topLook : LiveData<Resource<RecommendCloth>> = _topLook

    fun getUserInfoAndStyle(postId :Int) {
        viewModelScope.launch {
            _userInfo.value = Event(repository.getPostInfoByPostId(postId))

        }
    }

    fun getLookBook(postId: Int , category : String) {
        viewModelScope.launch {
            _lookBook.value = repository.getRecommendClothesInfo(postId,category)
        }
    }

    fun getTopLook(postId: Int,category: String){
        viewModelScope.launch {
            _topLook.value = repository.getRecommendClothesTop3(postId,category)
        }
    }
}