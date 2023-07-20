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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.lf.fashion.MainNaviDirections
import com.lf.fashion.R
import com.lf.fashion.data.common.PreferenceManager
import com.lf.fashion.data.network.Resource
import com.lf.fashion.data.response.*
import com.lf.fashion.databinding.HomeAFragmentBinding
import com.lf.fashion.ui.home.HomeViewModel
import com.lf.fashion.ui.home.PhotoClickListener
import com.lf.fashion.ui.home.VerticalViewPagerClickListener
import com.lf.fashion.ui.home.adapter.DefaultPostAdapter
import com.lf.fashion.ui.GridPostAdapter
import com.lf.fashion.ui.home.GridSpaceItemDecoration
import com.lf.fashion.ui.GridPhotoClickListener
import com.lf.fashion.ui.PrefCheckService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first

/**
 * 메인 포스트 스크롤 페이지 프래그먼트입니다.
 */
//TODO swipe refresh 추가
@AndroidEntryPoint
class HomeFragment : Fragment(), View.OnClickListener, PhotoClickListener,
    VerticalViewPagerClickListener,
    GridPhotoClickListener {
    private lateinit var binding: HomeAFragmentBinding
    private val viewModel: HomeViewModel by viewModels()

    // private val postList = MutableLiveData<List<Posts>>()
    private val gridAdapter = GridPostAdapter(gridPhotoClickListener = this)
    private val defaultAdapter = DefaultPostAdapter(this@HomeFragment, this@HomeFragment)
    private lateinit var userPref: PreferenceManager
 private lateinit var likeClickedPosts: Posts
    //private lateinit var layoutMode : String
    private lateinit var prefCheckService: PrefCheckService
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
        prefCheckService = PrefCheckService(userPref)

        runBlocking {
            launch {
                if (userPref.firstActivate.first().isNullOrEmpty()) {
                    val dialog = GenderSelectionDialog()
                    dialog.show(parentFragmentManager, "gender_selection_dialog")
                    userPref.isNotFirstActivate()  //테스트를 위해 계속 띄우려고 지워둠 ! -> 거슬려서 활성화
                    //TODO : 아직 다이얼로그 띄우기만하고 클릭시 api 요청 파라미터에 넣는 작업 x -> 필터기능 파라미터로 전부 정리되고나면 pref 에 넣어 관리할것
                }
            }
        }

        // 상단 메뉴 - 랜덤 모드 선택이 디폴트
        binding.appBarRandom.isSelected = true

        //기본 레이아웃 ui adapter 연결
        setMainViewPagerUI()

        //onclick listener 묶어서 한번에 달기
        binding.topMenu.children.forEach { it.setOnClickListener(this) }

        //좋아요 상태 변화 관찰&업데이트
        viewModel.changeLikeResponse.observe(viewLifecycleOwner) {
                resources ->
            if (resources is Resource.Success && resources.value.success != null) {
                val currentList = defaultAdapter.currentList
                val position = currentList.indexOf(likeClickedPosts)

                if (position != -1) {
                    defaultAdapter.currentList[position].apply {
                        isFavorite = likeClickedPosts.isFavorite
                        count.favorites = likeClickedPosts.count.favorites
                    }
                    defaultAdapter.notifyItemChanged(position,"FAVORITES_COUNT")

                }
            }


        }

    }

    private fun setMainViewPagerUI() {
        /*response 로 post 를 받아서 중첩 viewPager 와 recyclerView 모두에게 adapter 연결/submitList 후 visibility 로 노출을 관리한다
        (전환 속도 감소, 메모리에 무리가 가지않는다면 ok)*/
        photoLayoutVisibilityMode(true) // default ui visibility
        viewModel.response.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    val response = resource.value

                    //1개씩 보기 뷰페이저 세팅
                    with(binding.homeMainViewpager) {
                        adapter = defaultAdapter.apply {
                            submitList(response.posts)
                        }
                        getChildAt(0).overScrollMode =
                            RecyclerView.OVER_SCROLL_NEVER // 최상단,최하단 스크롤 이벤트 shadow 제거
                    }

                    //postList.value = response.posts

                    // 2-3개 씩 모아보기 리사이클러뷰 세팅
                    with(binding.gridRecyclerView) {
                        //staggeredGrid layoutManager 연결
                        layoutManager =
                            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
                        adapter = gridAdapter.apply {
                            addItemDecoration(GridSpaceItemDecoration(2, 6))
                            submitList(response.posts)
                        }
                        visibility = View.INVISIBLE // 기본 설정 invisible
                    }
                }
                is Resource.Loading -> {

                }
                else -> {

                }
            }
        }
    }

    private fun editGridSpanCount(spanCount: Int) {
        with(binding.gridRecyclerView) {
            layoutManager =
                StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL)
            while (itemDecorationCount > 0) { // 기존 추가한 itemDecoration 을 모두 지워주지않으면 점점 쌓인다.
                removeItemDecorationAt(0)
            }
            addItemDecoration(GridSpaceItemDecoration(spanCount, 6))
            gridAdapter.editSpanCountBtnClicked(spanCount)  // 이미지 높이 조정을 위한 리스너에 span 값 전송
        }
    }

    //TODO :  유저 프로필 페이지로 연결
    //default layout 모드에서 photo 클릭시 클릭한 이미지 url 만 safe args 에 담아 fragment 로 전송
    override fun photoClicked(bool: Boolean, photo: List<ImageUrl>) {
        if (bool) {
            val action =
                MainNaviDirections.actionGlobalToPhotoDetailFragment(photo.toTypedArray())
            findNavController().navigate(action)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onClick(view: View?) {
        when (view) {
            //상단 바의 랜덤,팔로잉 버튼 각각 클릭 (임시로 select 여부만 변경 처리)
            binding.topFollowingMenuLayer -> { //팔로잉 버튼 클릭
                binding.appBarFollowing.isSelected = true
                binding.appBarRandom.isSelected = false
            }
            binding.appBarRandom -> { //랜덤 버튼 클릭
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
                findNavController().navigate(R.id.action_global_to_filterFragment)
            }
        }
    }


    private fun photoLayoutVisibilityMode(default: Boolean) {
        binding.homeMainViewpager.isVisible = default
        binding.gridRecyclerView.isVisible = !default
    }

    override fun likeBtnClicked(likeState: Boolean, post: Posts) {
        if (prefCheckService.loginCheck()) {
            //likeState 기존 좋아요 상태
            when (likeState) {
                true -> {
                    viewModel.changeLikesState(create = false,post.id)
                    post.count.favorites = post.count.favorites?.minus(1) // 좋아요 카운트 -1
                }
                false -> {
                    viewModel.changeLikesState(create = true,post.id)
                    post.count.favorites = post.count.favorites?.plus(1)  // 좋아요 카운트 +1
                }
            }
            post.isFavorite = !post.isFavorite!!  // 좋아요 상태 반전
            likeClickedPosts = post
        }
    }

    //vertical fragment 에서 공유버튼 클릭시 바텀 다이얼로그를 생성한다.
    override fun shareBtnClicked() {

        val dialog = HomeBottomSheetFragment()
        dialog.show(parentFragmentManager, "bottom_sheet")

    }

    override fun photoZipBtnClicked() {
        findNavController().navigate(R.id.action_global_to_photoZipFragment)
    }

    override fun infoBtnClicked() {
        findNavController().navigate(R.id.action_global_to_userInfoFragment)
    }

    override fun gridPhotoClicked(postIndex: Int) {
        //grid 각 포토 클릭시 !!
    }
}