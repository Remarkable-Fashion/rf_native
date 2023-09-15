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


    private val _searchTermRank = MutableLiveData<List<SearchTerm>>()
    var searchTermRank: LiveData<List<SearchTerm>> = _searchTermRank

    var savedSearchTerm = ""

    var selectedOrderBy = ""

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
        term: String,
        sex: String? = null,
        height: Int? = null,
        weight: Int? = null,
        tpo: List<Int>? = null,
        season: List<Int>? = null,
        style: List<Int>? = null,
        order: String
    ) {
        viewModelScope.launch {
            _lookList.value =
                searchRepository.getSearchResult(term, sex, height, weight, tpo, season, style,order)
            Log.e(TAG, "getSearchResult 위치 : ViewModel - request")
        }
    }

    fun getItemSearchResult(
        term: String,
        sex: String? = null,
        minPrice: Int? = null,
        maxPrice: Int? = null,
        color: List<String>? = null,
        order: String
    ) {
        viewModelScope.launch {
            _itemList.value =
                searchRepository.getItemSearchResult(term, sex, minPrice, maxPrice, color,order)

        }
    }

    fun setGridMode(mode: Int) {
        _gridMode.value = mode
        Log.d(TAG, "SearchViewModel - setGridMode: ${gridMode.value}");
    }
}