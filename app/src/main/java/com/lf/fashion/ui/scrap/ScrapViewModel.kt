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

    private val _postResponse = MutableLiveData<Resource<RandomPostResponse>>()
    var postResponse: LiveData<Resource<RandomPostResponse>> = _postResponse

    private val _startIndex = MutableLiveData<Int>()
    var startIndex : MutableLiveData<Int> = _startIndex

    init {
        getPostList()
    }

    private fun getPostList() {
        viewModelScope.launch {
            _postResponse.value = scrapRepository.getScrapPosts()
            Log.d(TAG, "ScrapViewModel - getPostList: ${_postResponse.value}");
        }
    }
    fun editClickedPostIndex(postIndex: Int) {
        _startIndex.value = postIndex
    }
}