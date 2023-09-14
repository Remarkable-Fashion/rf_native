package com.lf.fashion.ui.home.frag

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lf.fashion.TAG
import com.lf.fashion.data.model.DeclareInfo
import com.lf.fashion.data.network.Resource
import com.lf.fashion.data.repository.CommunicateRepository
import com.lf.fashion.data.repository.HomeRepository
import com.lf.fashion.data.model.MsgResponse
import com.lf.fashion.data.model.PostInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostBottomViewModel @Inject constructor(
    private val homeRepository: HomeRepository,
    private val communicateRepository: CommunicateRepository
) : ViewModel() {
    private val _postInfo = MutableLiveData<Resource<PostInfo>>()
    var postInfo: LiveData<Resource<PostInfo>> = _postInfo

    private val _blockResponse = MutableLiveData<Resource<MsgResponse>>()
    val blockResponse = _blockResponse

    private val _scrapResponse = MutableLiveData<Resource<MsgResponse>>()
    val scrapResponse = _scrapResponse

    private val _followResponse = MutableLiveData<Resource<MsgResponse>>()
    val followResponse = _followResponse

    fun getPostByPostId(postId : Int){
        viewModelScope.launch {
            _postInfo.value = homeRepository.getPostInfoByPostId(postId)
        }
    }
    fun changeBlockUserState(create: Boolean, userId: Int) {
        viewModelScope.launch {
            if(create){
                _blockResponse.value = communicateRepository.blockUser(userId)
            }else{
                _blockResponse.value = communicateRepository.deleteBlock(userId)
            }
        }
    }

    fun changeScrapState(create : Boolean, postId : Int){
        viewModelScope.launch {
            Log.d(TAG, "PostBottomViewModel - changeScrapState: CREATE $create");
            if(create){
                _scrapResponse.value =  communicateRepository.createScrap(postId)
            }else{
                _scrapResponse.value = communicateRepository.deleteScrap(postId)
            }
        }
    }

    fun changeFollowingState(create : Boolean, userId : Int){
        viewModelScope.launch {
            Log.d(TAG, "PostBottomViewModel - changeFollowingState: CREATE $create");
            if(create){
                followResponse.value = communicateRepository.createFollowing(userId)
            }else{
                followResponse.value = communicateRepository.deleteFollowing(userId)
            }
        }
    }

    suspend fun declarePost(declareInfo: DeclareInfo):MsgResponse{
        val response =  communicateRepository.declarePost(declareInfo)
        return if(response is Resource.Success) return response.value
        else MsgResponse(false,"Resource Fail")
    }
}