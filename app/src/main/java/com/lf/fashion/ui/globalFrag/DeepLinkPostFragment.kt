package com.lf.fashion.ui.globalFrag

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayoutMediator
import com.lf.fashion.MainActivity
import com.lf.fashion.MainNaviDirections
import com.lf.fashion.R
import com.lf.fashion.TAG
import com.lf.fashion.data.common.UserDataStorePref
import com.lf.fashion.data.model.ImageUrl
import com.lf.fashion.data.model.Posts
import com.lf.fashion.data.network.Resource
import com.lf.fashion.databinding.DeeplinkPostFragmentBinding
import com.lf.fashion.ui.common.CreateDynamicLink
import com.lf.fashion.ui.common.MyBottomDialogListener
import com.lf.fashion.ui.common.handleApiError
import com.lf.fashion.ui.common.showRequireLoginDialog
import com.lf.fashion.ui.globalFrag.bottomsheet.PostBottomSheetFragment
import com.lf.fashion.ui.home.PhotoClickListener
import com.lf.fashion.ui.home.VerticalViewPagerClickListener
import com.lf.fashion.ui.home.adapter.PhotoHorizontalAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DeepLinkPostFragment : Fragment(R.layout.deeplink_post_fragment),
    PhotoClickListener, VerticalViewPagerClickListener, MyBottomDialogListener {
    private lateinit var binding: DeeplinkPostFragmentBinding
    private val viewModel: DeepLinkViewModel by viewModels()
    private lateinit var userPref: UserDataStorePref
    private lateinit var likeClickedPosts: Posts
    private lateinit var scrapClickedPosts: Posts
    private val nestedAdapter = PhotoHorizontalAdapter(this)
    private lateinit var post: Posts
    /*private val defaultAdapter = DeepLinkPostAdapter(
        this@DeepLinkPostFragment, this@DeepLinkPostFragment)*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainActivity.hideNavi(state = true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DeeplinkPostFragmentBinding.bind(view)
        userPref = UserDataStorePref(requireContext().applicationContext)

        val myUniqueId = userPref.getMyUniqueId()
        val postId = arguments?.getString("postId") ?: return

        Log.e(TAG, "onePostFragment onviewCreated: $postId")


        viewModel.getPost(postId.toInt(), myUniqueId)
        viewModel.response.observe(viewLifecycleOwner) { resources ->
            when (resources) {
                is Resource.Success -> {
                    Log.e(TAG, "to POSTS: ${resources.value}")

                    val userIdForPhotoZip = arguments?.getString("photoZip")
                    userIdForPhotoZip?.let {
                        findNavController().navigate(
                            R.id.action_navigation_deeplink_to_photoZipFragment,
                            bundleOf("post" to resources.value)
                        )
                    }
                    val userIdForUserInfo = arguments?.getString("userInfo")
                    userIdForUserInfo?.let {
                        findNavController().navigate(
                            R.id.action_deeplink_fragment_to_userInfoFragment,
                            bundleOf("postId" to postId)
                        )
                    }
                    val userIdForRecommendCloth = arguments?.getString("recommend")
                    userIdForRecommendCloth?.let {
                        findNavController().navigate(
                            R.id.action_deeplink_to_recommendFragment,
                            bundleOf("postId" to postId)
                        )
                    }
                    with(binding.horizontalViewPager) {
                        adapter = nestedAdapter.apply {
                            submitList(resources.value.images)
                        }
                        getChildAt(0).overScrollMode =
                            RecyclerView.OVER_SCROLL_NEVER // 양옆 오버 스크롤 이벤트 shadow 제거

                        TabLayoutMediator(
                            binding.viewpagerIndicator,
                            this
                        ) { _, _ -> }.attach()
                    }
                    post = resources.value
                    binding.post = post
                    with(binding.postDetailMenu) {
                        likeBtn.isSelected = post.isFavorite ?: false
                        likesValue.text = post.count.favorites.toString()
                        scrapBtn.isSelected = post.isScrap ?: true //null 인 경우는 내 스크랩 모아보기이기 때문에, 모두 true
                    }
                    /* binding.horizontalViewPager.apply {
                         adapter = defaultAdapter
                         (adapter as? DefaultPostAdapter)?.apply {
                             submitList(listOf(resources.value))
                         }
                         getChildAt(0).overScrollMode =
                             RecyclerView.OVER_SCROLL_NEVER // 최상단,최하단 스크롤 이벤트 shadow 제거
                     }*/
                }

                is Resource.Failure -> {
                    handleApiError(resources)
                }

                else -> {}
            }

        }
        buttonClick()
        updateLikeState()
        updateScrapState()
    }

    private fun buttonClick() {
        val postDetailMenu = binding.postDetailMenu
        with(postDetailMenu) {
            likeBtn.setOnClickListener {
                //if userPref logincheck !
                likeBtnClicked(it.isSelected, post)

            }

            scrapBtn.setOnClickListener {
                scrapBtnClicked(it.isSelected, post)
            }
            shareBtn.setOnClickListener {
                shareBtnClicked(post)
            }
            photoZipBtn.setOnClickListener {
                photoZipBtnClicked(post)
            }
            kebabBtn.setOnClickListener {
                Log.d(TAG, "kebabBtnClicked In DefaultAdapter  - postId : $post.id");
                kebabBtnClicked(post)
            }

        }

        binding.infoBtn.setOnClickListener {
            infoBtnClicked(post.id)
        }
        binding.profileSpace.setOnClickListener {
            if (post.user == null) return@setOnClickListener
            profileSpaceClicked(post.user!!.id)
        }
        binding.morePostBtn.setOnClickListener {
            findNavController().popBackStack()
        }
    }
    private fun updateLikeState() {
        viewModel.likeResponse.observe(viewLifecycleOwner) { resources ->
            if (resources is Resource.Success && resources.value.success) {
                binding.postDetailMenu.likeBtn.isSelected = !binding.postDetailMenu.likeBtn.isSelected
            }
        }
    }
    private fun updateScrapState() {
        viewModel.scrapResponse.observe(viewLifecycleOwner) { resources ->
            if (resources is Resource.Success && resources.value.success) {
                binding.postDetailMenu.scrapBtn.isSelected = !binding.postDetailMenu.scrapBtn.isSelected
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
            showRequireLoginDialog(true)
        }
    }

    override fun scrapBtnClicked(scrapState: Boolean, post: Posts) {
        if (userPref.loginCheck()) {
            //scrapState 기존 스크랩 상태
            viewModel.changeScrapState(create = !scrapState, post.id)
            post.isScrap = !post.isScrap!!
            scrapClickedPosts = post
        } else {
            showRequireLoginDialog(true)
        }
    }

    override fun shareBtnClicked(post: Posts) {

    }

    override fun kebabBtnClicked(post: Posts) {
        Log.d(TAG, "HomeFragment - kebabBtnClicked postId : ${post.id}");
        val dialog = PostBottomSheetFragment(post, myBottomDialogListener = this)
        dialog.show(childFragmentManager, "bottom_sheet")
    }

    override fun photoZipBtnClicked(post: Posts) {
        if (userPref.loginCheck()) {
            findNavController().navigate(
                R.id.action_navigation_deeplink_to_photoZipFragment,
                bundleOf("post" to post)
            )
        } else {
            showRequireLoginDialog(true)
        }
    }


    override fun infoBtnClicked(postId: Int) {
        if (userPref.loginCheck()) {
            findNavController().navigate(
                R.id.action_deeplink_fragment_to_userInfoFragment,
                bundleOf("postId" to postId)
            )
        } else {
            showRequireLoginDialog(true)
        }
    }

    override fun profileSpaceClicked(userId: Int) {
        if (userPref.loginCheck()) {
            val myUniqueId = userPref.getMyUniqueId()
            if (userId == myUniqueId) {
                findNavController().navigate(R.id.navigation_mypage)
                return
            }
            findNavController().navigate(
                R.id.action_deeplink_fragment_to_otherUserProfileFragment,
                bundleOf("userId" to userId)
            )
        } else {
            showRequireLoginDialog(true)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        MainActivity.hideNavi(false)
    }

    override fun onBottomSheetDismissed(post: Posts) {
        /* val currentList = defaultAdapter.currentList
         val position = currentList.indexOf(post)

         if (position != -1) {
             defaultAdapter.currentList[position].apply {
                 isScrap = post.isScrap
             }
             defaultAdapter.notifyItemChanged(position, "SCRAP_STATE")

         }*/
    }

    override fun deleteMyPost(post: Posts) {
        CoroutineScope(Dispatchers.IO).launch {
            val msg = viewModel.deletePost(postId = post.id)
            if (msg.success) {
                Toast.makeText(requireContext(), "게시물이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
        }
    }

    override fun changePostPublicStatus(post: Posts) {
        CoroutineScope(Dispatchers.Main).launch {
            //기존 게시/미게시 상태의 반전
            val response = viewModel.changePostStatus(post.id, false)
            if (response.success) {
                Toast.makeText(requireContext(), "게시물의 상태가 변경되었습니다", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()

                /* val currentList = defaultAdapter.currentList
                 val position = currentList.indexOf(post)
                 if (position != -1) {
                     defaultAdapter.currentList[position].apply {
                         isPublic = !isPublic!!
                     }
                     defaultAdapter.notifyItemChanged(position, "PUBLIC_STATE")
                 }*/
            }
        }
    }

    override fun editPost(post: Posts) {
        findNavController().navigate(
            R.id.action_global_to_editPostFragment,
            bundleOf("post" to post)
        )
    }

    override fun shareBtn(post: Posts) {
        CreateDynamicLink(requireContext(), "post", post.id)
    }
}