package com.lf.fashion.ui.globalFrag

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lf.fashion.data.model.Count
import com.lf.fashion.data.model.DeepLinkPost
import com.lf.fashion.data.model.MsgResponse
import com.lf.fashion.data.model.Posts
import com.lf.fashion.data.model.Profile
import com.lf.fashion.data.model.UserInfo
import com.lf.fashion.data.model.UserInfo2
import com.lf.fashion.data.network.Resource
import com.lf.fashion.data.repository.CommunicateRepository
import com.lf.fashion.data.repository.DeepLinkPostRepository
import com.lf.fashion.data.repository.MyPageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.ResponseBody.Companion.toResponseBody
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class DeepLinkViewModel @Inject constructor(
    private val repository: DeepLinkPostRepository,
    private val communicateRepository: CommunicateRepository,
    private val myPageRepository: MyPageRepository
) : ViewModel() {


    private val _response = MutableLiveData<Resource<Posts>>()
    var response: LiveData<Resource<Posts>> = _response

    private val _likeResponse = MutableLiveData<Resource<MsgResponse>>()
    val likeResponse = _likeResponse

    private val _scrapResponse = MutableLiveData<Resource<MsgResponse>>()
    val scrapResponse = _scrapResponse


    /* fun getPost(
         postId: Int, userId: Int? = null
     ) {
         viewModelScope.launch {
             _response.value = repository.getDeepLinkPost(postId, userId)
         }
     }*/
    fun DeepLinkPost.toPosts(): Posts {
        return Posts(
            id = id,
            isFavorite = isFavorite,
            isFollow = isFollow,
            isScrap = isScrap,
            isPublic = isPublic,
            createdAt = createdAt,
            images = images,
            user = user?.toUserInfo(), // UserInfo2를 UserInfo로 변환
            count = Count(likeCount)
        )
    }

    fun UserInfo2.toUserInfo(): UserInfo {
        return UserInfo(
            id = id,
            name = name,
            profile = Profile(
                profile.avartar,
                null,
                null,
                null,
                null
            ), // DeepLinkProfile를 Profile로 변환
            followers = null // 여기에서 필요에 따라 변환을 수행할 수 있습니다.
        )
    }

    fun getPost(
        postId: Int, userId: Int? = null
    ) {
        viewModelScope.launch {
            val deepLinkPost = repository.getDeepLinkPost(postId, userId)
            if (deepLinkPost is Resource.Success) {
                try {
                    val posts = deepLinkPost.value.toPosts()
                    _response.value = Resource.Success(posts)
                }catch (e : Exception){
                    _response.value = Resource.Failure(false ,500,"fail to convert PostsType".toResponseBody())
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