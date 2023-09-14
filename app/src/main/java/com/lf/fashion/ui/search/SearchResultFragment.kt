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
import com.lf.fashion.R
import com.lf.fashion.TAG
import com.lf.fashion.data.common.SearchItemFilterDataStore
import com.lf.fashion.data.common.SearchLookFilterDataStore
import com.lf.fashion.data.network.Resource
import com.lf.fashion.databinding.SearchResultViewpagerBinding
import com.lf.fashion.ui.GridPhotoClickListener
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

    //TODO : 이부분 해결해야합니당
    constructor() : this("back") // 외부 메뉴 이동후 재진입할 경우 기본 생성자 필요!
    private val viewModel: SearchViewModel by hiltNavGraphViewModels(R.id.navigation_search)
    private val itemGridAdapter = ItemGridAdapter(3, this)
    private val lookPostGridAdapter = LookPostGridAdapter(3, this)
    private lateinit var lookFilterDataStore: SearchLookFilterDataStore
    private lateinit var itemFilterDataStore: SearchItemFilterDataStore
    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = SearchResultViewpagerBinding.bind(view)
        lookFilterDataStore = SearchLookFilterDataStore(requireContext().applicationContext)
        itemFilterDataStore = SearchItemFilterDataStore(requireContext().applicationContext)

        val searchTerm = viewModel.savedSearchTerm
        Log.d(TAG, "SearchResultFragment - onViewCreated: ${viewModel.savedSearchTerm}")

        when (resultCategory) {
            "look" -> {
                requestLookSearch(searchTerm)
              //  viewModel.getSearchResult(searchTerm)
                lookResultUiBinding()
            }

            "item" -> { // item
                requestItemSearch(searchTerm)
                //viewModel.getItemSearchResult(searchTerm)
                itemResultUiBinding()
            }
            else->{
                findNavController().navigate(R.id.action_global_to_searchFragment)
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

    }
    private fun requestLookSearch(searchTerm : String){
        CoroutineScope(Dispatchers.IO).launch{
            with(lookFilterDataStore){
                val tpo = tpoId.first()?.split(",")?.map { it.toInt() }
                val season = seasonId.first()?.split(",")?.map { it.toInt() }
                val style = styleId.first()?.split(",")?.map { it.toInt() }
                val gender = lookGender.first()
                val height = height.first()
                val weight = weight.first()
                withContext(Dispatchers.Main) {
                    viewModel.getSearchResult(searchTerm, gender, height, weight, tpo, season, style)
                }
            }
        }
    }
    private fun requestItemSearch(searchTerm: String){
        CoroutineScope(Dispatchers.IO).launch {
            with(itemFilterDataStore){
                val gender = itemGender.first()
                val minPrice = minPrice.first()
                val maxPrice = maxPrice.first()
                val color = color.first()?.split(",")
                withContext(Dispatchers.Main){
                    viewModel.getItemSearchResult(searchTerm,gender,minPrice,maxPrice, color)
                }
            }
        }
    }
    private fun itemResultUiBinding() {

        viewModel.itemList.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    val response = resource.value
                    if (response.clothes.isNullOrEmpty()) {
                        binding.arrayEmptyText.isVisible = true
                    } else {
                        binding.arrayEmptyText.isVisible = false
                        with(binding.verticalViewpager) {

                            adapter = ItemVerticalAdapter().apply {
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
                            adapter = LookVerticalAdapter().apply {
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
            setCurrentItem(postIndex,false)
        }
    }

}
