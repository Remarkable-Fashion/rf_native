package com.lf.fashion.ui.home.frag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.lf.fashion.R
import com.lf.fashion.data.network.Resource
import com.lf.fashion.databinding.HomeBUserInfoFragmentBinding
import com.lf.fashion.ui.cancelBtnBackStack
import com.lf.fashion.ui.childChip
import com.lf.fashion.ui.home.UserInfoViewModel
import com.lf.fashion.ui.home.adapter.ClothesRvAdapter
import dagger.hilt.android.AndroidEntryPoint

/**
 * 메인홈 유저 정보보기 프래그먼트입니다.
 */
@AndroidEntryPoint
class UserInfoFragment : Fragment() {
    private lateinit var binding: HomeBUserInfoFragmentBinding
    private val viewModel: UserInfoViewModel by viewModels()

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

        cancelBtnBackStack(binding.cancelBtn)

        val postId = arguments?.get("postId") as Int

        binding.recommendBtn.setOnClickListener {
            findNavController().navigate(R.id.action_userInfoFragment_to_recommendFragment, bundleOf("postId" to postId))
        }

        viewModel.getUserInfoAndStyle(postId)
        viewModel.userInfo.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { resource ->
                when (resource) {
                    is Resource.Success -> {
                        val response = resource.value
                        binding.infoSpace.postInfo = response
                        binding.infoSpace.userInfo = response.user.profile
                        binding.profileSpace.profile = response.user
                        binding.clothesRv.apply {
                            adapter = ClothesRvAdapter().apply {
                                submitList(response.clothes)
                            }
                        }
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