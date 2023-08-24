package com.lf.fashion.ui.home.photozip

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.lf.fashion.R
import com.lf.fashion.data.common.PreferenceManager
import com.lf.fashion.data.network.Resource
import com.lf.fashion.data.response.Posts
import com.lf.fashion.data.response.UserInfo
import com.lf.fashion.databinding.HomeBPhotoZipFragmentBinding
import com.lf.fashion.ui.home.GridSpaceItemDecoration
import com.lf.fashion.ui.GridPhotoClickListener
import com.lf.fashion.ui.GridPostAdapter
import com.lf.fashion.ui.home.frag.PostBottomSheetFragment
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
    // private var postList = mutableListOf<Posts>()

    // private var followState by Delegates.notNull<Boolean>()

    private lateinit var userPref: PreferenceManager


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //생성 후 다른 바텀 메뉴 이동시 다시 home menu 클릭시 selected 아이콘으로 변경 안되는 오류 해결하기위해 수동 메뉴 checked 코드 추가
        val bottomNavigationView =
            requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavBar)
        val homeMenu = bottomNavigationView.menu.findItem(R.id.navigation_home)
        homeMenu.isChecked = true

        binding = HomeBPhotoZipFragmentBinding.bind(view)
        userPref = PreferenceManager(requireContext().applicationContext)

        val post = arguments?.get("post") as Posts
        userInfoPost =
            post // photoZip 엔드포인트 response 에는 post 내부에 user 정보가 없어서 vertical Fragment 로 이동시 같이 보낸다
        if (post.user == null) return
       // binding.userInfo = post.user

        followStateBinding(post)
        followBtnOnclick()
        // 팔로우 응답 success -> ui update
        updateFollowingState()

        viewModel.getPostByUserId(post.user!!.id)
        viewModel.getProfileInfoByUserId(post.user!!.id)
        viewModel.profileInfo.observe(viewLifecycleOwner){
            if (it is Resource.Success) {
                val profile = it.value
                binding.userInfo = UserInfo(profile.id,profile.name,profile.profile,null)
                //binding.userInfo = it.value

            }
        }
        with(binding.gridRv) { //grid layout
            adapter = GridPostAdapter(3, this@PhotoZipFragment, null).apply {
                viewModel.posts.observe(viewLifecycleOwner) { resource ->
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

        profileKebabBtnOnClick()

    }

    private fun followStateBinding(post: Posts) {
        //나의 사진 모아보기일 경우 팔로우 버튼을 숨김 (post.user.id == me.id)
        val myUniqueId = userPref.getMyUniqueId()
        val followBtn = binding.followBtn
        followBtn.isVisible = myUniqueId != post.user?.id
        followBtn.isSelected =
            post.isFollow ?: false
        followBtn.text = if (followBtn.isSelected) "팔로잉" else "+ 팔로우"
        userId = post.user!!.id
    }

    private fun followBtnOnclick() {
        binding.followBtn.setOnClickListener {
            val followBtn = binding.followBtn
            if (userPref.loginCheck()) {
                //팔로우 create / delete
                viewModel.changeFollowingState(!followBtn.isSelected, userId)
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