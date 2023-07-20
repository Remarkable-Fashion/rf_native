package com.lf.fashion.ui.home

import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import com.lf.fashion.TAG
import com.lf.fashion.data.common.PreferenceManager
import com.lf.fashion.data.network.Resource
import com.lf.fashion.data.repository.HomeRepository
import com.lf.fashion.data.response.MsgResponse
import com.lf.fashion.data.response.RandomPostResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeRepository: HomeRepository,
    @ApplicationContext context: Context
) : ViewModel() {

    private val _response = MutableLiveData<Resource<RandomPostResponse>>()
    var response: LiveData<Resource<RandomPostResponse>> = _response

    private val _changeLikeResponse = MutableLiveData<Resource<MsgResponse>>()
    var changeLikeResponse = _changeLikeResponse

    private val userPreferences = PreferenceManager(context)

      init {
          viewModelScope.launch {
          getPostList("Male",21)
          }
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

    fun changeLikesState(create : Boolean, postId : Int){
        viewModelScope.launch {
            if(create){
                _changeLikeResponse.value =  homeRepository.createLike(postId)
            }else{
                _changeLikeResponse.value = homeRepository.deleteLike(postId)
            }
        }
    }
}