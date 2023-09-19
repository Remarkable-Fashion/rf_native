package com.lf.fashion.ui.home.photozip

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.lf.fashion.R
import com.lf.fashion.TAG
import com.lf.fashion.data.common.UserDataStorePref
import com.lf.fashion.data.network.Resource
import com.lf.fashion.data.model.Posts
import com.lf.fashion.data.model.UserInfo
import com.lf.fashion.databinding.HomeBPhotoZipFragmentBinding
import com.lf.fashion.ui.home.GridSpaceItemDecoration
import com.lf.fashion.ui.GridPhotoClickListener
import com.lf.fashion.ui.GridPostAdapter
import com.lf.fashion.ui.home.frag.PostBottomSheetFragment
import com.lf.fashion.ui.showRequireLoginDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlin.properties.Delegates

/**
 * 메인 홈에서 유저 클릭시 노출되는 특정 유저 사진 모아보기 프래그먼트입니다.
 */
@AndroidEntryPoint
class PhotoZipFragment : Fragment(R.layout.home_b_photo_zip_fragment), GridPhotoClickListener {
    lateinit var binding: HomeBPhotoZipFragmentBinding
    private val viewModel: PhotoZipViewModel by hiltNavGraphViewModels(R.id.navigation_home)
    private var userId by Delegates.notNull<Int>()
    private lateinit var userInfoPost: Posts
    private lateinit var userPref: UserDataStorePref

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.e(TAG, "onViewCreated: photozip")
        binding = HomeBPhotoZipFragmentBinding.bind(view)
        userPref = UserDataStorePref(requireContext().applicationContext)

        viewModel.bundlePost = arguments?.get("post") as Posts
        if (viewModel.bundlePost == null) return

        userInfoPost = viewModel.bundlePost!! // photoZip 엔드포인트 response 에는 post 내부에 user 정보가 없어서 vertical Fragment 로 이동시 같이 보낸다
       // binding.userInfo = post.user

        followStateBinding(userInfoPost)
        followBtnOnclick()
        // 팔로우 응답 success -> ui update
        updateFollowingState()

        viewModel.getPostByUserId(userInfoPost.user!!.id)
        viewModel.getProfileInfoByUserId(userInfoPost.user!!.id)
        viewModel.profileInfo.observe(viewLifecycleOwner){
            if (it is Resource.Success) {
                val profile = it.value
                binding.userInfo = UserInfo(profile.id,profile.name,profile.profile,null)
                //binding.userInfo = it.value

            }
        }
        with(binding.gridRv) { //grid layout
            layoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
            adapter = GridPostAdapter(3, this@PhotoZipFragment, null,reduceViewWidth = true).apply {
                viewModel.posts.observe(viewLifecycleOwner) { resource ->
                    binding.layoutSwipeRefreah.isRefreshing = false
                    when (resource) {
                        is Resource.Success -> {
                            val response = resource.value

                            while (itemDecorationCount > 0) { // 기존 추가한 itemDecoration 을 모두 지워주지않으면 점점 쌓인다.
                                removeItemDecorationAt(0)
                            }
                            addItemDecoration(GridSpaceItemDecoration(3, 6))
                            //postList.addAll(response.posts)
                            submitList(response.posts)
                        }

                        is Resource.Loading -> {

                        }

                        else -> {

                        }
                    }
                }
            }
        }

    /*    //vertical 뷰에서 포스트를 삭제한 경우 refresh 하는 코드!
        viewModel.havetoRefresh.observe(viewLifecycleOwner){ it->
            if(it) {
                viewModel.getPostByUserId(post.user!!.id)
                viewModel.getProfileInfoByUserId(post.user!!.id)
                viewModel.havetoRefresh.value = false
            }
        }*/

        profileKebabBtnOnClick()

        binding.layoutSwipeRefreah.setOnRefreshListener {
            userInfoPost.user?.let {
                viewModel.getPostByUserId(it.id)
                viewModel.getProfileInfoByUserId(it.id)
            }
        }
    }

    private fun followStateBinding(post: Posts) {
        //나의 사진 모아보기일 경우 팔로우 버튼을 숨김 (post.user.id == me.id)
        val myUniqueId = userPref.getMyUniqueId()
        val followBtn = binding.followBtn
        followBtn.isVisible = myUniqueId != post.user?.id
        followBtn.isSelected =
            post.isFollow ?: false
        Log.e(TAG, "followStateBinding: ${post.isFollow}")
        followBtn.text = if (followBtn.isSelected) "팔로잉" else "+ 팔로우"
        userId = post.user!!.id
    }

    private fun followBtnOnclick() {
        binding.followBtn.setOnClickListener {
            val followBtn = binding.followBtn
            if (userPref.loginCheck()) {
                //팔로우 create / delete
                viewModel.changeFollowingState(!followBtn.isSelected, userId)
            }else{
                showRequireLoginDialog()
            }
        }
    }

    private fun updateFollowingState() {
        viewModel.followResponse.observe(viewLifecycleOwner) { resources ->
            if (resources is Resource.Success && resources.value.success) {
                val followBtn = binding.followBtn

                //팔로우 버튼 ui 반전
                followBtn.isSelected =
                    !followBtn.isSelected

                if (followBtn.isSelected) {
                    followBtn.text = "팔로잉"
                } else {
                    followBtn.text = "+ 팔로우"
                }
            }
        }
    }

    override fun gridPhotoClicked(postIndex: Int) {
        //grid 포토 클릭시!!
        viewModel.editClickedPostIndex(postIndex)
        //,
        //            bundleOf("postList" to postList)
        findNavController().navigate(
            R.id.action_photoZipFragment_to_photoZipVerticalFragment,
            bundleOf("userInfoPost" to userInfoPost)
        )
    }

    private fun profileKebabBtnOnClick() {
        binding.kebabBtn.setOnClickListener {
            val dialog = PostBottomSheetFragment(userId = userInfoPost.user?.id)
            dialog.show(parentFragmentManager, "bottom_sheet")
        }
    }
}