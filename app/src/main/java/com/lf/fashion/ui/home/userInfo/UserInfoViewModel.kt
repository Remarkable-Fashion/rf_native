package com.lf.fashion.ui.home.userInfo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lf.fashion.data.common.Event
import com.lf.fashion.data.network.Resource
import com.lf.fashion.data.repository.CommunicateRepository
import com.lf.fashion.data.repository.HomeRepository
import com.lf.fashion.data.response.MsgResponse
import com.lf.fashion.data.response.PostInfo
import com.lf.fashion.data.response.RecommendCloth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserInfoViewModel @Inject constructor(
    private val repository: HomeRepository,
    private val communicateRepository: CommunicateRepository
) : ViewModel() {
    private val _userInfo = MutableLiveData<Event<Resource<PostInfo>>>()
    var userInfo: LiveData<Event<Resource<PostInfo>>> = _userInfo

    private val _lookBook = MutableLiveData<Resource<RecommendCloth>>()
    var lookBook: LiveData<Resource<RecommendCloth>> = _lookBook

    private val _topLook = MutableLiveData<Resource<RecommendCloth>>()
    var topLook: LiveData<Resource<RecommendCloth>> = _topLook

    private val _likeResponse = MutableLiveData<Resource<MsgResponse>>()
    val likeResponse = _likeResponse

    private val _followResponse = MutableLiveData<Resource<MsgResponse>>()
    val followResponse = _followResponse


    fun getUserInfoAndStyle(postId: Int) {
        viewModelScope.launch {
            _userInfo.value = Event(repository.getPostInfoByPostId(postId))

        }
    }

    fun getLookBook(postId: Int, category: String) {
        viewModelScope.launch {
            _lookBook.value = repository.getRecommendClothesInfo(postId, category)
        }
    }

    fun getTopLook(postId: Int, category: String) {
        viewModelScope.launch {
            _topLook.value = repository.getRecommendClothesTop3(postId, category)
        }
    }

    fun changeFollowingState(create: Boolean, userId: Int) {
        viewModelScope.launch {
            if (create) {
                followResponse.value = communicateRepository.createFollowing(userId)
            } else {
                followResponse.value = communicateRepository.deleteFollowing(userId)
            }
        }
    }

    suspend fun changeClotheLikeState(create: Boolean, clothesId: Int): Resource<MsgResponse> {
        return if (create) {
            communicateRepository.createClothesLike(clothesId)
        } else {
            communicateRepository.deleteClothesLike(clothesId)
        }
    }
}