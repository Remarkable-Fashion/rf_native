package com.lf.fashion.ui.scrap

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lf.fashion.TAG
import com.lf.fashion.data.network.Resource
import com.lf.fashion.data.repository.ScrapRepository
import com.lf.fashion.data.response.RandomPostResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScrapViewModel @Inject constructor(private val scrapRepository: ScrapRepository) : ViewModel() {

    private val _postList = MutableLiveData<Resource<List<RandomPostResponse>>>()
    var postList: LiveData<Resource<List<RandomPostResponse>>> = _postList

    private val _startIndex = MutableLiveData<Int>()
    var startIndex : MutableLiveData<Int> = _startIndex

    init {
        getPostList()
    }

    private fun getPostList() {
        viewModelScope.launch {
            _postList.value = scrapRepository.getScrapPosts()
            Log.d(TAG, "ScrapViewModel - getPostList: ${_postList.value}");
        }
    }
    fun editClickedPostIndex(postIndex: Int) {
        _startIndex.value = postIndex
    }
}