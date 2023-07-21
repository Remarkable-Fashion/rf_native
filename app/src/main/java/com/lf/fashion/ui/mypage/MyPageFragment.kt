package com.lf.fashion.ui.mypage

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.lf.fashion.R
import com.lf.fashion.TAG
import com.lf.fashion.data.network.Resource
import com.lf.fashion.data.response.MyInfo
import com.lf.fashion.data.response.Posts
import com.lf.fashion.data.response.RandomPostResponse
import com.lf.fashion.databinding.MypageFragmentBinding
import com.lf.fashion.ui.home.GridSpaceItemDecoration
import com.lf.fashion.ui.GridPhotoClickListener
import com.lf.fashion.ui.GridPostAdapter
import com.lf.fashion.ui.OnScrollUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


@AndroidEntryPoint
class MyPageFragment : Fragment(), GridPhotoClickListener {
    private lateinit var binding: MypageFragmentBinding
    private val viewModel: MyPageViewModel by viewModels()
    private var postList = mutableListOf<Posts>()
    private lateinit var gridAdapter: GridPostAdapter
    private lateinit var recentResponse: RandomPostResponse
    private lateinit var onScrollListener: NestedScrollView.OnScrollChangeListener
    private lateinit var globalMyInfo : MyInfo
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        runBlocking {
            launch {
                viewModel.getSavedLoginToken()
            }
        }
        binding = MypageFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        gridAdapter = GridPostAdapter(3, this@MyPageFragment, null)

        //스크롤 리스너 설정
        onScrollListener = OnScrollUtils { loadMorePost() }
        binding.myNestedScrollView.setOnScrollChangeListener(onScrollListener)


        viewModel.savedLoginToken.observe(viewLifecycleOwner) {
            if (it.isNullOrEmpty()) {
                findNavController().navigate(R.id.action_navigation_mypage_to_loginFragment)
            } else {
                viewModel.getPostList()
                viewModel.getMyInfo()

                //내 정보 불러오기
                viewModel.myInfo.observe(viewLifecycleOwner) { myInfo ->
                    binding.userInfo = myInfo
                    globalMyInfo = myInfo
                }

                //내 게시물 불러오기
                viewModel.postResponse.observe(viewLifecycleOwner) { event ->
                    event.getContentIfNotHandled()?.let { resource ->
                        when (resource) {
                            is Resource.Success -> {
                                val response = resource.value
                                if (response.posts.isNotEmpty()) {
                                    binding.arrayEmptyText.visibility = View.GONE
                                    binding.gridRv.visibility = View.VISIBLE
                                    Log.d(TAG, "MyPageFragment - onViewCreated FIRST RESPONSE: $response")

                                    recentResponse = response
                                    postList.addAll(response.posts)

                                    with(binding.gridRv) {
                                        //grid layout
                                        adapter = gridAdapter.apply {

                                            while (itemDecorationCount > 0) { // 기존 추가한 itemDecoration 을 모두 지워주지않으면 점점 쌓인다.
                                                removeItemDecorationAt(0)
                                            }
                                            addItemDecoration(GridSpaceItemDecoration(3, 6))
                                            submitList(response.posts)
                                        }
                                    }
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
            }
        }

        binding.profileEditBtn.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_mypage_to_profileEditFragment,
                bundleOf("myInfo" to globalMyInfo))
        }
        //바텀 다이얼로그 show
        binding.settingBtn.setOnClickListener {
            val dialog = SettingBottomSheetFragment(viewModel)
            dialog.show(parentFragmentManager, "setting_bottom_sheet")
        }


    }

    private fun loadMorePost() {
        if (recentResponse.hasNext == true) {
            viewModel.getMorePostList(recentResponse.nextCursor!!)
            viewModel.morePost.observe(viewLifecycleOwner) { event ->
                event.getContentIfNotHandled()?.let { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            val more = resource.value
                            postList.addAll(more.posts)
                            recentResponse = more // new nextCursor , hasNext check 를 위해 값 재초기화

                            Log.d(TAG, "MyPageFragment - onScrolled LOAD MORE RECENT: $recentResponse")

                            gridAdapter.apply {
                                submitList(postList)
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
    }
    override fun onDestroyView() {
        super.onDestroyView()

        // Fragment가 소멸될 때 OnScrollChangeListener를 제거합니다.
        binding.myNestedScrollView.setOnScrollChangeListener(null as NestedScrollView.OnScrollChangeListener?)
    }
}
