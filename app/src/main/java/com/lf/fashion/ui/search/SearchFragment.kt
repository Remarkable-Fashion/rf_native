package com.lf.fashion.ui.search

import android.content.Context.INPUT_METHOD_SERVICE
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import com.google.android.material.chip.Chip
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import com.lf.fashion.R
import com.lf.fashion.TAG
import com.lf.fashion.data.common.PreferenceManager
import com.lf.fashion.databinding.SearchFragmentBinding
import com.lf.fashion.ui.hideKeyboard
import com.lf.fashion.ui.search.adapter.SearchResultViewPagerAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


@AndroidEntryPoint
class SearchFragment : Fragment(){
    private lateinit var binding: SearchFragmentBinding
    private lateinit var userPreferences: PreferenceManager
    private val searchViewModel: SearchViewModel by viewModels()
    private val keywordTest = listOf(
        "어그", "반팔", "t셔츠", "슬랙스", "셔츠", "니트반팔", "린넨반지", "원피스", "셔츠 원피스"
    )
    private val tabTitleArray = arrayOf("LOOK", "ITEM")
    private val historyList = MutableLiveData<MutableList<String>>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SearchFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userPreferences = PreferenceManager(requireContext().applicationContext)


        // 바로 datastore 에서 history 꺼내와서 liveData 객체에 담아주기
        runBlocking {
            launch {
                val historyStr = userPreferences.searchHistoryList.first() ?: ""
                if (historyStr.isNotEmpty()) {
                    val history = Gson().fromJson(historyStr, Array<String>::class.java)
                        .toMutableList()
                    historyList.value = history
                }
            }
        }


        //검색 동작
        searchAction() // 검색 동작시 ui visibility 로 결과 레이아웃 노출 조정
        keyBoardUIControl() // edittext 외부 클릭시 hide keyboard

        // 검색어 레이아웃 관련
        recentSearchTermChipSetting() // 최근 검색어 chip 생성, 개별 삭제
        recentSearchHistoryClear()  // 최근 검색어 모두 지우기
        popularSearchTermRvSetting() // 인기 검색어 recycler view 세팅

        //검색 결과 레이아웃 관련
        searchResultViewSetting()  //결과 레이아웃 내부 세팅 (tab,viewpager)
        searchResultSpanCountBtnOnClick() // 결과 레이아웃 사진 모아보기 갯수 버튼 클릭
    }

    private fun searchAction() {
        binding.searchEt.setOnEditorActionListener { textView, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val searchTerm = textView.text.toString()
                runBlocking {
                    launch {
                        var history = historyList.value
                        if (history != null) {
                            history.add(searchTerm)
                        } else {
                            history = mutableListOf(searchTerm)
                        }
                        historyList.value = history!!  // liveData 객체 업데이트 , datastore 정보 업데이트
                        userPreferences.storeSearchHistoryList(historyList.value!!)
                        Log.d(TAG, "SearchFragment - onViewCreated: ${historyList.value}");
                    }
                }
                hideKeyboard()
                binding.searchEt.isCursorVisible = false // 검색 실행시 edittext 커서 focus 제거
                binding.searchTerm.root.visibility = View.GONE
                binding.searchResult.root.visibility = View.VISIBLE


                true
            } else {
                false
            }
        }

    }
    private fun keyBoardUIControl(){
        //editText 활성화,키보드 올라오면 최신 검색어 노출 view visible 하게
        binding.searchEt.setOnClickListener {
            if (binding.searchEt.hasFocus()) {
                binding.searchEt.isCursorVisible = true // cursor focus true
                binding.searchTerm.root.visibility = View.VISIBLE
                binding.searchResult.root.visibility = View.GONE
            }
        }
        binding.root.setOnClickListener{ hideKeyboard() }
        binding.searchTerm.nest.setOnClickListener{hideKeyboard()}
    }

    private fun searchResultViewSetting() {
        val tabViewpager = binding.searchResult.tabViewpager
        val tabLayout = binding.searchResult.tab

        tabViewpager.adapter = SearchResultViewPagerAdapter(this)
        TabLayoutMediator(tabLayout, tabViewpager) { tab, position ->
            tab.text = tabTitleArray[position]
        }.attach()
    }

    private fun recentSearchTermChipSetting() {
        val chipGroup = binding.searchTerm.recentTermChipGroup

        historyList.observe(viewLifecycleOwner) { history ->
            //기존 chipChild 모두 지우고, 새롭게 덮어쓴 ChipContents 리스트를 역순으로(최신 검색어 상단) child 칩 생성
            binding.searchTerm.recentTermChipGroup.removeAllViews()
            val orderByRecent = history.reversed()
            //10개까지만 노출
            val chipRange = if(orderByRecent.size > 10) 0..9 else orderByRecent.indices
            for (j in chipRange) {
                val chip =
                    LayoutInflater.from(requireContext())
                        .inflate(R.layout.chip_grey_item, null) as Chip
                val content = if(orderByRecent[j].length>10)orderByRecent[j].substring(0,11)+"..." else orderByRecent[j]
                chip.text = content
                chip.setOnCloseIconClickListener {
                    runBlocking {
                        launch {
                            history.removeAt(history.indexOf(orderByRecent[j]))
                            userPreferences.storeSearchHistoryList(history)
                            historyList.value = history // liveData 객체 업데이트 , datastore 정보 업데이트
                        }
                    }
                }
                chipGroup.addView(chip)
            }
        }
    }

    private fun recentSearchHistoryClear() {
        binding.searchTerm.historyDeleteBtn.setOnClickListener {
            runBlocking {
                launch {
                    historyList.value?.let {
                        val temp = it
                        temp.clear()
                        historyList.value = temp
                    }
                    userPreferences.clearSearchHistory()
                }
            }
        }
    }

    private fun popularSearchTermRvSetting() {
        binding.searchTerm.searchRankRv.apply {
            adapter = TermRankAdapter().apply {
                submitList(keywordTest)
            }
        }
    }

    private fun searchResultSpanCountBtnOnClick() {
        binding.searchResult.appBarPhotoGridModeBtn.setOnClickListener {
            when (binding.searchResult.appBarPhotoGridModeBtn.text) {
                "1" -> {
                    binding.searchResult.appBarPhotoGridModeBtn.text = "3"
                    searchViewModel.setGridMode(3)
                }
                "2" -> {
                    binding.searchResult.appBarPhotoGridModeBtn.text = "1"
                    searchViewModel.setGridMode(1)
                }
                "3" -> {
                    binding.searchResult.appBarPhotoGridModeBtn.text = "2"
                    searchViewModel.setGridMode(2)
                }
            }
        }
    }
}

