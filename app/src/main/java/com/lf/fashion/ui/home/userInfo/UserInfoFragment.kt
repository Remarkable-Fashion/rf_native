package com.lf.fashion.ui.home.userInfo

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.lf.fashion.R
import com.lf.fashion.data.common.UserDataStorePref
import com.lf.fashion.data.network.Resource
import com.lf.fashion.databinding.HomeBUserInfoFragmentBinding
import com.lf.fashion.ui.cancelBtnBackStack
import com.lf.fashion.ui.childChip
import com.lf.fashion.ui.home.adapter.ClothesRvAdapter
import com.lf.fashion.ui.home.frag.PostBottomSheetFragment
import com.lf.fashion.ui.showRequireLoginDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlin.properties.Delegates

/**
 * 메인홈 유저 정보보기 프래그먼트입니다.
 */
@AndroidEntryPoint
class UserInfoFragment : Fragment(R.layout.home_b_user_info_fragment) {
    private lateinit var binding: HomeBUserInfoFragmentBinding
    private val viewModel: UserInfoViewModel by viewModels()
    private lateinit var userPref: UserDataStorePref
    private var userId by Delegates.notNull<Int>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //생성 후 다른 바텀 메뉴 이동시 다시 home menu 클릭시 selected 아이콘으로 변경 안되는 오류 해결하기위해 수동 메뉴 checked 코드 추가
        val bottomNavigationView =
            requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavBar)
        val homeMenu = bottomNavigationView.menu.findItem(R.id.navigation_home)
        homeMenu.isChecked = true

        binding = HomeBUserInfoFragmentBinding.bind(view)
        userPref = UserDataStorePref(requireContext().applicationContext)


        cancelBtnBackStack(binding.cancelBtn)

        val postId = arguments?.get("postId") as Int
        val myUniqueId = userPref.getMyUniqueId()

        binding.recommendBtn.setOnClickListener {
            findNavController().navigate(
                R.id.action_userInfoFragment_to_recommendFragment,
                bundleOf("postId" to postId)
            )
        }

        viewModel.getUserInfoAndStyle(postId)
        userInfoObserveAndBinding(myUniqueId)

        followBtnOnclick()
        // 팔로우 응답 success -> ui update
        updateFollowingState()

        profileKebabBtnOnClick()
    }

    private fun followBtnOnclick() {
        binding.profileSpace.followBtn.setOnClickListener {
            val followBtn = binding.profileSpace.followBtn
            if (userPref.loginCheck()) {
                //팔로우 create / delete
                viewModel.changeFollowingState(!followBtn.isSelected, userId)
            }else{
                showRequireLoginDialog(presentFragId = R.id.userInfoFragment)
            }
        }
    }

    private fun userInfoObserveAndBinding(me: Int?) {
        viewModel.userInfo.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { resource ->
                when (resource) {
                    is Resource.Success -> {
                        val response = resource.value
                        //데이터 바인딩
                        binding.infoSpace.postInfo = response
                        binding.infoSpace.userInfo = response.user.profile
                        binding.profileSpace.profile = response.user
                        binding.clothesRv.apply {
                            adapter = ClothesRvAdapter().apply {
                                val nullOrEmpty = response.clothes.isNullOrEmpty()
                                binding.arrayEmptyText.isVisible = nullOrEmpty
                                binding.clothesRv.isVisible = !nullOrEmpty

                                if(!nullOrEmpty) { submitList(response.clothes) }
                            }
                        }

                        //팔로우 관련
                        // 내 포스트인 경우 팔로우 버튼 숨기기
                        val followBtn = binding.profileSpace.followBtn
                        followBtn.isVisible = me != response.user.id
                        followBtn.isSelected =
                            response.isFollow ?: false
                        followBtn.text = if (followBtn.isSelected) "팔로잉" else "+ 팔로우"
                        userId = response.user.id

                        //스타일 칩
                        val styleChipGroup = binding.infoSpace.styleChipGroup
                        childChip(response.styles, styleChipGroup, "purple")
                    }

                    is Resource.Loading -> {

                    }

                    else -> {

                    }
                }

            }
        }
    }

    private fun updateFollowingState() {
        viewModel.followResponse.observe(viewLifecycleOwner) { resources ->
            if (resources is Resource.Success && resources.value.success) {
                val followBtn = binding.profileSpace.followBtn

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

    private fun profileKebabBtnOnClick() {
        binding.profileSpace.kebabBtn.setOnClickListener {
            val dialog = PostBottomSheetFragment(userId = userId)
            dialog.show(parentFragmentManager, "bottom_sheet")
        }
    }
}