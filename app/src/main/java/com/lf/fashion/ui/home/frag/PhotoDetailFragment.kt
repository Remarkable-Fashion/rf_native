package com.lf.fashion.ui.home.frag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.lf.fashion.data.response.Photo
import com.lf.fashion.databinding.HomeBPhotoDetailFragmentBinding
import com.lf.fashion.ui.cancelBtnBackStack
import com.lf.fashion.ui.home.adapter.PhotoHorizontalAdapter

/**
 * 포스팅 사진 클릭시 원본을 보여주는 프래그먼트입니다.
 */
class PhotoDetailFragment : Fragment() {
    private lateinit var binding : HomeBPhotoDetailFragmentBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = HomeBPhotoDetailFragmentBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val photoUrl =arguments?.get("photos") as Array<Photo>

        //viewpager adapter 설정
        with(binding.photoDetailViewPager){
            adapter = PhotoHorizontalAdapter(null).apply {
                submitList(photoUrl.asList())
            }
            TabLayoutMediator(
                binding.viewpagerIndicator,
                this
            ){_,_ ->}.attach()
        }

        cancelBtnBackStack(binding.cancelBtn)
    }
}