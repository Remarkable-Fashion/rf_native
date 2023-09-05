package com.lf.fashion.ui.search.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.lf.fashion.ui.search.SearchResultFragment


private const val NUM_TABS = 2
class SearchResultViewPagerAdapter(fragment: Fragment) :
    FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return NUM_TABS
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                SearchResultFragment("look")
            }
            1 -> {
                SearchResultFragment("item")
            }
            else -> {
                SearchResultFragment("look")
            }
        }
    }


}
