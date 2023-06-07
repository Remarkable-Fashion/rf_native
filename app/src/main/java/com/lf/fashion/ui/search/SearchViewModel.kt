package com.lf.fashion.ui.search

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lf.fashion.TAG
import com.lf.fashion.data.repository.HomeRepository
import com.lf.fashion.data.response.Post
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(private val homeRepository: HomeRepository) :
    ViewModel() {
    private val _gridMode = MutableLiveData<Int>()
    val gridMode: LiveData<Int> = _gridMode

    //지금은 POST LIST 하나로 LOOK ITEM 모두 테스트 돌리는 중 ~
    private val _postList = MutableLiveData<List<Post>>()
    var postList: LiveData<List<Post>> = _postList

    init {
        getPostList()
    }

    private fun getPostList() {
        viewModelScope.launch {
            _postList.value = homeRepository.getTestPostList()
        }
    }

    fun setGridMode(mode: Int) {
        _gridMode.value = mode
        Log.d(TAG, "SearchViewModel - setGridMode: ${gridMode.value}");
    }
}