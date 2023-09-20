package com.lf.fashion.ui.search

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.lf.fashion.R
import com.lf.fashion.TAG
import com.lf.fashion.data.common.SearchItemFilterDataStore
import com.lf.fashion.data.common.SearchLookFilterDataStore
import com.lf.fashion.data.network.Resource
import com.lf.fashion.databinding.SearchResultViewpagerBinding
import com.lf.fashion.ui.common.adapter.GridPhotoClickListener
import com.lf.fashion.ui.home.GridSpaceItemDecoration
import com.lf.fashion.ui.search.adapter.ItemGridAdapter
import com.lf.fashion.ui.search.adapter.ItemVerticalAdapter
import com.lf.fashion.ui.search.adapter.LookPostGridAdapter
import com.lf.fashion.ui.search.adapter.LookVerticalAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class SearchResultFragment(private val resultCategory: String) :
    Fragment(R.layout.search_result_viewpager), GridPhotoClickListener {
    private lateinit var binding: SearchResultViewpagerBinding

    constructor() : this("back") // 외부 메뉴 이동후 재진입할 경우 기본 생성자 필요!

    private val viewModel: SearchViewModel by hiltNavGraphViewModels(R.id.navigation_search)
    private val itemGridAdapter = ItemGridAdapter(3, this)
    private val lookPostGridAdapter = LookPostGridAdapter(3, this)
    private lateinit var lookFilterDataStore: SearchLookFilterDataStore
    private lateinit var itemFilterDataStore: SearchItemFilterDataStore
    private val itemVerticalAdapter = ItemVerticalAdapter()
    private val lookVerticalAdapter = LookVerticalAdapter()
    private var nextCursor = listOf<Long>()
    private var hasNext = false
    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = SearchResultViewpagerBinding.bind(view)
        lookFilterDataStore = SearchLookFilterDataStore(requireContext().applicationContext)
        itemFilterDataStore = SearchItemFilterDataStore(requireContext().applicationContext)

        Log.d(TAG, "SearchResultFragment - onViewCreated: ${viewModel.savedSearchTerm}")

        when (resultCategory) {
            "look" -> {
                lookResultUiBinding()
            }

            "item" -> {
                itemResultUiBinding()
            }

            else -> {
                findNavController().navigate(R.id.navigation_search)
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
            if (resultCategory == "look") lookPostGridAdapter.notifyDataSetChanged() else itemGridAdapter.notifyDataSetChanged()
        }

        loadMoreSearchResult()
    }
    private fun loadMoreSearchResult(){
        binding.verticalViewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                val totalItemCount = binding.verticalViewpager.adapter?.itemCount ?: 0
                if (position == totalItemCount - 1&&hasNext) {
                    requestSearch()                }
            }
        })

        binding.gridRv.addOnScrollListener(object :RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val staggeredGridLayoutManager =
                    binding.gridRv.layoutManager as StaggeredGridLayoutManager
                val lastVisibleItems =
                    staggeredGridLayoutManager.findLastCompletelyVisibleItemPositions(null)
                val totalItemCount = recyclerView.adapter?.itemCount ?: 0
                val lastVisibleItem = lastVisibleItems.maxOrNull() ?: -1

                if (lastVisibleItem == totalItemCount - 1&&hasNext) {
                    // 마지막 아이템이 보이는 경우 처리할 내용을 여기에 추가
                    requestSearch()
                }
            }
        })
        viewModel.loadMoreItem.observe(viewLifecycleOwner){ resource->
            when(resource){
                is Resource.Success ->{
                    val morePost = resource.value
                    hasNext = morePost.hasNext
                    morePost.nextCursor?.let {
                        nextCursor = it
                    }
                    val currentList = itemGridAdapter.currentList.toMutableList()
                    Log.e(TAG, "observeLoadMorePost: currrent : ${currentList.size} , more : ${morePost.clothes}")
                    currentList.addAll(morePost.clothes!!)
                    Log.e(TAG, "observeLoadMorePost: 합 : $morePost")

                    itemGridAdapter.apply {
                        submitList(currentList)
                        notifyDataSetChanged()
                    }
                    itemVerticalAdapter.apply {
                        submitList(currentList)
                        notifyDataSetChanged()
                    }
                }
                else ->{

                }
            }
        }
        viewModel.loadMoreLook.observe(viewLifecycleOwner){ resource->
            when(resource){
                is Resource.Success ->{
                    val morePost = resource.value
                    hasNext = morePost.hasNext
                    morePost.nextCursor?.let {
                        nextCursor = it
                    }
                    val currentList = lookPostGridAdapter.currentList.toMutableList()
                    Log.e(TAG, "observeLoadMorePost: currrent : ${currentList.size} , more : ${morePost.posts}")
                    currentList.addAll(morePost.posts!!)
                    Log.e(TAG, "observeLoadMorePost: 합 : $morePost")

                    lookPostGridAdapter.apply {
                        submitList(currentList)
                        notifyDataSetChanged()
                    }
                    lookPostGridAdapter.apply {
                        submitList(currentList)
                        notifyDataSetChanged()
                    }
                }
                else ->{

                }
            }
        }
        //todo loadMoreItem observe 테스트 필요
        viewModel.loadMoreItem.observe(viewLifecycleOwner){ resource->
            when(resource){
                is Resource.Success ->{
                    val moreItem = resource.value
                    hasNext = moreItem.hasNext
                    moreItem.nextCursor?.let {
                        nextCursor = it
                    }
                    val currentList = itemGridAdapter.currentList.toMutableList()
                    Log.e(TAG, "observeLoadMorePost: currrent : ${currentList.size} , more : ${moreItem.clothes}")
                    currentList.addAll(moreItem.clothes!!)
                    Log.e(TAG, "observeLoadMorePost: 합 : $moreItem")

                    itemGridAdapter.apply {
                        submitList(currentList)
                        notifyDataSetChanged()
                    }
                    itemVerticalAdapter.apply {
                        submitList(currentList)
                        notifyDataSetChanged()
                    }
                }
                else ->{

                }
            }
        }
    }
    private fun requestSearch() {
        when(resultCategory){
            "look"->{
                CoroutineScope(Dispatchers.IO).launch {
                    with(lookFilterDataStore) {
                        val tpo = tpoId.first()?.split(",")?.mapNotNull { it.toIntOrNull() }
                        val season = seasonId.first()?.split(",")?.mapNotNull { it.toIntOrNull() }
                        val style = styleId.first()?.split(",")?.mapNotNull { it.toIntOrNull() }
                        val gender = lookGender.first()
                        val height = height.first()
                        val weight = weight.first()
                        val orderBy = SearchFragment.orderByParamMap[viewModel.selectedOrderBy.value] ?: "best"

                        withContext(Dispatchers.Main) {
                            viewModel.getSearchResult(
                                true,
                                viewModel.savedSearchTerm,
                                gender,
                                height,
                                weight,
                                tpo,
                                season,
                                style,
                                orderBy,
                                nextCursor
                            )
                        }
                    }
                }
            }
            "item" ->{
                CoroutineScope(Dispatchers.IO).launch {
                    with(itemFilterDataStore) {
                        val gender = itemGender.first()
                        val minPrice = minPrice.first()
                        val maxPrice = maxPrice.first()
                        val color = color.first()?.split(",")
                        val orderBy = SearchFragment.orderByParamMap[viewModel.selectedOrderBy.value] ?: "best"

                        withContext(Dispatchers.Main) {
                            viewModel.getItemSearchResult(
                                true,
                                viewModel.savedSearchTerm,
                                gender,
                                minPrice,
                                maxPrice,
                                color,
                                orderBy,
                                nextCursor
                            )
                        }
                    }
                }
            }
        }

    }

    private fun itemResultUiBinding() {

        viewModel.itemList.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    val response = resource.value
                    hasNext = response.hasNext
                    response.nextCursor?.let {
                        nextCursor = it
                    }
                    if (response.clothes.isNullOrEmpty()) {
                        binding.arrayEmptyText.isVisible = true
                    } else {
                        binding.arrayEmptyText.isVisible = false
                        with(binding.verticalViewpager) {

                            adapter = itemVerticalAdapter.apply {
                                submitList(response.clothes)
                            }
                            getChildAt(0).overScrollMode =
                                RecyclerView.OVER_SCROLL_NEVER // 최상단,최하단 스크롤 이벤트 shadow 제거
                        }
                        with(binding.gridRv) {
                            layoutManager =
                                StaggeredGridLayoutManager(
                                    3,
                                    StaggeredGridLayoutManager.VERTICAL
                                )
                            adapter = itemGridAdapter.apply {

                                while (itemDecorationCount > 0) { // 기존 추가한 itemDecoration 을 모두 지워주지않으면 점점 쌓인다.
                                    removeItemDecorationAt(0)
                                }
                                addItemDecoration(GridSpaceItemDecoration(3, 6))
                                submitList(response.clothes)
                            }
                        }


                    }
                }

                is Resource.Loading -> {

                }

                else -> {

                }
            }

        }
    }

    private fun lookResultUiBinding() {
        viewModel.lookList.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    val response = resource.value
                    hasNext = response.hasNext
                    response.nextCursor?.let {
                        nextCursor = it
                    }
                    Log.e(TAG, "lookResultUiBinding: $response")
                    if (response.posts.isNullOrEmpty()) {
                        binding.arrayEmptyText.isVisible = true
                    } else {
                        binding.arrayEmptyText.isVisible = false
                        binding.gridRv.apply {
                            layoutManager =
                                StaggeredGridLayoutManager(
                                    3,
                                    StaggeredGridLayoutManager.VERTICAL
                                )
                            adapter = lookPostGridAdapter.apply {

                                while (itemDecorationCount > 0) { // 기존 추가한 itemDecoration 을 모두 지워주지않으면 점점 쌓인다.
                                    removeItemDecorationAt(0)
                                }
                                addItemDecoration(GridSpaceItemDecoration(3, 6))
                                submitList(response.posts)
                            }
                        }
                        binding.verticalViewpager.apply {
                            adapter = lookVerticalAdapter.apply {
                                submitList(response.posts)
                            }
                            getChildAt(0).overScrollMode =
                                RecyclerView.OVER_SCROLL_NEVER // 최상단,최하단 스크롤 이벤트 shadow 제거
                        }

                    }
                }

                is Resource.Loading -> {

                }

                else -> {

                }
            }

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

            if (resultCategory == "look") {
                lookPostGridAdapter.apply {
                    addItemDecoration(GridSpaceItemDecoration(spanCount, 6))
                    editSpanCountBtnClicked(spanCount)
                }
            } else {
                itemGridAdapter.apply {
                    addItemDecoration(GridSpaceItemDecoration(spanCount, 6))
                    editSpanCountBtnClicked(spanCount)
                }
            }
        }
    }

    override fun gridPhotoClicked(postIndex: Int) {
        //grid 포토 클릭시!!
        viewModel.setGridMode(1)
        layoutVisibilityUpdate(false)
        binding.verticalViewpager.apply {
            Log.e(TAG, "gridPhotoClicked: ok")
            setCurrentItem(postIndex, false)
        }
    }

}
