package com.lf.fashion.ui.mypage.followDetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.lf.fashion.databinding.MypageFollowDetailBinding

class MyPageFollowDetailFragment : Fragment() {
    private lateinit var binding : MypageFollowDetailBinding
    private val tabTitleArray = arrayOf("팔로워", "팔로잉","차단")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MypageFollowDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val tabViewPager = binding.tabViewpager
        tabViewPager.adapter = FollowDetailAdapter(this)
        TabLayoutMediator(binding.tab,tabViewPager){tab,position->
            tab.text = tabTitleArray[position]
        }.attach()


    }
}