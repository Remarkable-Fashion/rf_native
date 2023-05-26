package com.lf.fashion.ui.search

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.lf.fashion.TAG
import com.lf.fashion.data.common.PreferenceManager
import com.lf.fashion.data.response.ChipContents
import com.lf.fashion.databinding.SearchTermFragmentBinding
import com.lf.fashion.ui.childChip
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class SearchTermFragment : Fragment() {
    lateinit var binding: SearchTermFragmentBinding
    private lateinit var userPreferences : PreferenceManager

    val keywordTest = listOf(
        "어그", "반팔", "t셔츠", "슬랙스", "셔츠", "니트반팔", "린넨반지", "원피스", "셔츠 원피스"
    )
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SearchTermFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userPreferences = PreferenceManager(requireContext().applicationContext)

        recentSearchTermChipSetting()

        popularSearchTermRvSetting()



    }

    private fun recentSearchTermChipSetting() {
        val chipGroup = binding.recentTermChipGroup
        val testList = mutableListOf<ChipContents>()

        runBlocking {
            launch {
                val historyStr = userPreferences.searchHistoryList.first()
                historyStr?.let {
                    val historyList =
                        Gson().fromJson(historyStr, Array<String>::class.java).toMutableList()
                    if (historyList.isNotEmpty()) {
                        Log.d(TAG, "SearchTermFragment - onViewCreated: $historyList ");
                        for (i in historyList.indices) {
                            testList.add(ChipContents(historyList[i], null))
                        }
                        childChip(testList.toList(), chipGroup, "grey")

                    }
                }
            }
        }

    }

    private fun popularSearchTermRvSetting(){
        binding.searchRankRv.apply {
            adapter = TermRankAdapter().apply {
                submitList(keywordTest)
            }
        }
    }
}