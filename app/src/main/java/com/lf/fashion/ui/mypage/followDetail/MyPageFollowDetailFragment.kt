package com.lf.fashion.ui.mypage.followDetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.lf.fashion.R
import com.lf.fashion.databinding.MypageFollowDetailBinding

class MyPageFollowDetailFragment : Fragment(R.layout.mypage_follow_detail) {
    private lateinit var binding : MypageFollowDetailBinding
    private val tabTitleArray = arrayOf("팔로워", "팔로잉","차단")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = MypageFollowDetailBinding.bind(view)

        val tabViewPager = binding.tabViewpager
        tabViewPager.adapter = FollowDetailAdapter(this)
        TabLayoutMediator(binding.tab,tabViewPager){tab,position->
            tab.text = tabTitleArray[position]
        }.attach()


    }
}