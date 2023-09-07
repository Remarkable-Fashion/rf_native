package com.lf.fashion.ui.home.frag

import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import com.lf.fashion.TAG
import com.lf.fashion.data.common.UserDataStorePref
import com.lf.fashion.data.network.Resource
import com.lf.fashion.data.repository.CommunicateRepository
import com.lf.fashion.data.repository.HomeRepository
import com.lf.fashion.data.model.MsgResponse
import com.lf.fashion.data.model.RandomPostResponse
import com.lf.fashion.data.repository.MyPageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeRepository: HomeRepository,
    private val communicateRepository: CommunicateRepository,
    private val myPageRepository: MyPageRepository,
    @ApplicationContext context: Context
) : ViewModel() {
    var postMode = MutableLiveData<String>()

    private val _response = MutableLiveData<Resource<RandomPostResponse>>()
    var response: LiveData<Resource<RandomPostResponse>> = _response

    private val _likeResponse = MutableLiveData<Resource<MsgResponse>>()
    val likeResponse = _likeResponse

    private val _scrapResponse = MutableLiveData<Resource<MsgResponse>>()
    val scrapResponse = _scrapResponse


    private val userPreferences = UserDataStorePref(context)

    init {
        postMode.value = "random"
        getPostList("Male", 21)

    }

    fun getPostList(sex: String, take: Int) {
        Log.d(TAG, "suspend getPostList 호출 ")
        viewModelScope.launch {
            val savedToken = userPreferences.accessToken.first()
            Log.d(TAG, "HomeViewModel - getPostList: $savedToken");
            if (savedToken.isNullOrEmpty()) {
                _response.value = homeRepository.getRandomPostPublic(sex, take)
                Log.d(TAG, "HomeViewModel - getPostList: public ! ${_response.value}")
            } else {
                _response.value = homeRepository.getRandomPost(sex, take)
                Log.d(TAG, "HomeViewModel - getPostList: private ! ${_response.value}")
            }
        }
    }

    fun changeLikesState(create: Boolean, postId: Int) {
        viewModelScope.launch {
            if (create) {
                _likeResponse.value = communicateRepository.createLike(postId)
            } else {
                _likeResponse.value = communicateRepository.deleteLike(postId)
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
}