package com.lf.fashion.ui.scrap

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import com.lf.fashion.R
import com.lf.fashion.TAG
import com.lf.fashion.data.common.PreferenceManager
import com.lf.fashion.data.network.Resource
import com.lf.fashion.data.model.Posts
import com.lf.fashion.data.model.RandomPostResponse
import com.lf.fashion.databinding.ScrapFragmentBinding
import com.lf.fashion.ui.*
import com.lf.fashion.ui.home.GridSpaceItemDecoration
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ScrapFragment : Fragment(R.layout.scrap_fragment), GridPhotoClickListener {
    private lateinit var binding: ScrapFragmentBinding
    private val viewModel: ScrapViewModel by hiltNavGraphViewModels(R.id.navigation_scrap)
    private var postList = mutableListOf<Posts>()
    private lateinit var gridPostAdapter: GridPostAdapter
    private lateinit var recentResponse: RandomPostResponse
    private lateinit var onScrollListener: NestedScrollView.OnScrollChangeListener
    private lateinit var userPref: PreferenceManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = ScrapFragmentBinding.bind(view)
        userPref = PreferenceManager(requireContext())

        if (userPref.loginCheck()) {
            gridPostAdapter = GridPostAdapter(3, this@ScrapFragment, scrapPage = true)

            //스크롤 리스너 설정
            onScrollListener = OnScrollUtils { loadMorePost() }
            binding.myNestedScrollView.setOnScrollChangeListener(onScrollListener)

            viewModel.getPostList()
            viewModel.postResponse.observe(viewLifecycleOwner) { resources ->
                binding.layoutSwipeRefreah.isRefreshing = false

                when (resources) {
                    is Resource.Success -> {
                        val response = resources.value
                        Log.d(TAG, "ScrapFragment - onViewCreated RESPONSE: $response");
                        if (response.posts.isNotEmpty()) {
                            binding.scrapRv.visibility = View.VISIBLE
                            binding.arrayEmptyText.visibility = View.GONE

                            postList.addAll(response.posts)
                            recentResponse = response

                            with(binding.scrapRv) {
                                adapter = gridPostAdapter.apply {
                                    while (itemDecorationCount > 0) { // 기존 추가한 itemDecoration 을 모두 지워주지않으면 점점 쌓인다.
                                        removeItemDecorationAt(0)
                                    }
                                    addItemDecoration(GridSpaceItemDecoration(3, 6))
                                    this.submitList(response.posts)
                                }
                            }
                        } else {
                            binding.scrapRv.visibility = View.GONE
                            binding.arrayEmptyText.visibility = View.VISIBLE
                        }
                    }

                    is Resource.Failure -> {
                        if (resources.errorCode == 401) {
                            showRequireLoginDialog()
                        } else {
                            Log.e(TAG, "onViewCreated postList response Error: $resources ")
                        }

                    }

                    is Resource.Loading -> {

                    }
                }
            }
        } else {
            showRequireLoginDialog()

        }

        binding.layoutSwipeRefreah.setOnRefreshListener {
            viewModel.getPostList()
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
                            recentResponse = more

                            gridPostAdapter.apply {
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
        Log.d(TAG, "ScrapFragment - gridPhotoClicked: grid 포토 클릭 $postIndex")
        // post list 에서 클릭한 포토의 포지션을 viewModel 에 저장
        viewModel.editClickedPostIndex(postIndex)
        findNavController().navigate(R.id.action_navigation_scrap_to_scrapVerticalFragment)
    }
}