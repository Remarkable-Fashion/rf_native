package com.lf.fashion.ui.search

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
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
class SearchFragment : Fragment() {
    private lateinit var binding: SearchFragmentBinding
    private lateinit var userPreferences: PreferenceManager
    private val searchViewModel: SearchViewModel by viewModels()
    private val keywordTest = listOf(
        "어그", "반팔", "t셔츠", "슬랙스", "셔츠", "니트반팔", "린넨반지", "원피스", "셔츠 원피스"
    )
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


        //editText 활성화,키보드 올라오면 최신 검색어 노출 view visible 하게
        binding.searchEt.setOnClickListener {
            if (binding.searchEt.hasFocus()) {
                binding.searchEt.isCursorVisible = true // cursor focus true
                binding.searchTerm.root.visibility = View.VISIBLE
                binding.searchResult.root.visibility = View.GONE
            }
        }


        searchAction()

        recentSearchTermChipSetting()

        recentSearchHistoryClear()

        popularSearchTermRvSetting()

        searchResultViewSetting()

        searchResultSpanCountBtnOnClick()
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

    private val tabTitleArray = arrayOf("LOOK", "ITEM")

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
            Log.d(TAG, "SearchFragment - recentSearchTermChipSetting: $history");
            val orderByRecent = history.reversed()
            for (j in orderByRecent.indices) {
                val chip =
                    LayoutInflater.from(requireContext())
                        .inflate(R.layout.chip_grey_item, null) as Chip
                chip.text = orderByRecent[j]
                chip.setOnCloseIconClickListener {
                    Log.d(
                        TAG,
                        "chlicked chip text: ${chip.text}"
                    );
                    runBlocking {
                        launch {
                            history.removeAt(history.indexOf(chip.text))
                            Log.d(
                                TAG,
                                "is it removed? : $history"
                            );
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

