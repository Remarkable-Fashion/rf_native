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


    private val _loadMore = MutableLiveData<Resource<RandomPostResponse>>()
    var loadMore: LiveData<Resource<RandomPostResponse>> = _loadMore

    private val _likeResponse = MutableLiveData<Resource<MsgResponse>>()
    val likeResponse = _likeResponse

    private val _scrapResponse = MutableLiveData<Resource<MsgResponse>>()
    val scrapResponse = _scrapResponse


    private val userPreferences = UserDataStorePref(context)
    private var recentFollowingPostCursor = ""

    init {
        postMode.value = "random"
        //   getPostList(21,"Male")
    }

    fun getPostList(
        loadMore: Boolean? = null,
        take: Int,
        sex: String,
        height: Int? = null,
        weight: Int? = null,
        tpo: List<Int>? = null,
        season: List<Int>? = null,
        style: List<Int>? = null
    ) {
        Log.d(TAG, "suspend getPostList 호출 ")
        viewModelScope.launch {

            val savedToken = userPreferences.accessToken.first()
            val postData = when (postMode.value) {
                "random" -> {
                    if (savedToken.isNullOrEmpty()) {
                        homeRepository.getRandomPostPublic(
                            take,
                            sex,
                            height,
                            weight,
                            tpo,
                            season,
                            style
                        )
                    } else {
                        homeRepository.getRandomPost(
                            take,
                            sex,
                            height,
                            weight,
                            tpo,
                            season,
                            style
                        )
                    }
                }

                "following" -> {
                    homeRepository.getFollowingPost(
                        if(loadMore==true) recentFollowingPostCursor else null,
                        take,
                        sex,
                        height,
                        weight,
                        tpo,
                        season,
                        style
                    )
                }

                else -> null
            }
            postData?.let { response ->
                if (loadMore == true) {
                    _loadMore.value = response
                } else {
                    _response.value = response
                }
            //todo paging test
                if (postMode.value == "following") {
                    if (response is Resource.Success) {
                        recentFollowingPostCursor = response.value.nextFollowingCursor!!
                    }
                }
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

    suspend fun deletePost(postId: Int): MsgResponse {
        val response = myPageRepository.deletePost(postId)
        return if (response is Resource.Success) return response.value
        else MsgResponse(false, "Resource Fail")
    }

    suspend fun changePostStatus(postInt: Int, status: Boolean): MsgResponse {
        val response = myPageRepository.updatePostStatus(postInt, !status)
        return if (response is Resource.Success) return response.value
        else MsgResponse(false, "Resource Fail")
    }
}