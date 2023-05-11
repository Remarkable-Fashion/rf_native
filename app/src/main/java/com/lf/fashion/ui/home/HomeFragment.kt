package com.lf.fashion.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.lf.fashion.R
import com.lf.fashion.data.response.Photo
import com.lf.fashion.databinding.HomeFragmentBinding
import com.lf.fashion.ui.home.adapter.DefaultPostAdapter
import com.lf.fashion.ui.home.adapter.GridPostAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment(), View.OnClickListener, PhotoClickListener, ShareBtnClickListener {
    private lateinit var binding: HomeFragmentBinding
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

        // 상단 메뉴 - 랜덤 모드 선택이 디폴트
        binding.appBarRandom.isSelected = true

        //기본 레이아웃 ui adapter 연결
        setMainViewPagerUI()

        //onclick listener 계층별로 묶어서 한번에 달기
        binding.topMenu.children.forEach { it.setOnClickListener(this) }
       /* binding.bottomSheet.children.forEach { it.setOnClickListener(this) }
        binding.bottomLinear.children.forEach { it.setOnClickListener(this) }*/

    }
    private fun setMainViewPagerUI() {
        /*response 로 post 를 받아서 중첩 viewPager 와 recyclerView 모두에게 adapter 연결/submitList 후 visibility 로 노출을 관리한다*/
        photoLayoutVisibilityMode(true) // default ui visibility
        viewModel.postList.observe(viewLifecycleOwner) { response ->
            with(binding.homeMainViewpager) {
                adapter = DefaultPostAdapter(this@HomeFragment, this@HomeFragment).apply {
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

    //TODO : bottom sheet (레이아웃만 추가?) , 유저 프로필 페이지로 연결
    //default layout 모드에서 photo 클릭시 클릭한 이미지 url 만 safeargs 에 담아 fragment 로 전송
    override fun photoClicked(bool: Boolean, photos: List<Photo>) {
        if (bool) {
            val action =
                HomeFragmentDirections.actionNavigationHomeToPhotoDetailFragment(photos.toTypedArray())
            findNavController().navigate(action)
        }
    }

    override fun shareBtnClicked(bool: Boolean) {
        if (bool) {
            val dialog = HomeBottomSheetFragment()
            dialog.show(parentFragmentManager,"bottom_sheet")
        }
    }

    override fun onClick(view: View?) {
        when (view) {
            //상단 바의 랜덤,팔로잉 버튼 각각 클릭 (임시로 select 여부만 변경 처리)
            binding.appBarFollowing -> { //팔로잉 버튼 클릭
                binding.appBarFollowing.isSelected  = true
                binding.appBarRandom.isSelected = false
            }
            binding.topRandomMenuLayer -> { //랜덤 버튼 클릭
                binding.appBarFollowing.isSelected  = false
                binding.appBarRandom.isSelected = true
            }

            //상단 바의 3장씩 보기 버튼 클릭
            binding.appBarPhotoGridModeBtn -> {
                when (binding.appBarPhotoGridModeBtn.text) {
                    "1" -> {
                        binding.appBarPhotoGridModeBtn.text = "3"
                        photoLayoutVisibilityMode(false) // grid visibility
                    }
                    "3" -> {
                        binding.appBarPhotoGridModeBtn.text = "1"
                        photoLayoutVisibilityMode(true) // default visibility
                    }
                }
            }

            //상단 바의 필터 버튼 클릭
            binding.appBarPhotoFilterBtn -> {
                findNavController().navigate(R.id.action_navigation_home_to_filterFragment)
            }

            // vertical 뷰의 share 버튼 클릭시 오픈되는 bottom sheet 내부 버튼 onclick
          /*  binding.bottomSheetLinkCopyBtn -> {

            }
            binding.bottomSheetShareBtn -> {

            }
            binding.bottomSheetScrapBtn -> {

            }
            binding.noInterestBtn -> {

            }
            binding.cancelFollowBtn -> {

            }
            binding.blockBtn -> {

            }
            binding.declareBtn -> {

            }*/
        }
    }


    private fun photoLayoutVisibilityMode(default: Boolean) {
        binding.homeMainViewpager.isVisible = default
        binding.gridRecyclerView.isVisible = !default
    }


}