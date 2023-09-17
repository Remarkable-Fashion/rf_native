package com.lf.fashion.ui.mypage

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.lf.fashion.MainActivity
import com.lf.fashion.MainNaviDirections
import com.lf.fashion.R
import com.lf.fashion.TAG
import com.lf.fashion.data.common.UserDataStorePref
import com.lf.fashion.data.network.Resource
import com.lf.fashion.data.model.ImageUrl
import com.lf.fashion.data.model.Posts
import com.lf.fashion.databinding.MypagePhotozipVerticalFragmentBinding
import com.lf.fashion.ui.MyBottomDialogListener
import com.lf.fashion.ui.cancelBtnBackStack
import com.lf.fashion.ui.home.PhotoClickListener
import com.lf.fashion.ui.home.VerticalViewPagerClickListener
import com.lf.fashion.ui.home.adapter.DefaultPostAdapter
import com.lf.fashion.ui.home.frag.PostBottomSheetFragment
import com.lf.fashion.ui.home.photozip.PhotoZipViewModel
import com.lf.fashion.ui.navigateToMyPage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class MyPhotoZipVerticalFragment : Fragment(R.layout.mypage_photozip_vertical_fragment),
    PhotoClickListener, VerticalViewPagerClickListener, MyBottomDialogListener {
    private lateinit var binding: MypagePhotozipVerticalFragmentBinding
    private val viewModel: PhotoZipViewModel by hiltNavGraphViewModels(R.id.navigation_mypage)
    private lateinit var defaultAdapter: DefaultPostAdapter
    private lateinit var userPref: UserDataStorePref

    private lateinit var likeClickedPosts: Posts
    private lateinit var scrapClickedPosts: Posts
    override fun onCreate(savedInstanceState: Bundle?) {
        MainActivity.hideNavi(true)
        super.onCreate(savedInstanceState)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bottomNavigationView =
            requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavBar)
        val myPage = bottomNavigationView.menu.findItem(R.id.navigation_mypage)
        myPage.isChecked = true
        Log.e(TAG, " onviewCreated : My vertical fragment !! ")

        binding = MypagePhotozipVerticalFragmentBinding.bind(view)
        cancelBtnBackStack(binding.backBtn)
        userPref = UserDataStorePref(requireContext().applicationContext)

        val userInfoPost = arguments?.get("userInfoPost") as Posts
        defaultAdapter = DefaultPostAdapter(
            this@MyPhotoZipVerticalFragment,
            this@MyPhotoZipVerticalFragment,
            userInfoPost,true
        )

        viewModel.posts.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    val response = resource.value
                    binding.verticalViewpager.apply {
                        adapter = defaultAdapter
                        (adapter as? DefaultPostAdapter)?.apply {
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
        val dialog = PostBottomSheetFragment(post, myBottomDialogListener = this)
        dialog.show(childFragmentManager, "bottom_sheet")
    }

    override fun photoZipBtnClicked(post: Posts) {
        findNavController().navigateUp()
       /* findNavController().navigate(
            R.id.action_global_to_photoZipFragment, bundleOf("post" to post)
        )*/
    }

    override fun infoBtnClicked(postId: Int) {
        findNavController().navigate(
            R.id.action_global_to_userInfoFragment,
            bundleOf("postId" to postId)
        )
    }

    override fun profileSpaceClicked(userId: Int) {
        val myUniqueId = userPref.getMyUniqueId()
        if (userId == myUniqueId) {
            navigateToMyPage(R.id.myPhotoZipVerticalFragment)
            return
        }
        findNavController().navigate(
            R.id.action_global_to_otherUSerFragment,
            bundleOf("userId" to userId)
        )
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

    override fun deleteMyPost(post: Posts) {
        CoroutineScope(Dispatchers.IO).launch {
            val msg = viewModel.deletePost(postId = post.id)
            if (msg.success) {
                withContext(Dispatchers.Main) {
                    val currentList = defaultAdapter.currentList.toMutableList()
                    val position = currentList.indexOfFirst { it.id == post.id }

                    if (position != -1) {
                        currentList.removeAt(position)
                        defaultAdapter.submitList(currentList)
                        Toast.makeText(requireContext(), "게시물이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }    }

    override fun changePostPublicStatus(post: Posts) {
        CoroutineScope(Dispatchers.Main).launch {
            //기존 게시/미게시 상태의 반전
            val response = viewModel.changePostStatus(post.id, !(post.isPublic ?: true))
            if(response.success){
                Toast.makeText(requireContext(),"게시물의 상태가 변경되었습니다",Toast.LENGTH_SHORT).show()
                val currentList = defaultAdapter.currentList
                val position = currentList.indexOf(post)
                if(position != -1){
                    defaultAdapter.currentList[position].apply {
                        isPublic = !isPublic!!
                    }
                    defaultAdapter.notifyItemChanged(position,"PUBLIC_STATE")
                }            }
        }
    }

    override fun onDestroy() {
        MainActivity.hideNavi(false)
        super.onDestroy()
    }
}