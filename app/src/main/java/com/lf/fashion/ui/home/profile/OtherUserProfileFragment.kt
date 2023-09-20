package com.lf.fashion.ui.home.profile

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.lf.fashion.R
import com.lf.fashion.TAG
import com.lf.fashion.data.network.Resource
import com.lf.fashion.databinding.UserProfileFragmentBinding
import com.lf.fashion.ui.common.adapter.GridPhotoClickListener
import com.lf.fashion.ui.common.adapter.GridPostAdapter
import com.lf.fashion.ui.home.GridSpaceItemDecoration

class OtherUserProfileFragment : Fragment(R.layout.user_profile_fragment), GridPhotoClickListener
{
    private lateinit var binding : UserProfileFragmentBinding
    private val viewModel :OtherUserProfileViewModel by hiltNavGraphViewModels(R.id.otherUserProfileFragment)
    private val gridAdapter = GridPostAdapter(3,this@OtherUserProfileFragment , reduceViewWidth = true)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = UserProfileFragmentBinding.bind(view)

        val userId = arguments?.getInt("userId") ?: return
        viewModel.getProfileInfo(userId)
        viewModel.getPostByUserId(userId)

        viewModel.profileInfo.observe(viewLifecycleOwner){resources->
            if(resources is Resource.Success ){
                binding.userInfo = resources.value
            }
        }
        binding.gridRv.apply {
            layoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
            adapter = gridAdapter.apply {
                while (itemDecorationCount > 0) { // 기존 추가한 itemDecoration 을 모두 지워주지않으면 점점 쌓인다.
                    removeItemDecorationAt(0)
                }
                addItemDecoration(GridSpaceItemDecoration(3, 6))
            }
        }
        viewModel.postResponse.observe(viewLifecycleOwner){resource->
            if(resource is Resource.Success){
                val post = resource.value
                gridAdapter.submitList(post.posts)
            }
        }
    }

    override fun gridPhotoClicked(postIndex: Int) {
        Log.d(
            TAG, "MyPageFragment - gridPhotoClicked: 마이페이지 grid"
                    + "클릭된 인덱스 : ${postIndex}"
        );
        viewModel.editClickedPostIndex(postIndex)
        //,
        //            bundleOf("postList" to postList)
        findNavController().navigate(
            R.id.action_otherUserProfileFragment_to_otherUserVerticalPostFragment
        )
    }
}