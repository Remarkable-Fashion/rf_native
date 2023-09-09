package com.lf.fashion.ui.mypage

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.lf.fashion.R
import com.lf.fashion.data.common.UserDataStorePref
import com.lf.fashion.data.network.Resource
import com.lf.fashion.data.model.Posts
import com.lf.fashion.data.model.UserInfo
import com.lf.fashion.databinding.MypagePhotoZipFragmentBinding
import com.lf.fashion.ui.home.GridSpaceItemDecoration
import com.lf.fashion.ui.GridPhotoClickListener
import com.lf.fashion.ui.GridPostAdapter
import com.lf.fashion.ui.home.frag.PostBottomSheetFragment
import com.lf.fashion.ui.home.photozip.PhotoZipViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.properties.Delegates

/**
 * 메인 홈에서 유저 클릭시 노출되는 특정 유저 사진 모아보기 프래그먼트입니다.
 */
@AndroidEntryPoint
class MyPhotoZipFragment : Fragment(R.layout.mypage_photo_zip_fragment), GridPhotoClickListener {
    lateinit var binding: MypagePhotoZipFragmentBinding
    private val viewModel: PhotoZipViewModel by hiltNavGraphViewModels(R.id.navigation_mypage)
    private var userId by Delegates.notNull<Int>()
    private lateinit var userInfoPost: Posts
    // private var postList = mutableListOf<Posts>()

    // private var followState by Delegates.notNull<Boolean>()

    private lateinit var userPref: UserDataStorePref


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //마이페이지 내부로 설정
        val bottomNavigationView =
            requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavBar)
        val myPage = bottomNavigationView.menu.findItem(R.id.navigation_mypage)
        myPage.isChecked = true

        binding = MypagePhotoZipFragmentBinding.bind(view)
        userPref = UserDataStorePref(requireContext().applicationContext)

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
            adapter = GridPostAdapter(3, this@MyPhotoZipFragment, null).apply {
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
    // 내 페이지 내부 팔로우 버튼 노출 x
    /*  binding.followBtn.setOnClickListener {
            val followBtn = binding.followBtn
            if (userPref.loginCheck()) {
                //팔로우 create / delete
                viewModel.changeFollowingState(!followBtn.isSelected, userId)
            }
        }*/
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
            R.id.action_myPhotoZipFragment_to_myPhotoZipVerticalFragment,
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