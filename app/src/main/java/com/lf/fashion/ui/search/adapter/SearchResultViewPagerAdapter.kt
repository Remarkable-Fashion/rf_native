package com.lf.fashion.ui.search.adapter

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.lf.fashion.ui.search.SearchResultFragment


private const val NUM_TABS = 2
class SearchResultViewPagerAdapter(fragment: Fragment,private val searchTerm :String) :
    FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return NUM_TABS
    }

    override fun createFragment(position: Int): Fragment {
        val bundle = bundleOf("searchTerm" to searchTerm)
        return when (position) {
            0 -> {
                val lookFrag = SearchResultFragment("look")
                lookFrag.arguments = bundle
                lookFrag
            }
            1 -> {
                val itemFrag = SearchResultFragment("item")
                itemFrag.arguments = bundle
                itemFrag
            }
            else -> {
                val lookFrag = SearchResultFragment("look")
                lookFrag.arguments = bundle
                lookFrag
            }
        }
    }


}
