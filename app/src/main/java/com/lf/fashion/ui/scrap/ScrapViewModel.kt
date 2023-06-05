package com.lf.fashion.ui.scrap

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
class ScrapViewModel @Inject constructor(private val homeRepository: HomeRepository) : ViewModel() {

    private val _postList = MutableLiveData<List<Post>>()
    var postList: LiveData<List<Post>> = _postList

    private val _startIndex = MutableLiveData<Int>()
    var startIndex : LiveData<Int> = _startIndex

    init {
        getPostList()
    }

    private fun getPostList() {
        Log.d(TAG, "ScrapViewModel - getPostList: !!!");
        viewModelScope.launch {
            _postList.value = homeRepository.getTestPostList()
        }
    }
    fun editClickedPostIndex(postIndex: Int) {
        _startIndex.value = postIndex
    }
}