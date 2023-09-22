package com.lf.fashion.ui.globalFrag.photoDetail

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayoutMediator
import com.lf.fashion.MainActivity
import com.lf.fashion.R
import com.lf.fashion.data.model.ImageUrl
import com.lf.fashion.databinding.HomeBPhotoDetailFragmentBinding
import com.lf.fashion.ui.common.cancelBtnBackStack
import com.lf.fashion.ui.home.adapter.PhotoHorizontalAdapter

/**
 * 포스팅 사진 클릭시 원본을 보여주는 프래그먼트입니다.
 */
class PhotoDetailFragment : Fragment(R.layout.home_b_photo_detail_fragment) {
    private lateinit var binding : HomeBPhotoDetailFragmentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainActivity.hideNavi(true)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = HomeBPhotoDetailFragmentBinding.bind(view)

        val photoUrl =arguments?.get("photos") as Array<ImageUrl>

        //viewpager adapter 설정
        with(binding.photoDetailViewPager){
            adapter = PhotoHorizontalAdapter(null).apply {
                submitList(photoUrl.asList())
            }
            getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER // 최상단,최하단 스크롤 이벤트 shadow 제거

            TabLayoutMediator(
                binding.viewpagerIndicator,
                this
            ){_,_ ->}.attach()
        }

        cancelBtnBackStack(binding.cancelBtn)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        MainActivity.hideNavi(false)
    }

}