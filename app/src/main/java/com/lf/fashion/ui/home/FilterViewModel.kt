package com.lf.fashion.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lf.fashion.data.repository.HomeRepository
import com.lf.fashion.data.response.ChipInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FilterViewModel @Inject constructor(private val repository: HomeRepository) : ViewModel() {
    private val _chipList = MutableLiveData<List<ChipInfo>>()
    var chipList: LiveData<List<ChipInfo>> = _chipList

    init {
        getChipsInfo()
    }
    private fun getChipsInfo(){
        viewModelScope.launch {
            _chipList.value = repository.getChipInfo()
        }
    }
}