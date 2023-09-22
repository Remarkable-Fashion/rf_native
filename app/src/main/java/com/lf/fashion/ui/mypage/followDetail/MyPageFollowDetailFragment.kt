package com.lf.fashion.ui.mypage.followDetail

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayoutMediator
import com.lf.fashion.MainActivity
import com.lf.fashion.R
import com.lf.fashion.databinding.MypageFollowDetailBinding
import com.lf.fashion.ui.common.cancelBtnBackStack
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyPageFollowDetailFragment : Fragment(R.layout.mypage_follow_detail) {
    private lateinit var binding : MypageFollowDetailBinding
    private val tabTitleArray = arrayOf("팔로워", "팔로잉","차단")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainActivity.hideNavi(true)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = MypageFollowDetailBinding.bind(view)

        val tabViewPager = binding.tabViewpager
        tabViewPager.adapter = FollowDetailAdapter(this)
        tabViewPager.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER // 양옆 오버 스크롤 이벤트 shadow 제거
        TabLayoutMediator(binding.tab,tabViewPager){tab,position->
            tab.text = tabTitleArray[position]
        }.attach()

        cancelBtnBackStack(binding.cancelBtn)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        MainActivity.hideNavi(false)
    }
}