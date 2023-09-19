package com.lf.fashion.ui.search

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.chip.Chip
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import com.lf.fashion.R
import com.lf.fashion.TAG
import com.lf.fashion.data.common.SearchItemFilterDataStore
import com.lf.fashion.data.common.SearchLookFilterDataStore
import com.lf.fashion.data.common.UserDataStorePref
import com.lf.fashion.databinding.SearchFragmentBinding
import com.lf.fashion.ui.hideKeyboard
import com.lf.fashion.ui.search.adapter.SearchRankRowClickListener
import com.lf.fashion.ui.search.adapter.SearchResultViewPagerAdapter
import com.lf.fashion.ui.search.adapter.TermRankAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlin.properties.Delegates

@AndroidEntryPoint
class SearchFragment : Fragment(R.layout.search_fragment),
    AdapterView.OnItemSelectedListener,
    SearchRankRowClickListener {
    private lateinit var binding: SearchFragmentBinding
    private lateinit var userPreferences: UserDataStorePref
    private val viewModel: SearchViewModel by hiltNavGraphViewModels(R.id.navigation_search)
    private lateinit var lookFilterDataStore: SearchLookFilterDataStore
    private lateinit var itemFilterDataStore: SearchItemFilterDataStore
    private var lastRequestTimeMillis: Long = 0 // 마지막 요청 시간을 저장할 변수

    private val tabTitleArray = arrayOf("LOOK", "ITEM")
    private val historyList = MutableLiveData<MutableList<String>>()
    private val termRankAdapter = TermRankAdapter(this)
    private var orderByParamMap =
        mapOf("인기순" to "best", "최신순" to "recent", "고가순" to "high", "저가순" to "low")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = SearchFragmentBinding.bind(view)

        lookFilterDataStore = SearchLookFilterDataStore(requireContext().applicationContext)
        itemFilterDataStore = SearchItemFilterDataStore(requireContext().applicationContext)
        userPreferences = UserDataStorePref(requireContext().applicationContext)

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

        //검색어 et에 있을시 인기 검색어 gone
        if (viewModel.savedSearchTerm.isNotEmpty()) {
            searchTermRankUiVisible(default = false)
            // Log.e(TAG, "onViewCreated: ${binding.searchTerm.root.isVisible}")
        }
        //검색 동작
        searchEtSetActionListener() // 검색 동작시 ui visibility 로 결과 레이아웃 노출 조정
        keyBoardUIControl() // edittext 외부 클릭시 hide keyboard

        // 검색어 레이아웃 관련
        recentSearchTermChipSetting() // 최근 검색어 chip 생성, 개별 삭제
        recentSearchHistoryClear()  // 최근 검색어 모두 지우기
        searchTermRankingRvSetting() // 인기 검색어 recycler view 세팅

        searchResultSpanCountBtnOnClick() // 결과 레이아웃 사진 모아보기 갯수 버튼 클릭
        filterOnclickNavigate() // item / look 각 필터 이동
        searchResultViewPagerSetting()  //결과 레이아웃 내부 세팅 (tab,viewpager)

        // 탭이 바뀔 때마다 spinner array 바꿔주기
        searchViewPagerOnChangeCallback(binding.searchResult.tabViewpager)

        binding.searchIcon.setOnClickListener {
            val term = binding.searchEt.text.toString()
            if (term.isNotEmpty()) {
                searchResultVisible(term)
            }
        }

        //검색 결과 화면에서 grid 모드로 보다가 사진을 클릭해서 1개씩 보기 모드로 바뀔 경우
        viewModel.gridMode.observe(viewLifecycleOwner) {
            if (it == 1) {
                binding.searchResult.gridText.text = "1"
            }
        }

        // spinner 값 바뀔 때마다 request !
        viewModel.selectedOrderBy.observe(viewLifecycleOwner) {
            val currentTimeMillis = System.currentTimeMillis()
            if (currentTimeMillis - lastRequestTimeMillis >= 500) { // 최소 0.5초 이내에 중복 요청 방지
                lastRequestTimeMillis = currentTimeMillis // 현재 시간으로 갱신
                when (binding.searchResult.tabViewpager.currentItem) {
                    0 -> { //look
                        requestLookSearch(viewModel.savedSearchTerm)
                    }

                    1 -> { //item
                        requestItemSearch(viewModel.savedSearchTerm)
                    }
                }
            }
        }
    }


    private fun filterOnclickNavigate() {
        binding.searchResult.filter.setOnClickListener {
            when (binding.searchResult.tabViewpager.currentItem) {
                0 -> { //look
                    findNavController().navigate(
                        R.id.action_navigation_search_to_searchFilterFragment,
                        bundleOf("searchResult" to true)
                    )
                }

                1 -> { //item
                    findNavController().navigate(
                        R.id.action_navigation_search_to_itemFilterFragment,
                        bundleOf("searchResult" to true)
                    )
                }
            }
        }
    }

    private fun searchEtSetActionListener() {
        binding.searchEt.setOnEditorActionListener { textView, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val searchTerm = textView.text.toString()
                searchResultVisible(searchTerm)
                true
            } else {
                false
            }
        }
    }

    //searchResult 바인딩 visible 처리 및 최근 검색어 칩 추가
    private fun searchResultVisible(searchTerm: String) {
        runBlocking {
            launch {
                viewModel.savedSearchTerm = searchTerm
                //chip 에 추가
                var newHistory = historyList.value
                if (newHistory != null) {
                    newHistory.add(0, searchTerm)
                    newHistory = newHistory.distinct().toMutableList()
                    Log.e(TAG, "searchAction HISTORY: $newHistory")
                } else {
                    newHistory = mutableListOf(searchTerm)
                }
                historyList.value = newHistory!!  // liveData 객체 업데이트 , datastore 정보 업데이트
                userPreferences.storeSearchHistoryList(historyList.value!!)

                hideKeyboard()
                binding.searchEt.isCursorVisible = false // 검색 실행시 edittext 커서 focus 제거
                searchTermRankUiVisible(false)
            }
        }
    }

    private fun searchTermRankUiVisible(default: Boolean) {
        binding.searchTerm.root.isVisible = default
        binding.searchResult.root.isVisible = !default
    }

    private fun keyBoardUIControl() {
        //editText 활성화,키보드 올라오면 최신 검색어 노출 view visible 하게
        binding.searchEt.setOnClickListener {
            if (binding.searchEt.hasFocus()) {
                binding.searchEt.isCursorVisible = true // cursor focus true
                searchTermRankUiVisible(true)
            }
        }
        binding.root.setOnClickListener { hideKeyboard() }
        binding.searchTerm.nest.setOnClickListener { hideKeyboard() }
    }

    private fun searchResultViewPagerSetting() {
        val tabViewpager = binding.searchResult.tabViewpager
        val tabLayout = binding.searchResult.tab

        tabViewpager.adapter = SearchResultViewPagerAdapter(this)
        TabLayoutMediator(tabLayout, tabViewpager) { tab, position ->
            tab.text = tabTitleArray[position]
        }.attach()
    }

    private fun searchViewPagerOnChangeCallback(tabViewpager: ViewPager2) {
        tabViewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                setupOrderBySpinner(position)  //페이지 바뀔때 spinner 새로 생성
                super.onPageSelected(position)
            }
        })
    }

    private fun requestLookSearch(searchTerm: String) {
        CoroutineScope(Dispatchers.IO).launch {
            with(lookFilterDataStore) {
                val tpo = tpoId.first()?.split(",")?.mapNotNull { it.toIntOrNull() }
                val season = seasonId.first()?.split(",")?.mapNotNull { it.toIntOrNull() }
                val style = styleId.first()?.split(",")?.mapNotNull { it.toIntOrNull() }
                val gender = lookGender.first()
                val height = height.first()
                val weight = weight.first()
                val orderBy = orderByParamMap[viewModel.selectedOrderBy.value] ?: "best"

                withContext(Dispatchers.Main) {
                    viewModel.getSearchResult(
                        searchTerm,
                        gender,
                        height,
                        weight,
                        tpo,
                        season,
                        style,
                        orderBy
                    )
                }
            }
        }
    }

    private fun requestItemSearch(searchTerm: String) {
        CoroutineScope(Dispatchers.IO).launch {
            with(itemFilterDataStore) {
                val gender = itemGender.first()
                val minPrice = minPrice.first()
                val maxPrice = maxPrice.first()
                val color = color.first()?.split(",")
                val orderBy = orderByParamMap[viewModel.selectedOrderBy.value] ?: "best"

                withContext(Dispatchers.Main) {
                    viewModel.getItemSearchResult(
                        searchTerm,
                        gender,
                        minPrice,
                        maxPrice,
                        color,
                        orderBy
                    )
                }
            }
        }
    }

    private fun recentSearchTermChipSetting() {
        val chipGroup = binding.searchTerm.recentTermChipGroup

        historyList.observe(viewLifecycleOwner) { history ->
            //기존 chipChild 모두 지우고, 새롭게 덮어쓴 ChipContents 리스트를 역순으로(최신 검색어 상단) child 칩 생성
            binding.searchTerm.recentTermChipGroup.removeAllViews()
            //10개까지만 노출
            val chipRange = if (history.size > 10) 0..9 else history.indices
            for (j in chipRange) {
                val chip =
                    LayoutInflater.from(requireContext())
                        .inflate(R.layout.chip_grey_item, null) as Chip
                val content =
                    if (history[j].length > 10) history[j].substring(0, 11) + "..." else history[j]
                chip.text = content
                chip.setOnCloseIconClickListener {
                    runBlocking {
                        launch {
                            history.removeAt(history.indexOf(history[j]))
                            userPreferences.storeSearchHistoryList(history)
                            historyList.value = history // liveData 객체 업데이트 , datastore 정보 업데이트
                        }
                    }
                }
                chip.setOnClickListener {
                    val term = chip.text.toString()
                    binding.searchEt.setText(term)
                    searchResultVisible(term)
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

    private fun searchTermRankingRvSetting() {
        binding.searchTerm.searchRankRv.adapter = termRankAdapter
        viewModel.searchTermRank.observe(viewLifecycleOwner) { searTermList ->
            termRankAdapter.submitList(searTermList)
        }
    }


    private fun searchResultSpanCountBtnOnClick() {
        binding.searchResult.gridModeBtn.setOnClickListener {
            when (binding.searchResult.gridText.text) {
                "1" -> {
                    binding.searchResult.gridText.text = "3"
                    viewModel.setGridMode(3)
                }

                "2" -> {
                    binding.searchResult.gridText.text = "1"
                    viewModel.setGridMode(1)
                }

                "3" -> {
                    binding.searchResult.gridText.text = "2"
                    viewModel.setGridMode(2)
                }
            }
        }
    }

    private fun setupOrderBySpinner(tabPosition: Int) {
        val orderBySpinner = binding.searchResult.orderBySpinner

        val arrayResId = when (tabPosition) {
            0 -> R.array.spinner_look_order
            1 -> R.array.spinner_item_order
            else -> R.array.spinner_look_order // 기본
        }
        orderBySpinner.onItemSelectedListener = this
        ArrayAdapter.createFromResource(
            requireContext(),
            arrayResId,
            R.layout.spinner_text_view
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            orderBySpinner.adapter = adapter
        }
    }


    override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
        viewModel.selectedOrderBy.value = parent.getItemAtPosition(position).toString()
        //Log.e(TAG, "onItemSelected: spinner ${viewModel.selectedOrderBy.value}")
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

    override fun searchRankOnclick(term: String) {
        binding.searchEt.setText(term)
        searchResultVisible(term)
    }

}