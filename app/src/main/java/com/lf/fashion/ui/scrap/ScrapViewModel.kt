package com.lf.fashion.ui.scrap

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lf.fashion.TAG
import com.lf.fashion.data.common.Event
import com.lf.fashion.data.network.Resource
import com.lf.fashion.data.repository.CommunicateRepository
import com.lf.fashion.data.repository.ScrapRepository
import com.lf.fashion.data.model.MsgResponse
import com.lf.fashion.data.model.RandomPostResponse
import com.lf.fashion.data.repository.MyPageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScrapViewModel @Inject constructor(
    private val scrapRepository: ScrapRepository,
    private val communicateRepository: CommunicateRepository,
    private val myPageRepository: MyPageRepository
) : ViewModel() {

    private val _postResponse = MutableLiveData<Resource<RandomPostResponse>>()
    var postResponse: LiveData<Resource<RandomPostResponse>> = _postResponse

    private val _morePost = MutableLiveData<Resource<RandomPostResponse>>()
    var morePost: MutableLiveData<Resource<RandomPostResponse>> = _morePost

    private val _startIndex = MutableLiveData<Int>()
    var startIndex: MutableLiveData<Int> = _startIndex

    private val _changeLikeResponse = MutableLiveData<Resource<MsgResponse>>()
    var changeLikeResponse = _changeLikeResponse

    private val _scrapResponse = MutableLiveData<Resource<MsgResponse>>()
    val scrapResponse = _scrapResponse

    /*   init {
           getPostList()
       }
   */
    fun getPostList() {
        viewModelScope.launch {
            _postResponse.value = scrapRepository.getScrapPosts()
            Log.d(TAG, "ScrapViewModel - getPostList: ${_postResponse.value}");
        }
    }

    fun getMorePostList(nextCursor: Int) {
        viewModelScope.launch {
            _morePost.value = scrapRepository.getScrapPosts(nextCursor)
            Log.d(TAG, "ScrapViewModel - getPostList: ${_postResponse.value}");
        }
    }

    fun editClickedPostIndex(postIndex: Int) {
        _startIndex.value = postIndex
    }

    fun changeLikesState(create: Boolean, postId: Int) {
        viewModelScope.launch {
            if (create) {
                _changeLikeResponse.value = communicateRepository.createLike(postId)
            } else {
                _changeLikeResponse.value = communicateRepository.deleteLike(postId)
            }
        }
    }

    fun changeScrapState(create: Boolean, postId: Int) {
        viewModelScope.launch {
            if (create) {
                _scrapResponse.value = communicateRepository.createScrap(postId)
            } else {
                _scrapResponse.value = communicateRepository.deleteScrap(postId)
            }
        }
    }
    suspend fun deletePost(postId : Int) : MsgResponse{
        val response =  myPageRepository.deletePost(postId)
        return if(response is Resource.Success) return response.value
        else MsgResponse(false,"Resource Fail")
    }
    suspend fun changePostStatus(postInt: Int,status : Boolean) :MsgResponse{
        val response = myPageRepository.updatePostStatus(postInt,!status)
        return if (response is Resource.Success) return response.value
        else MsgResponse(false, "Resource Fail")
    }
}