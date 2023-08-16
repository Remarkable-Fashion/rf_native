package com.lf.fashion.ui.mypage.followDetail

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import com.lf.fashion.R
import com.lf.fashion.data.network.Resource
import com.lf.fashion.databinding.MypageFollowViewpagerBinding
import com.lf.fashion.ui.mypage.MyPageViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FollowDetailItemFragment(private val tabName: String) :
    Fragment(R.layout.mypage_follow_viewpager) {
    private lateinit var binding: MypageFollowViewpagerBinding
    private val viewModel: MyPageViewModel by hiltNavGraphViewModels(R.id.navigation_mypage)
    private val userListAdapter = UserListAdapter()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = MypageFollowViewpagerBinding.bind(view)

        binding.profileRV.adapter = userListAdapter

        when (tabName) {
            "follower" -> {
                viewModel.getMyFollowers()
                viewModel.myFollowers.observe(viewLifecycleOwner) { resources ->
                    if (resources is Resource.Success) {
                        val followers = resources.value.followers
                        updateEmptyText(followers.isNullOrEmpty(), "팔로워가 없습니다.")
                        userListAdapter.submitList(followers)
                    }
                }

            }

            "following" -> {
                viewModel.getMyFollowings()
                viewModel.myFollowings.observe(viewLifecycleOwner) { resources ->
                    if (resources is Resource.Success) {
                        val followings = resources.value.followings
                        updateEmptyText(followings.isNullOrEmpty(), "팔로잉이 없습니다.")
                        userListAdapter.submitList(followings)
                    }
                }
            }

            "block" -> {
                viewModel.getMyBlockUsers()
                viewModel.myBlockUsers.observe(viewLifecycleOwner) { resources ->
                    if (resources is Resource.Success) {
                        val blockedUsers = resources.value.blockedUsers
                        updateEmptyText(blockedUsers.isNullOrEmpty(), "차단하신 유저가 없습니다.")
                        userListAdapter.submitList(blockedUsers)
                    }
                }
            }
        }
    }

    private fun updateEmptyText(isEmpty: Boolean, text: String) {
        if (isEmpty) {
            binding.arrayEmptyText.text = text
            binding.arrayEmptyText.isVisible = true
        } else {
            binding.arrayEmptyText.isVisible = false
        }
    }
}