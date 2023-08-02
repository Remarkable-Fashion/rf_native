package com.lf.fashion.ui.mypage.followDetail

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

private const val NUM_TABS = 3
class FollowDetailAdapter(fragment : Fragment):FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return NUM_TABS
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0->{
                FollowDetailItemFragment("follower")
            }
            1->{
                FollowDetailItemFragment("following")
            }
            else->{
                FollowDetailItemFragment("block")
            }
        }
    }

}