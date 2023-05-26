package com.lf.fashion.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.lf.fashion.data.response.ChipContents
import com.lf.fashion.databinding.SearchTermFragmentBinding
import com.lf.fashion.ui.childChip

class SearchTermFragment : Fragment() {
    lateinit var binding : SearchTermFragmentBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SearchTermFragmentBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val chipGroup = binding.recentTermChipGroup
        val testList = mutableListOf<ChipContents>()
        val keywordTest = listOf(
            "어그","반팔","t셔츠","슬랙스","셔츠","니트반팔","린넨반지","원피스","셔츠 원피스")
        for(i in 0..8){
            testList.add(ChipContents(keywordTest[i],null))
        }

        childChip(testList.toList(),chipGroup,"grey")

        binding.searchRankRv.apply {
            adapter = TermRankAdapter().apply {
                submitList(keywordTest)
            }
        }
    }
}