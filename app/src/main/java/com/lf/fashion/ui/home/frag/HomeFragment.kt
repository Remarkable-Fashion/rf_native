package com.lf.fashion.ui.home.frag

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.lf.fashion.MainNaviDirections
import com.lf.fashion.R
import com.lf.fashion.TAG
import com.lf.fashion.data.common.PostFilterDataStore
import com.lf.fashion.data.common.UserDataStorePref
import com.lf.fashion.data.network.Resource
import com.lf.fashion.data.model.*
import com.lf.fashion.databinding.HomeAFragmentBinding
import com.lf.fashion.ui.home.PhotoClickListener
import com.lf.fashion.ui.home.VerticalViewPagerClickListener
import com.lf.fashion.ui.globalFrag.adapter.DefaultPostAdapter
import com.lf.fashion.ui.globalFrag.adapter.GridPostAdapter
import com.lf.fashion.ui.home.GridSpaceItemDecoration
import com.lf.fashion.ui.globalFrag.adapter.GridPhotoClickListener
import com.lf.fashion.ui.common.MyBottomDialogListener
import com.lf.fashion.ui.globalFrag.bottomsheet.PostBottomSheetFragment
import com.lf.fashion.ui.common.showRequireLoginDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first

/**
 * 메인 포스트 스크롤 페이지 프래그먼트입니다.
 */
@AndroidEntryPoint
class HomeFragment :
    Fragment(R.layout.home_a_fragment),
    View.OnClickListener,
    PhotoClickListener,
    VerticalViewPagerClickListener,
    GridPhotoClickListener,
    MyBottomDialogListener {

    private lateinit var binding: HomeAFragmentBinding
    private val viewModel: HomeViewModel by viewModels()

    private val gridAdapter = GridPostAdapter(gridPhotoClickListener = this)
    private val defaultAdapter = DefaultPostAdapter(this@HomeFragment, this@HomeFragment)
    private lateinit var userPref: UserDataStorePref
    private lateinit var likeClickedPosts: Posts
    private lateinit var scrapClickedPosts: Posts
    private lateinit var filterDataStore: PostFilterDataStore
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = HomeAFragmentBinding.bind(view)

        //앱 최초 실행시 gender 선택 다이얼로그 띄우기
        userPref = UserDataStorePref(requireContext().applicationContext)
        filterDataStore = PostFilterDataStore(requireContext().applicationContext)

        //todo test
        CoroutineScope(Dispatchers.Main).launch {
            if (userPref.firstActivate.first().isNullOrEmpty()) {
                val dialog = GenderSelectionDialog {
                    Log.e(TAG, "onViewCreated: $it")
                    CoroutineScope(Dispatchers.IO).launch {
                        filterDataStore.saveGender(it)
                    }
                }
                dialog.show(parentFragmentManager, "gender_selection_dialog")
                userPref.isNotFirstActivate()
            }
        }

        /*response 로 post 를 받아서 중첩 viewPager 와 recyclerView 모두에게 adapter 연결/submitList 후 visibility 로 노출을 관리한다
              (전환 속도 감소, 메모리에 무리가 가지않는다면 ok)*/
        photoLayoutVisibilityMode(true) // default ui visibility
        viewModel.postMode.observe(viewLifecycleOwner) {
            requestPost()

            //상단 랜덤/팔로잉 모드 selected 적용
            val randomMode = (it == "random")
            binding.appBarRandom.isSelected = randomMode
            binding.appBarFollowing.isSelected = !randomMode
        }

        binding.topMenu.children.forEach { it.setOnClickListener(this) }    //onclick listener 묶어서 한번에 달기

        //좋아요 상태 변화 관찰&업데이트
        updateLikeState()
        //스크랩 상태 변화 관찰&업데이트
        updateScrapState()

        binding.homeMainViewpager.adapter = defaultAdapter
        binding.gridRecyclerView.adapter = gridAdapter

        //당겨서 새로고침
        binding.layoutSwipeRefreah.setOnRefreshListener {
            requestPost()
            return@setOnRefreshListener
        }

        observePostResponse()

        randomPostScrollEndPoint()
        observeLoadMorePost()
    }

    //todo 계속 같은 response만 옴
    private fun randomPostScrollEndPoint() {
        binding.homeMainViewpager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                val totalItemCount = binding.homeMainViewpager.adapter?.itemCount ?: 0
                if (position == totalItemCount - 1 && viewModel.postMode.value == "random") {
                    requestPost(loadMore = true)
                }
            }
        })

        binding.gridRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val staggeredGridLayoutManager =
                    binding.gridRecyclerView.layoutManager as StaggeredGridLayoutManager
                val lastVisibleItems =
                    staggeredGridLayoutManager.findLastCompletelyVisibleItemPositions(null)
                val totalItemCount = recyclerView.adapter?.itemCount ?: 0

                // StaggeredGridLayoutManager는 배열 형태로 마지막 행의 아이템 인덱스를 반환합니다.
                // 여러 열을 가진 경우, 가장 마지막에 보이는 아이템의 인덱스를 확인합니다.
                val lastVisibleItem = lastVisibleItems.maxOrNull() ?: -1

                if (lastVisibleItem == totalItemCount - 1&& viewModel.postMode.value == "random") {
                    // 마지막 아이템이 보이는 경우 처리할 내용을 여기에 추가
                    requestPost(loadMore = true)

                }
            }
        })
    }

    private fun requestPost(loadMore : Boolean?=null) {
        CoroutineScope(Dispatchers.IO).launch {
            with(filterDataStore) {
                val tpo = tpoId.first()?.split(",")?.mapNotNull { it.toIntOrNull() }
                val season = seasonId.first()?.split(",")?.mapNotNull { it.toIntOrNull() }
                val style = styleId.first()?.split(",")?.mapNotNull { it.toIntOrNull() }
                val gender = postGender.first() ?: "All"
                val height = height.first()
                val weight = weight.first()
                Log.e(TAG, "requestPost: $tpo ,$season ,$style", )
                withContext(Dispatchers.Main) {
                    viewModel.getPostList(loadMore,21, gender, height, weight, tpo, season, style)
                }
            }
        }
    }


    private fun observePostResponse() {
        viewModel.response.observe(viewLifecycleOwner) { resource ->
            binding.layoutSwipeRefreah.isRefreshing = false
            when (resource) {
                is Resource.Success -> {
                    val response = resource.value
                    val currentGridCount = binding.gridText.text.toString().toInt()
                    val spanCount = if (currentGridCount == 1 || currentGridCount == 2) 2 else 3
                    photoLayoutVisibilityMode(default = currentGridCount == 1)

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
                        //3개로 보고있다가 refresh하는 경우를 감안해서 view에서 grid count를 받아오기
                        //staggeredGrid layoutManager 연결
                        while (itemDecorationCount > 0) { // 기존 추가한 itemDecoration 을 모두 지워주지않으면 점점 쌓인다.
                            removeItemDecorationAt(0)
                        }
                        layoutManager =
                            StaggeredGridLayoutManager(
                                spanCount,
                                StaggeredGridLayoutManager.VERTICAL
                            )
                        adapter = gridAdapter.apply {
                            addItemDecoration(GridSpaceItemDecoration(spanCount, 6))
                            submitList(response.posts)
                        }

                    }
                }
                else -> {

                }
            }
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    private fun observeLoadMorePost(){
        viewModel.loadMore.observe(viewLifecycleOwner){resource->
            when(resource){
                is Resource.Success ->{
                    val morePost = resource.value
                    val currentList = gridAdapter.currentList.toMutableList()
                    Log.e(TAG, "observeLoadMorePost: currrent : ${currentList.size} , more : ${morePost.posts.size}")
                    currentList.addAll(morePost.posts)
                    Log.e(TAG, "observeLoadMorePost: 합 : $morePost")

                    gridAdapter.apply {
                        submitList(currentList)
                        notifyDataSetChanged()
                    }
                    defaultAdapter.apply {
                        submitList(currentList)
                        notifyDataSetChanged()
                    }
                }
                else ->{

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
                viewModel.postMode.value = "following"
            }

            binding.appBarRandom -> { //랜덤 버튼 클릭
                viewModel.postMode.value = "random"
            }

            //상단 바의 2,3장씩 보기 버튼 클릭
            binding.gridModeBtn -> {
                uiVisibilityUpdate(binding.gridText.text.toString())
                gridAdapter.notifyDataSetChanged()
            }

            //상단 바의 필터 버튼 클릭
            binding.filter -> {
                findNavController().navigate(R.id.action_home_fragment_to_filterFragment)
            }
        }

    }

    private fun uiVisibilityUpdate(currentGrid: String) {
        when (currentGrid) {
            "1" -> {
                binding.gridText.text = "2"
                photoLayoutVisibilityMode(false) // grid visibility
                editGridSpanCount(2)
            }

            "2" -> {
                binding.gridText.text = "3"
                photoLayoutVisibilityMode(false) // grid visibility
                editGridSpanCount(3)
            }

            "3" -> {
                binding.gridText.text = "1"
                photoLayoutVisibilityMode(true) // default visibility
            }
        }
    }


    private fun photoLayoutVisibilityMode(default: Boolean) {
        binding.homeMainViewpager.isVisible = default
        binding.gridRecyclerView.isVisible = !default
    }

    private fun updateLikeState() {
        viewModel.likeResponse.observe(viewLifecycleOwner) { resources ->
            if (resources is Resource.Success && resources.value.success) {
                val currentList = defaultAdapter.currentList
                val position = currentList.indexOf(likeClickedPosts)

                if (position != -1) {
                    defaultAdapter.currentList[position].apply {
                        isFavorite = likeClickedPosts.isFavorite
                        count.favorites = likeClickedPosts.count.favorites
                    }
                    defaultAdapter.notifyItemChanged(position, "FAVORITES_COUNT")

                }
            }
        }
    }

    private fun updateScrapState() {
        viewModel.scrapResponse.observe(viewLifecycleOwner) { resources ->
            if (resources is Resource.Success && resources.value.success) {
                val currentList = defaultAdapter.currentList
                val position = currentList.indexOf(scrapClickedPosts)

                if (position != -1) {
                    defaultAdapter.currentList[position].apply {
                        isScrap = scrapClickedPosts.isScrap
                    }
                    defaultAdapter.notifyItemChanged(position, "SCRAP_STATE")

                    gridAdapter.currentList[position].apply {
                        isScrap = scrapClickedPosts.isScrap
                    }
                    gridAdapter.notifyItemChanged(position, "SCRAP_STATE")
                }
            }
        }
    }

    override fun likeBtnClicked(likeState: Boolean, post: Posts) {
        if (userPref.loginCheck()) {
            //likeState 기존 좋아요 상태
            when (likeState) {
                true -> {
                    viewModel.changeLikesState(create = false, post.id)
                    post.count.favorites = post.count.favorites?.minus(1) // 좋아요 카운트 -1
                }

                false -> {
                    viewModel.changeLikesState(create = true, post.id)
                    post.count.favorites = post.count.favorites?.plus(1)  // 좋아요 카운트 +1
                }
            }
            post.isFavorite = !post.isFavorite!!  // 좋아요 상태 반전
            likeClickedPosts = post
        } else {
            showRequireLoginDialog()
        }
    }

    override fun scrapBtnClicked(scrapState: Boolean, post: Posts) {
        if (userPref.loginCheck()) {
            //scrapState 기존 스크랩 상태
            viewModel.changeScrapState(create = !scrapState, post.id)
            post.isScrap = !post.isScrap!!
            scrapClickedPosts = post
        } else {
            showRequireLoginDialog()
        }

    }

    //vertical fragment 에서 공유버튼 클릭시 바텀 다이얼로그를 생성한다.
    override fun shareBtnClicked(post: Posts) {

    }

    override fun kebabBtnClicked(post: Posts) {
        Log.d(TAG, "HomeFragment - kebabBtnClicked postId : ${post.id}");
        val dialog = PostBottomSheetFragment(post, myBottomDialogListener = this)
        dialog.show(childFragmentManager, "bottom_sheet")
    }

    override fun photoZipBtnClicked(post: Posts) {
        findNavController().navigate(
            R.id.action_navigation_home_to_photoZipFragment,
            bundleOf("post" to post)
        )
    }

    override fun infoBtnClicked(postId: Int) {
        findNavController().navigate(
            R.id.action_home_fragment_to_userInfoFragment,
            bundleOf("postId" to postId)
        )
    }

    //프로필 영역 클릭
    override fun profileSpaceClicked(userId: Int) {
        val myUniqueId = userPref.getMyUniqueId()
        if (userId == myUniqueId) {
            findNavController().navigate(R.id.navigation_mypage)
            return
        }
        findNavController().navigate(
            R.id.action_home_fragment_to_otherUserProfileFragment,
            bundleOf("userId" to userId)
        )
    }

    override fun gridPhotoClicked(postIndex: Int) {
        //grid 각 포토 클릭시 !!
        //defaultAdapter.startChangeByGridClicked()
        binding.homeMainViewpager.apply {
            setCurrentItem(postIndex, false)
        }
        binding.gridText.text = "1"
        photoLayoutVisibilityMode(true)

    }

    //PostBottomSheetFragment 를 통해 Scrap 할 시 , dismiss 되면 scrap 버튼 색상 selected 상태 update가 필요.
    override fun onBottomSheetDismissed(post: Posts) {
        val currentList = defaultAdapter.currentList
        val position = currentList.indexOf(post)

        if (position != -1) {
            defaultAdapter.currentList[position].apply {
                isScrap = post.isScrap
            }
            defaultAdapter.notifyItemChanged(position, "SCRAP_STATE")

            gridAdapter.currentList[position].apply {
                isScrap = post.isScrap
            }
            gridAdapter.notifyItemChanged(position, "SCRAP_STATE")
        }
    }

    override fun deleteMyPost(post: Posts) {
        CoroutineScope(Dispatchers.IO).launch {
            val msg = viewModel.deletePost(postId = post.id)
            if (msg.success) {
                withContext(Dispatchers.Main) {
                    val currentList = defaultAdapter.currentList.toMutableList()
                    val position = currentList.indexOfFirst { it.id == post.id }

                    if (position != -1) {
                        currentList.removeAt(position)
                        gridAdapter.submitList(currentList)
                        defaultAdapter.submitList(currentList)
                        Toast.makeText(requireContext(), "게시물이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun changePostPublicStatus(post: Posts) {
        CoroutineScope(Dispatchers.Main).launch {
            //기존 게시/미게시 상태의 반전
            val response = viewModel.changePostStatus(post.id, !(post.isPublic ?: true))
            if (response.success) {
                Toast.makeText(requireContext(), "게시물의 상태가 변경되었습니다", Toast.LENGTH_SHORT).show()
                val currentList = defaultAdapter.currentList
                val position = currentList.indexOf(post)
                if (position != -1) {
                    defaultAdapter.currentList[position].apply {
                        isPublic = !isPublic!!
                    }
                    defaultAdapter.notifyItemChanged(position, "PUBLIC_STATE")
                }
            }
        }
    }

    override fun editPost(post: Posts) {
        findNavController().navigate(R.id.action_global_to_editPostFragment, bundleOf("post" to post))
    }
}
