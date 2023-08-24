package com.lf.fashion.ui.mypage

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.lf.fashion.R
import com.lf.fashion.ui.cancelBtnBackStack
import com.lf.fashion.ui.home.PhotoClickListener
import com.lf.fashion.ui.home.VerticalViewPagerClickListener
import com.lf.fashion.ui.home.adapter.DefaultPostAdapter
import com.lf.fashion.ui.home.frag.PostBottomSheetFragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.lf.fashion.MainNaviDirections
import com.lf.fashion.TAG
import com.lf.fashion.data.network.Resource
import com.lf.fashion.data.model.ImageUrl
import com.lf.fashion.data.model.Posts
import com.lf.fashion.data.model.UserInfo
import com.lf.fashion.databinding.MyVerticalFragmentBinding
import com.lf.fashion.ui.MyBottomDialogListener
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MyPageVerticalFragment : Fragment(),
    PhotoClickListener, VerticalViewPagerClickListener,
    MyBottomDialogListener {
    private lateinit var binding: MyVerticalFragmentBinding
    private val viewModel: MyPageViewModel by hiltNavGraphViewModels(R.id.navigation_mypage) // hilt navi 함께 사용할때 viewModel 공유
    private val defaultAdapter = DefaultPostAdapter(
        this@MyPageVerticalFragment,
        this@MyPageVerticalFragment
    )
    private lateinit var likeClickedPosts: Posts
    private lateinit var scrapClickedPosts: Posts
    private lateinit var userInfo: UserInfo
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val bottomNavigationView =
            requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavBar)
        val loginMenuItem = bottomNavigationView.menu.findItem(R.id.navigation_mypage)
        loginMenuItem.isChecked = true
        bottomNavigationView.selectedItemId = R.id.navigation_mypage

        binding = MyVerticalFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cancelBtnBackStack(binding.backBtn)
        viewModel.myInfo.observe(viewLifecycleOwner) {
            userInfo = UserInfo(it.id, it.name, it.profile, null)
        }
        // val postList = arguments?.get("postList") as List<Posts>
        viewModel.postResponse.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    val response = resource.value
                    binding.verticalViewpager.apply {
                        adapter = defaultAdapter
                        (adapter as? DefaultPostAdapter)?.apply {
                            submitList(response.posts)
                            //scrapFragment 에서 선택한 item 의 index 를 시작 index 로 지정 , animation false 처리
                            Log.d(
                                TAG,
                                "MyPageVerticalFragment - onViewCreated: ${viewModel.startIndex.value}"
                            );
                            setCurrentItem(viewModel.startIndex.value ?: 0, false)
                        }
                        getChildAt(0).overScrollMode =
                            RecyclerView.OVER_SCROLL_NEVER // 최상단,최하단 스크롤 이벤트 shadow 제거
                    }
                }

                is Resource.Failure -> {

                }

                is Resource.Loading -> {

                }
            }
        }

        //좋아요 상태 변화 관찰&업데이트
        updateLikeState()
        updateScrapState()
    }

    private fun updateLikeState() {
        viewModel.changeLikeResponse.observe(viewLifecycleOwner) { resources ->
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
            if (resources is Resource.Success && resources.value.success && this::scrapClickedPosts.isInitialized) {
                val currentList = defaultAdapter.currentList
                val position = currentList.indexOf(scrapClickedPosts)

                if (position != -1) {
                    defaultAdapter.currentList[position].apply {
                        isScrap = scrapClickedPosts.isScrap
                    }
                    defaultAdapter.notifyItemChanged(position, "SCRAP_STATE")
                }
            }
        }
    }

    override fun photoClicked(bool: Boolean, photo: List<ImageUrl>) {
        if (bool) {
            val action =
                MainNaviDirections.actionGlobalToPhotoDetailFragment(photo.toTypedArray())
            findNavController().navigate(action)
        }
    }

    override fun likeBtnClicked(likeState: Boolean, post: Posts) {
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
    }

    override fun scrapBtnClicked(scrapState: Boolean, post: Posts) {
        //scrapState 기존 스크랩 상태
        viewModel.changeScrapState(create = !scrapState, post.id)
        post.isScrap = !(post.isScrap ?: true)
        scrapClickedPosts = post
    }

    override fun shareBtnClicked(post: Posts) {

    }

    override fun kebabBtnClicked(post: Posts) {
        val dialog = PostBottomSheetFragment(post)
        dialog.show(parentFragmentManager, "bottom_sheet")
    }

    override fun photoZipBtnClicked(post: Posts) {
        post.user = userInfo
        findNavController().navigate(
            R.id.action_global_to_photoZipFragment, bundleOf("post" to post)
        )
    }

    override fun infoBtnClicked(postId: Int) {
        findNavController().navigate(
            R.id.action_global_to_userInfoFragment,
            bundleOf("postId" to postId)
        )
    }

    override fun profileSpaceClicked(userId: Int) {
        //mypage Vertical 에서는 profile space 가 invisible !
    }

    override fun onBottomSheetDismissed(post: Posts) {
        val currentList = defaultAdapter.currentList
        val position = currentList.indexOf(post)

        if (position != -1) {
            defaultAdapter.currentList[position].apply {
                isScrap = post.isScrap
            }
            defaultAdapter.notifyItemChanged(position, "SCRAP_STATE")
        }
    }
}