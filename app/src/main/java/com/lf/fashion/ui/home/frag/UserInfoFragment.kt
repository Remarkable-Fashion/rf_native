package com.lf.fashion.ui.home.frag

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.lf.fashion.R
import com.lf.fashion.TAG
import com.lf.fashion.data.common.PreferenceManager
import com.lf.fashion.data.network.Resource
import com.lf.fashion.databinding.HomeBUserInfoFragmentBinding
import com.lf.fashion.ui.PrefCheckService
import com.lf.fashion.ui.cancelBtnBackStack
import com.lf.fashion.ui.childChip
import com.lf.fashion.ui.home.UserInfoViewModel
import com.lf.fashion.ui.home.adapter.ClothesRvAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlin.properties.Delegates

/**
 * 메인홈 유저 정보보기 프래그먼트입니다.
 */
@AndroidEntryPoint
class UserInfoFragment : Fragment() {
    private lateinit var binding: HomeBUserInfoFragmentBinding
    private val viewModel: UserInfoViewModel by viewModels()
    private lateinit var userPref: PreferenceManager
    private lateinit var prefCheckService: PrefCheckService
    private var userId by Delegates.notNull<Int>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = HomeBUserInfoFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userPref = PreferenceManager(requireContext().applicationContext)
        prefCheckService = PrefCheckService(userPref)


        cancelBtnBackStack(binding.cancelBtn)

        val postId = arguments?.get("postId") as Int

        binding.recommendBtn.setOnClickListener {
            findNavController().navigate(
                R.id.action_userInfoFragment_to_recommendFragment,
                bundleOf("postId" to postId)
            )
        }

        viewModel.getUserInfoAndStyle(postId)
        userInfoObserveAndBinding()
        binding.profileSpace.followBtn.setOnClickListener {
            if (prefCheckService.loginCheck()) {

                viewModel.changeFollowingState(!binding.profileSpace.followBtn.isSelected,userId)


            }
        }
    }

    private fun userInfoObserveAndBinding() {
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
                                submitList(response.clothes)
                            }
                        }
                        //팔로우 관련
                        binding.profileSpace.followBtn.isSelected =
                            response.isFollow ?: false
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
}