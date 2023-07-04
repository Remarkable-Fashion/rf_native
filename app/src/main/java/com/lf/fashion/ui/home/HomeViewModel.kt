package com.lf.fashion.ui.home

import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import com.lf.fashion.TAG
import com.lf.fashion.data.common.PreferenceManager
import com.lf.fashion.data.repository.HomeRepository
import com.lf.fashion.data.response.RandomPostResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val homeRepository: HomeRepository,@ApplicationContext context : Context) : ViewModel() {

    private val _postList = MutableLiveData<List<RandomPostResponse>>()
    var postList: LiveData<List<RandomPostResponse>> = _postList
    private val userPreferences = PreferenceManager(context)


    init {
        getPostList()
    }

    private fun getPostList() {
        Log.d(TAG, "suspend getPostList 호출 ")
        viewModelScope.launch {
            val savedToken = userPreferences.accessToken.last()
            if(savedToken.isNullOrEmpty()){
                _postList.value = homeRepository.getRandomPostPublic("Male")
            }else{
                _postList.value = homeRepository.getRandomPost("Male")
            }
        }
    }
}