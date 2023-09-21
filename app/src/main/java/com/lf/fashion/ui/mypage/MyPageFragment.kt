package com.lf.fashion.ui.mypage

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.lf.fashion.R
import com.lf.fashion.TAG
import com.lf.fashion.data.common.UserDataStorePref
import com.lf.fashion.data.model.MyInfo
import com.lf.fashion.data.network.Resource
import com.lf.fashion.databinding.MypageFragmentBinding
import com.lf.fashion.ui.globalFrag.adapter.GridPhotoClickListener
import com.lf.fashion.ui.globalFrag.adapter.GridPostAdapter
import com.lf.fashion.ui.common.OnScrollUtils
import com.lf.fashion.ui.home.GridSpaceItemDecoration
import com.lf.fashion.ui.common.mainBottomMenuListener
import com.lf.fashion.ui.mypage.setting.SettingBottomSheetFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


@AndroidEntryPoint
class MyPageFragment : Fragment(), GridPhotoClickListener {
    private lateinit var binding: MypageFragmentBinding
    private val viewModel: MyPageViewModel by hiltNavGraphViewModels(R.id.navigation_mypage)
    private lateinit var gridAdapter: GridPostAdapter
    private lateinit var onScrollListener: NestedScrollView.OnScrollChangeListener
    private lateinit var globalMyInfo: MyInfo
    private lateinit var userPref: UserDataStorePref

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        viewModel.savedLoginToken.observe(viewLifecycleOwner) {
            if (it.isNullOrEmpty()) {
                findNavController().navigate(R.id.action_navigation_mypage_to_loginFragment)
                return@observe
            }
        }
        binding = MypageFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userPref = UserDataStorePref(requireContext().applicationContext)

        gridAdapter = GridPostAdapter(3, this@MyPageFragment, reduceViewWidth = true)
        with(binding.gridRv){
            layoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
            adapter = gridAdapter.apply {
                while (itemDecorationCount > 0) { // 기존 추가한 itemDecoration 을 모두 지워주지않으면 점점 쌓인다.
                    removeItemDecorationAt(0)
                }
                addItemDecoration(GridSpaceItemDecoration(3, 6)) }
        }

        //스크롤 리스너 설정
        onScrollListener = OnScrollUtils { loadMorePost() }
        binding.myNestedScrollView.setOnScrollChangeListener(onScrollListener)

        //내 정보 불러오기
        viewModel.myInfo.observe(viewLifecycleOwner) { myInfo ->
            myInfo?.let {
                binding.userInfo = myInfo
                globalMyInfo = myInfo
                runBlocking {
                    launch {
                        userPref.saveMyId(myInfo.id)
                    }
                }

                //내 게시물 불러오기
                loadMyPost()
            }
        }

        binding.profileEditBtn.setOnClickListener {
            if (!::globalMyInfo.isInitialized) return@setOnClickListener

            findNavController().navigate(
                R.id.action_navigation_mypage_to_profileEditFragment,
                bundleOf("myInfo" to globalMyInfo)
            )
        }
        //바텀 다이얼로그 show
        binding.settingBtn.setOnClickListener {
            val dialog = SettingBottomSheetFragment(viewModel)
            dialog.show(parentFragmentManager, "setting_bottom_sheet")
        }

        navigateFollowDetailFrag()

        //vertical 뷰에서 포스트를 삭제한 경우 refresh 하는 코드!
        viewModel.havetoRefresh.observe(viewLifecycleOwner){ it->
            if(it) {
                viewModel.getPostList()
                viewModel.havetoRefresh.value = false
            }
        }
        binding.layoutSwipeRefreah.setOnRefreshListener {
            if(!viewModel.savedLoginToken.value.isNullOrEmpty()){
                viewModel.getPostList()
                viewModel.getMyInfo()
            }
            return@setOnRefreshListener
        }
    }

    private fun navigateFollowDetailFrag() {
        binding.followerCount.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_mypage_to_myPageFollowDetailFragment)
        }
        binding.followingCount.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_mypage_to_myPageFollowDetailFragment)
        }
    }

    private fun loadMyPost() {
        viewModel.postResponse.observe(viewLifecycleOwner) { resource ->
            binding.layoutSwipeRefreah.isRefreshing = false
            when (resource) {
                is Resource.Success -> {
                    val response = resource.value
                    if (response.posts.isNotEmpty()) {
                        binding.arrayEmptyText.visibility = View.GONE
                        binding.gridRv.visibility = View.VISIBLE
                        viewModel.recentResponse = response
                        viewModel.allPostList = response.posts.toMutableList()
                        gridAdapter.submitList(response.posts)
                    } else {
                        binding.arrayEmptyText.visibility = View.VISIBLE
                        binding.gridRv.visibility = View.GONE
                    }
                }

                is Resource.Loading -> {

                }

                else -> {

                }
            }
        }
    }

    private fun loadMorePost() {
        if (viewModel.recentResponse?.hasNext == true) {
            var recentResponse = viewModel.recentResponse!!
            viewModel.getMorePostList(recentResponse.nextCursor!!)
            viewModel.morePost.observe(viewLifecycleOwner) { event ->
                event.getContentIfNotHandled()?.let { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            val more = resource.value
                            viewModel.allPostList.addAll(more.posts)
                            viewModel.recentResponse = more // new nextCursor , hasNext check 를 위해 값 재초기화
                            gridAdapter.apply {
                                submitList(viewModel.allPostList)
                                notifyDataSetChanged()
                            }

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

    override fun gridPhotoClicked(postIndex: Int) {
        //grid 포토 클릭시!!
        Log.e(TAG, "gridPhotoClicked: GRID CLICKED $postIndex")

        //바텀 메뉴 중복 클릭시 첫 화면으로 돌아가도록 구현한 리스너가 에러가 나기 때문에 지워준다. (이후에 다시 달아줌)
        mainBottomMenuListener(false)

        viewModel.editClickedPostIndex(postIndex)
        findNavController().navigate(
            R.id.action_navigation_mypage_to_myPageVerticalFragment
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()

        // Fragment가 소멸될 때 OnScrollChangeListener를 제거합니다.
        binding.myNestedScrollView.setOnScrollChangeListener(null as NestedScrollView.OnScrollChangeListener?)
    }


}
