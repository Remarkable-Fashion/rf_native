package com.lf.fashion.ui.search.adapter

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.lf.fashion.TAG
import com.lf.fashion.ui.search.ItemLinearFragment
import com.lf.fashion.ui.search.LookGridFragment


private const val NUM_TABS = 2
class SearchResultViewPagerAdapter(fragment: Fragment) :
    FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return NUM_TABS
    }

    override fun createFragment(position: Int): Fragment {
        Log.d(TAG, "SearchResultViewPagerAdapter - getItemId: ${getItemId(position)}");

        return when (position) {
            0 -> LookGridFragment()
            1 -> ItemLinearFragment()
            else -> {
                LookGridFragment()
            }
        }
    }


}
