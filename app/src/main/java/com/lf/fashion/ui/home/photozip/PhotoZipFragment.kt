package com.lf.fashion.ui.home.photozip

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.lf.fashion.R
import com.lf.fashion.data.common.PreferenceManager
import com.lf.fashion.data.network.Resource
import com.lf.fashion.data.response.Posts
import com.lf.fashion.databinding.HomeBPhotoZipFragmentBinding
import com.lf.fashion.ui.home.GridSpaceItemDecoration
import com.lf.fashion.ui.home.HomeViewModel
import com.lf.fashion.ui.GridPhotoClickListener
import com.lf.fashion.ui.GridPostAdapter
import com.lf.fashion.ui.PrefCheckService
import dagger.hilt.android.AndroidEntryPoint
import kotlin.properties.Delegates

/**
 * 메인 홈에서 유저 클릭시 노출되는 특정 유저 사진 모아보기 프래그먼트입니다.
 */
@AndroidEntryPoint
class PhotoZipFragment : Fragment(R.layout.home_b_photo_zip_fragment), GridPhotoClickListener {
    lateinit var binding: HomeBPhotoZipFragmentBinding
    private val viewModel: PhotoZipViewModel by viewModels()
    private var userId by Delegates.notNull<Int>()
   // private var followState by Delegates.notNull<Boolean>()

    private lateinit var userPref: PreferenceManager
    private lateinit var prefCheckService: PrefCheckService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //생성 후 다른 바텀 메뉴 이동시 다시 home menu 클릭시 selected 아이콘으로 변경 안되는 오류 해결하기위해 수동 메뉴 checked 코드 추가
        val bottomNavigationView =
            requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavBar)
        val homeMenu = bottomNavigationView.menu.findItem(R.id.navigation_home)
        homeMenu.isChecked = true
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = HomeBPhotoZipFragmentBinding.bind(view)
        userPref = PreferenceManager(requireContext().applicationContext)
        prefCheckService = PrefCheckService(userPref)


        val post = arguments?.get("post") as Posts
        binding.userInfo = post.user

        followStateBinding(post)
        followBtnOnclick()
        // 팔로우 응답 success -> ui update
        updateFollowingState()
        viewModel.getUserInfoAndStyle(post.user!!.id)

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


    }
    private fun followStateBinding(post: Posts){
        //나의 사진 모아보기일 경우 팔로우 버튼을 숨김 (post.user.id == me.id)
        val myUniqueId = prefCheckService.getMyUniqueId()
        val followBtn = binding.followBtn
        followBtn.isVisible = myUniqueId != post.user?.id
        followBtn.isSelected =
            post.isFollow ?: false
        followBtn.text = if(followBtn.isSelected) "팔로잉" else "+ 팔로우"
        userId = post.user!!.id
    }
    private fun followBtnOnclick() {
        binding.followBtn.setOnClickListener {
            val followBtn = binding.followBtn
            if (prefCheckService.loginCheck()) {
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

                if(followBtn.isSelected){
                    followBtn.text = "팔로잉"
                }else{
                    followBtn.text = "+ 팔로우"
                }
            }
        }
    }
    override fun gridPhotoClicked(postIndex: Int) {
        //grid 포토 클릭시!!
    }
}