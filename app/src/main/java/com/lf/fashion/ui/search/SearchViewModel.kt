package com.lf.fashion.ui.search

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lf.fashion.TAG
import com.lf.fashion.data.common.Event
import com.lf.fashion.data.network.Resource
import com.lf.fashion.data.repository.SearchRepository
import com.lf.fashion.data.model.RandomPostResponse
import com.lf.fashion.data.model.SearchItemResult
import com.lf.fashion.data.model.SearchLookResult
import com.lf.fashion.data.model.SearchTerm
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(private val searchRepository: SearchRepository) :
    ViewModel() {
    private val _gridMode = MutableLiveData<Int>()
    val gridMode: LiveData<Int> = _gridMode

    //지금은 POST LIST 하나로 LOOK ITEM 모두 테스트 돌리는 중 ~ -> dev 연결하면서 분리해둠 2023.7.7
    private val _lookList = MutableLiveData<Resource<SearchLookResult>>()
    var lookList: LiveData<Resource<SearchLookResult>> = _lookList

    private val _itemList = MutableLiveData<Resource<SearchItemResult>>()
    var itemList: LiveData<Resource<SearchItemResult>> = _itemList

    private val _loadMoreLook = MutableLiveData<Resource<SearchLookResult>>()
    var loadMoreLook: LiveData<Resource<SearchLookResult>> = _loadMoreLook

    private val _loadMoreItem = MutableLiveData<Resource<SearchItemResult>>()
    var loadMoreItem: LiveData<Resource<SearchItemResult>> = _loadMoreItem

    private val _searchTermRank = MutableLiveData<List<SearchTerm>>()
    var searchTermRank: LiveData<List<SearchTerm>> = _searchTermRank

    var savedSearchTerm = ""

    var selectedOrderBy = MutableLiveData<String>()

    init {
        getSearchTermRank()
    }

    private fun getSearchTermRank() {
        viewModelScope.launch {
            val response = searchRepository.getSearchTermRank()
            if (response is Resource.Success) {
                response.value?.let {
                    _searchTermRank.value = it
                }
            }
        }
    }

    fun getSearchResult(
        loadMore : Boolean?=null,
        term: String,
        sex: String? = null,
        height: Int? = null,
        weight: Int? = null,
        tpo: List<Int>? = null,
        season: List<Int>? = null,
        style: List<Int>? = null,
        order: String,
        cursor : List<Long>?=null
    ) {
        viewModelScope.launch {

           val responseData =
                searchRepository.getSearchResult(term, sex, height, weight, tpo, season, style,order,cursor)
            if(loadMore == true){
                _loadMoreLook.value =responseData
            }else{
                _lookList.value = responseData
            }
        }
    }

    fun getItemSearchResult(
        loadMore : Boolean?=null,
        term: String,
        sex: String? = null,
        minPrice: Int? = null,
        maxPrice: Int? = null,
        color: List<String>? = null,
        order: String,
        cursor : List<Long>?=null
    ) {
        viewModelScope.launch {
            val responseData =
                searchRepository.getItemSearchResult(term, sex, minPrice, maxPrice, color,order,cursor)
            if(loadMore == true){
                _loadMoreItem.value = responseData
            }else{
                _itemList.value = responseData
            }
        }
    }


    fun setGridMode(mode: Int) {
        _gridMode.value = mode
    }
}