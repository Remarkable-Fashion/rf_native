package com.lf.fashion.ui.scrap

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lf.fashion.TAG
import com.lf.fashion.data.common.Event
import com.lf.fashion.data.network.Resource
import com.lf.fashion.data.repository.ScrapRepository
import com.lf.fashion.data.response.RandomPostResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScrapViewModel @Inject constructor(private val scrapRepository: ScrapRepository) : ViewModel() {

    private val _postResponse = MutableLiveData<Event<Resource<RandomPostResponse>>>()
    var postResponse: LiveData<Event<Resource<RandomPostResponse>>> = _postResponse

    private val _morePost = MutableLiveData<Event<Resource<RandomPostResponse>>>()
    var morePost : MutableLiveData<Event<Resource<RandomPostResponse>>> = _morePost

    private val _startIndex = MutableLiveData<Int>()
    var startIndex : MutableLiveData<Int> = _startIndex

    init {
        getPostList()
    }

    private fun getPostList() {
        viewModelScope.launch {
            _postResponse.value = Event(scrapRepository.getScrapPosts())
            Log.d(TAG, "ScrapViewModel - getPostList: ${_postResponse.value}");
        }
    }

    fun getMorePostList(nextCursor : Int) {
        viewModelScope.launch {
            _morePost.value = Event(scrapRepository.getScrapPosts(nextCursor))
            Log.d(TAG, "ScrapViewModel - getPostList: ${_postResponse.value}");
        }
    }

    fun editClickedPostIndex(postIndex: Int) {
        _startIndex.value = postIndex
    }
}