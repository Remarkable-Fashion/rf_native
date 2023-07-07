package com.lf.fashion.ui.search

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lf.fashion.TAG
import com.lf.fashion.data.repository.SearchRepository
import com.lf.fashion.data.response.RandomPostResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(private val searchRepository: SearchRepository) :
    ViewModel() {
    private val _gridMode = MutableLiveData<Int>()
    val gridMode: LiveData<Int> = _gridMode

    //지금은 POST LIST 하나로 LOOK ITEM 모두 테스트 돌리는 중 ~ -> dev 연결하면서 분리해둠 2023.7.7
    private val _postList = MutableLiveData<List<RandomPostResponse>>()
    var postList: LiveData<List<RandomPostResponse>> = _postList


    private val _itemList = MutableLiveData<List<RandomPostResponse>>()
    var itemList: LiveData<List<RandomPostResponse>> = _itemList

    /*  init {
          getPostList()
      }*/

     fun getSearchResult(term : String) {
        viewModelScope.launch {
            _postList.value = searchRepository.getSearchResult(term)
        }
    }

     fun getItemSearchResult(term : String){
        viewModelScope.launch {
            _itemList.value = searchRepository.getItemSearchResult(term)
        }
    }

    fun setGridMode(mode: Int) {
        _gridMode.value = mode
        Log.d(TAG, "SearchViewModel - setGridMode: ${gridMode.value}");
    }
}