package com.lf.fashion.ui.home.frag

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.lf.fashion.R
import com.lf.fashion.data.common.PreferenceManager
import com.lf.fashion.data.response.Photo
import com.lf.fashion.data.response.Post
import com.lf.fashion.databinding.HomeAFragmentBinding
import com.lf.fashion.ui.home.HomeViewModel
import com.lf.fashion.ui.home.PhotoClickListener
import com.lf.fashion.ui.home.VerticalViewPagerClickListener
import com.lf.fashion.ui.home.adapter.DefaultPostAdapter
import com.lf.fashion.ui.home.adapter.GridPostAdapter
import com.lf.fashion.ui.home.GridSpaceItemDecoration
import com.lf.fashion.ui.home.adapter.GridPhotoClickListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * 메인 포스트 스크롤 페이지 프래그먼트입니다.
 */
@AndroidEntryPoint
class HomeFragment : Fragment(), View.OnClickListener, PhotoClickListener,
    VerticalViewPagerClickListener,
    GridPhotoClickListener {
    private lateinit var binding: HomeAFragmentBinding
    private val viewModel: HomeViewModel by viewModels()
    private val postList = MutableLiveData<List<Post>>()
    private val gridAdapter = GridPostAdapter(null,this)
    private lateinit var userPref :PreferenceManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = HomeAFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //앱 최초 실행시 gender 선택 다이얼로그 띄우기
        userPref = PreferenceManager(requireContext().applicationContext)
        runBlocking {
            launch {
                if(userPref.firstActivate.first().isNullOrEmpty()){
                    val dialog = GenderSelectionDialog()
                    dialog.show(parentFragmentManager, "gender_selection_dialog")
                   // userPref.isNotFirstActivate()  //테스트를 위해 계속 띄우려고 지워둠 !
                }
            }
        }
        // 상단 메뉴 - 랜덤 모드 선택이 디폴트
        binding.appBarRandom.isSelected = true

        //기본 레이아웃 ui adapter 연결
        setMainViewPagerUI()

        //onclick listener 묶어서 한번에 달기
        binding.topMenu.children.forEach { it.setOnClickListener(this) }

    }

    private fun setMainViewPagerUI() {
        /*response 로 post 를 받아서 중첩 viewPager 와 recyclerView 모두에게 adapter 연결/submitList 후 visibility 로 노출을 관리한다
        (전환 속도 감소, 메모리에 무리가 가지않는다면 ok)*/
        photoLayoutVisibilityMode(true) // default ui visibility
        viewModel.postList.observe(viewLifecycleOwner) { response ->
            with(binding.homeMainViewpager) {
                adapter = DefaultPostAdapter(this@HomeFragment, this@HomeFragment).apply {
                    submitList(response)
                }
            }
            postList.value = response
            with(binding.gridRecyclerView) {

                //staggeredGrid layoutManager 연결
                layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
                adapter = gridAdapter.apply {
                    addItemDecoration(GridSpaceItemDecoration(2,6))
                    submitList(response)
                }
                visibility = View.INVISIBLE
            }
        }
    }

    private fun editGridSpanCount(spanCount: Int) {
        with(binding.gridRecyclerView){
            layoutManager = StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL)
            while (itemDecorationCount > 0) { // 기존 추가한 itemDecoration 을 모두 지워주지않으면 점점 쌓인다.
                removeItemDecorationAt(0)
            }
            addItemDecoration(GridSpaceItemDecoration(spanCount,6))
            gridAdapter.editSpanCountBtnClicked(spanCount)  // 이미지 높이 조정을 위한 리스너에 span 값 전송
        }
    }

    //TODO :  유저 프로필 페이지로 연결
    //default layout 모드에서 photo 클릭시 클릭한 이미지 url 만 safe args 에 담아 fragment 로 전송
    override fun photoClicked(bool: Boolean, photo: List<Photo>) {
        if (bool) {
            val action =
                HomeFragmentDirections.actionNavigationHomeToPhotoDetailFragment(photo.toTypedArray())
            findNavController().navigate(action)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onClick(view: View?) {
        when (view) {
            //상단 바의 랜덤,팔로잉 버튼 각각 클릭 (임시로 select 여부만 변경 처리)
            binding.appBarFollowing -> { //팔로잉 버튼 클릭
                binding.appBarFollowing.isSelected = true
                binding.appBarRandom.isSelected = false
            }
            binding.topRandomMenuLayer -> { //랜덤 버튼 클릭
                binding.appBarFollowing.isSelected = false
                binding.appBarRandom.isSelected = true
            }

            //상단 바의 2,3장씩 보기 버튼 클릭
            binding.appBarPhotoGridModeBtn -> {
                when (binding.appBarPhotoGridModeBtn.text) {
                    "1" -> {
                        binding.appBarPhotoGridModeBtn.text = "2"
                        photoLayoutVisibilityMode(false) // grid visibility
                        editGridSpanCount(2)
                    }
                    "2" -> {
                        binding.appBarPhotoGridModeBtn.text = "3"
                        photoLayoutVisibilityMode(false) // grid visibility
                        editGridSpanCount(3)
                    }
                    "3" -> {
                        binding.appBarPhotoGridModeBtn.text = "1"
                        photoLayoutVisibilityMode(true) // default visibility
                    }
                }
                gridAdapter.notifyDataSetChanged()
            }

            //상단 바의 필터 버튼 클릭
            binding.appBarPhotoFilterBtn -> {
                findNavController().navigate(R.id.action_navigation_home_to_filterFragment)
            }
        }
    }


    private fun photoLayoutVisibilityMode(default: Boolean) {
        binding.homeMainViewpager.isVisible = default
        binding.gridRecyclerView.isVisible = !default
    }

    //vertical fragment 에서 공유버튼 클릭시 바텀 다이얼로그를 생성한다.
    override fun shareBtnClicked(bool: Boolean) {
        if (bool) {
            val dialog = HomeBottomSheetFragment()
            dialog.show(parentFragmentManager, "bottom_sheet")
        }
    }

    override fun photoZipBtnClicked(bool: Boolean) {
        findNavController().navigate(R.id.action_navigation_home_to_photoZipFragment)
    }

    override fun infoBtnClicked(bool: Boolean) {
        findNavController().navigate(R.id.action_navigation_home_to_userInfoFragment)
    }

    override fun gridPhotoClicked(postIndex:Int) {
        //grid 각 포토 클릭시 !!
    }
}