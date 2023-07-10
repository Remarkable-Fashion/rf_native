package com.lf.fashion.ui.search

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.lf.fashion.databinding.SearchResultViewpagerBinding
import com.lf.fashion.ui.home.GridSpaceItemDecoration
import com.lf.fashion.ui.GridPhotoClickListener
import com.lf.fashion.ui.GridPostAdapter
import com.lf.fashion.ui.search.adapter.LookVerticalAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchResultFragment(resultCategory: String?) : Fragment(),
    GridPhotoClickListener {
    private lateinit var binding: SearchResultViewpagerBinding

    //TODO : 이부분 해결해야합니당
    constructor() : this("look") // 외부 메뉴 이동후 재진입할 경우 기본 생성자 필요!

    /**중요@ parentFragment 의 viewModel 데이터 변동 사항을 인지할 수 있도록 requireParentFragment()를 넣어줘야한다 (GRID 모드 조정 버튼이 PARENT FRAG 에 위치 )**/
    private val viewModel: SearchViewModel by viewModels({ requireParentFragment() })
    private val gridAdapter = GridPostAdapter(3, this, resultCategory)
    private val verticalAdapter = LookVerticalAdapter(resultCategory)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SearchResultViewpagerBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /** 메인 홈 post 구조와 동일하게, viewPager , staggerGrid RecyclerView 를 동시에 활용하고 visibility 로 노출을 조정
         * 특히 staggerGridAdapter 는 메인 홈과 동일하기 때문에 같은 어뎁터를 사용함 (GridPostAdapter)**/
        binding.gridRv.adapter = gridAdapter

        val searchTerm = arguments?.get("searchTerm").toString()

        viewModel.getSearchResult(searchTerm)
        viewModel.getItemSearchResult(searchTerm)

        with(binding.gridRv) {
            viewModel.postResponse.observe(viewLifecycleOwner) { response ->

                if (response.posts.isEmpty()) {
                    binding.arrayEmptyText.isVisible = true
                } else {
                    binding.arrayEmptyText.isVisible = false

                    layoutManager =
                        StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
                    gridAdapter.apply {
                        while (itemDecorationCount > 0) { // 기존 추가한 itemDecoration 을 모두 지워주지않으면 점점 쌓인다.
                            removeItemDecorationAt(0)
                        }
                        addItemDecoration(GridSpaceItemDecoration(3, 6))
                        submitList(response.posts)
                    }
                }
            }
        }
        with(binding.verticalViewpager) {
            viewModel.itemList.observe(viewLifecycleOwner) { response ->
                if (response.posts.isEmpty()) {
                    binding.arrayEmptyText.isVisible = true
                } else {
                    binding.arrayEmptyText.isVisible = false

                    adapter = verticalAdapter.apply {
                        submitList(response.posts)
                    }
                    getChildAt(0).overScrollMode =
                        RecyclerView.OVER_SCROLL_NEVER // 최상단,최하단 스크롤 이벤트 shadow 제거
                }
            }
        }

        viewModel.gridMode.observe(viewLifecycleOwner) { gridMode ->
            when (gridMode) {
                1 -> {
                    layoutVisibilityUpdate(false)
                }
                2 -> {
                    editGridSpanCount(2)
                    layoutVisibilityUpdate(true)
                }
                3 -> {
                    editGridSpanCount(3)
                    layoutVisibilityUpdate(true)
                }

            }
            gridAdapter.notifyDataSetChanged()
        }
    }

    private fun layoutVisibilityUpdate(default: Boolean) {
        binding.verticalViewpager.isVisible = !default
        binding.gridRv.isVisible = default
    }

    private fun editGridSpanCount(spanCount: Int) {
        with(binding.gridRv) {
            layoutManager = StaggeredGridLayoutManager(
                spanCount,
                StaggeredGridLayoutManager.VERTICAL
            )
            while (itemDecorationCount > 0) { // 기존 추가한 itemDecoration 을 모두 지워주지않으면 점점 쌓인다.
                removeItemDecorationAt(0)
            }
            gridAdapter.apply {
                addItemDecoration(GridSpaceItemDecoration(spanCount, 6))
                editSpanCountBtnClicked(spanCount)
            }
        }
    }

    override fun gridPhotoClicked(postIndex: Int) {
        //grid 포토 클릭시!!
    }

}
