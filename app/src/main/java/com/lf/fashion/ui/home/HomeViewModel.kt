package com.lf.fashion.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lf.fashion.data.repository.HomeRepository
import com.lf.fashion.data.response.Post
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val homeRepository: HomeRepository) : ViewModel() {

    private val _postList = MutableLiveData<List<Post>>()
    var postList: LiveData<List<Post>> = _postList

    init {

        getPostList()

    }

    private fun getPostList() {
        _postList.value = homeRepository.getTestPostList()
    }
}