package com.lf.fashion.ui.home.frag

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lf.fashion.data.repository.FilterRepository
import com.lf.fashion.data.model.ChipInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FilterViewModel @Inject constructor(private val repository: FilterRepository) : ViewModel() {
    private val _tpoChipList = MutableLiveData<List<ChipInfo>>()
    var tpoChipList: LiveData<List<ChipInfo>> = _tpoChipList

    private val _seasonChipList = MutableLiveData<List<ChipInfo>>()
    var seasonChipList: LiveData<List<ChipInfo>> = _seasonChipList

    private val _styleChipList = MutableLiveData<List<ChipInfo>>()
    var styleChipList: LiveData<List<ChipInfo>> = _styleChipList

    var selectedGender :String?=null
    var savedHeight : Int?=null
    var savedWeight : Int?=null
    var selectedTposId: MutableList<Int> = mutableListOf()
    var selectedSeasonsId : MutableList<Int> = mutableListOf()
    var selectedStylesId : MutableList<Int> = mutableListOf()
    var tposTexts: MutableList<String> = mutableListOf()
    var seasonsTexts : MutableList<String> = mutableListOf()
    var stylesTexts : MutableList<String> = mutableListOf()

    //search itemFilter 에서 사용하는 변수
    var minPrice : Int? = null
    var maxPrice : Int? = null
    var selectedColor :MutableList<String> = mutableListOf()

    init {
        getTPOChipsInfo()
        getSeasonChipsInfo()
        getStyleChipsInfo()
    }
    private fun getTPOChipsInfo(){
        viewModelScope.launch {
            _tpoChipList.value = repository.getTPOChips()
        }
    }
    private fun getSeasonChipsInfo(){
        viewModelScope.launch {
            _seasonChipList.value = repository.getSeasonChips()
        }
    }
    private fun getStyleChipsInfo(){
        viewModelScope.launch {
            _styleChipList.value = repository.getStyleChips()
        }
    }
    fun clearAll(){
        selectedTposId.clear()
        selectedSeasonsId.clear()
        selectedStylesId.clear()
        tposTexts.clear()
        seasonsTexts.clear()
        stylesTexts.clear()
        selectedGender = null
        savedHeight = null
        savedWeight = null
        maxPrice =null
        minPrice =null
        selectedColor.clear()
    }
}