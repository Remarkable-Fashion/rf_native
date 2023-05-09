package com.lf.fashion.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.lf.fashion.TAG
import com.lf.fashion.databinding.HomeFragmentBinding
import com.lf.fashion.ui.home.adapter.DefaultPostAdapter
import com.lf.fashion.ui.home.adapter.GridPostAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment(), PhotoClickListener {
    private lateinit var binding: HomeFragmentBinding
    private var defaultRandomPhoto = true
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = HomeFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    //TODO: 보고싶은 성별을 선택하는 다이얼로그 만들어야함
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 상단 메뉴 - 랜덤/팔로잉 텍스트 선택 시 text color 변경 로직
        topMenuUiSetting()
        //기본 레이아웃 ui adapter 연결
        setMainViewPagerUI()
        //grid 모드 변경 adapter 연결 (기본 레이아웃 visibility gone)
        onClickGridLayoutUI()

    }

    private fun topMenuUiSetting(){
        //default 값으로 "랜덤" 메뉴 선택
        binding.appBarRandom.isSelected = defaultRandomPhoto //true

        binding.appBarFollowing.setOnClickListener {
            defaultRandomPhoto = false
            topMenuTextUiUpdate(defaultRandomPhoto)
        }
        binding.topRandomMenuLayer.setOnClickListener {
            defaultRandomPhoto = true
            topMenuTextUiUpdate(defaultRandomPhoto)
        }
    }
    private fun topMenuTextUiUpdate(boolean: Boolean) {
        binding.appBarRandom.isSelected = boolean
        binding.appBarFollowing.isSelected = !boolean
    }

    private fun setMainViewPagerUI() {
    /*response 로 post 를 받아서 중첩 viewPager 와 recyclerView 모두에게 adapter 연결/submitList 후 visibility 로 노출을 관리한다*/
        photoLayoutVisibilityMode(true) // default ui visibility
        viewModel.postList.observe(viewLifecycleOwner) { response ->
            with(binding.homeMainViewpager) {
                adapter = DefaultPostAdapter(this@HomeFragment).apply {
                    submitList(response)
                }
            }

            with(binding.gridRecyclerView) {
                adapter = GridPostAdapter().apply {
                    submitList(response)
                }
            }
        }
    }

    private fun onClickGridLayoutUI() {
        binding.photoGridModeBtn.setOnClickListener {
            when (binding.photoGridModeBtn.text) {
                "1" -> {
                    binding.photoGridModeBtn.text = "3"
                    photoLayoutVisibilityMode(false) // grid visibility
                }
                "3" -> {
                    binding.photoGridModeBtn.text = "1"
                    photoLayoutVisibilityMode(true) // default visibility
                }
            }
        }
    }

    private fun photoLayoutVisibilityMode(default: Boolean) {
        binding.homeMainViewpager.isVisible = default
        binding.gridRecyclerView.isVisible = !default
    }

    //default layout 모드에서 photo 클릭시 클릭한 이미지 url 만 safeargs 에 담아 fragment 로 전송
    override fun photoClicked(bool: Boolean, imageUrl: String) {
        if (bool) {
            val action =
                HomeFragmentDirections.actionNavigationHomeToPhotoDetailFragment(imageUrl)
            findNavController().navigate(action)
        }
    }
}