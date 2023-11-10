package com.lf.fashion.ui.home.profile

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.lf.fashion.MainNaviDirections
import com.lf.fashion.R
import com.lf.fashion.TAG
import com.lf.fashion.data.common.UserDataStorePref
import com.lf.fashion.data.network.Resource
import com.lf.fashion.data.model.ImageUrl
import com.lf.fashion.data.model.Posts
import com.lf.fashion.data.model.UserInfo
import com.lf.fashion.databinding.UserVerticalFragmentBinding
import com.lf.fashion.ui.common.CopyLink
import com.lf.fashion.ui.common.CreateDynamicLink
import com.lf.fashion.ui.common.MyBottomDialogListener
import com.lf.fashion.ui.common.cancelBtnBackStack
import com.lf.fashion.ui.home.PhotoClickListener
import com.lf.fashion.ui.home.VerticalViewPagerClickListener
import com.lf.fashion.ui.globalFrag.adapter.DefaultPostAdapter
import com.lf.fashion.ui.globalFrag.bottomsheet.PostBottomSheetFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class OtherUserVerticalPostFragment : Fragment(R.layout.user_vertical_fragment),
    PhotoClickListener, VerticalViewPagerClickListener, MyBottomDialogListener {
    private lateinit var binding: UserVerticalFragmentBinding
    private val viewModel: OtherUserProfileViewModel by hiltNavGraphViewModels(R.id.otherUserProfileFragment)
    private val defaultAdapter = DefaultPostAdapter(
        this@OtherUserVerticalPostFragment,
        this@OtherUserVerticalPostFragment
    )
    private lateinit var userPref: UserDataStorePref

    private lateinit var likeClickedPosts: Posts
    private lateinit var scrapClickedPosts: Posts
    private lateinit var userInfo: UserInfo

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = UserVerticalFragmentBinding.bind(view)
        cancelBtnBackStack(binding.backBtn)
        userPref = UserDataStorePref(requireContext().applicationContext)

        viewModel.profileInfo.observe(viewLifecycleOwner) { resources ->
            if (resources is Resource.Success) {
                val it = resources.value
                userInfo = UserInfo(it.id, it.name, it.profile, null)
            }
        }
        viewModel.postResponse.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    val response = resource.value
                    binding.verticalViewpager.apply {
                        adapter = defaultAdapter
                        (adapter as? DefaultPostAdapter)?.apply {
                            response.posts.forEach {
                                it.user = userInfo
                            } // userInfo 가 null 이기때문에 채우기 (mypagevertical 에서는 필요없다)
                            submitList(response.posts)
                            //scrapFragment 에서 선택한 item 의 index 를 시작 index 로 지정 , animation false 처리
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
    }

    override fun photoClicked(bool: Boolean, photo: List<ImageUrl>) {
        if (bool) {
            val action =
                MainNaviDirections.actionGlobalToPhotoDetailFragment(photo.toTypedArray())
            findNavController().navigate(action)
        }
    }

    override fun likeBtnClicked(likeState: Boolean, post: Posts) {
        runBlocking {
            launch {
                val response = viewModel.changeLikesState(create = !likeState, post.id)
                if (response is Resource.Success && response.value.success) {

                    post.count.favorites = if (likeState) post.count.favorites?.minus(1) else {
                        post.count.favorites?.plus(1)
                    }
                    post.isFavorite = !post.isFavorite!!  // 좋아요 상태 반전
                    likeClickedPosts = post
                    updateLikeState()

                }
            }
        }
    }

    override fun scrapBtnClicked(scrapState: Boolean, post: Posts) {
        //scrapState 기존 스크랩 상태
        runBlocking {
            launch {
                val response = viewModel.changeScrapState(create = !scrapState, post.id)
                if (response is Resource.Success && response.value.success) {

                    post.isScrap = !(post.isScrap ?: true)
                    scrapClickedPosts = post
                    updateScrapState()
                }

            }
        }
    }

    private fun updateLikeState() {

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

    private fun updateScrapState() {
        val currentList = defaultAdapter.currentList
        val position = currentList.indexOf(scrapClickedPosts)

        if (position != -1) {
            defaultAdapter.currentList[position].apply {
                isScrap = scrapClickedPosts.isScrap
            }
            defaultAdapter.notifyItemChanged(position, "SCRAP_STATE")
        }
    }

    override fun shareBtnClicked(post: Posts) {
        CreateDynamicLink(requireContext(), "post" , post.id)
    }

    override fun kebabBtnClicked(post: Posts) {
        val dialog = PostBottomSheetFragment(post , userShareOnclick = null){
            CopyLink().copyTextToClipboard(requireContext(),post.id,"post")
        }
        dialog.show(parentFragmentManager, "bottom_sheet")
    }

    override fun photoZipBtnClicked(post: Posts) {
        post.user = userInfo
        findNavController().navigate(
            R.id.action_otherUserVerticalPostFragment_to_photoZipFragment,
            bundleOf("post" to post)
        )
    }

    override fun infoBtnClicked(postId: Int) {
        findNavController().navigate(
            R.id.action_otherUserVerticalPostFragment_to_userInfoFragment,
            bundleOf("postId" to postId)
        )
    }

    override fun profileSpaceClicked(userId: Int) {
        val myUniqueId = userPref.getMyUniqueId()
        if (userId == myUniqueId) {
            findNavController().navigate(R.id.navigation_mypage)
            return
        }
        findNavController().navigateUp()
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
    override fun shareBtn(post: Posts) {
        CreateDynamicLink(requireContext(), "post" , post.id)
    }



    override fun deleteMyPost(post: Posts) {
    }

    override fun changePostPublicStatus(post: Posts) {
    }

    override fun editPost(post: Posts) {
    }


}