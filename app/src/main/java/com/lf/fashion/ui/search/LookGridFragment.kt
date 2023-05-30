package com.lf.fashion.ui.search

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.lf.fashion.TAG
import com.lf.fashion.databinding.SearchLookGridFragmentBinding
import com.lf.fashion.ui.home.GridSpaceItemDecoration
import com.lf.fashion.ui.home.adapter.GridPostAdapter
import com.lf.fashion.ui.search.adapter.LookVerticalAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LookGridFragment : Fragment(){
    private lateinit var binding: SearchLookGridFragmentBinding
    private val viewModel : SearchViewModel by viewModels({requireParentFragment()}) //중요@
    private val gridAdapter = GridPostAdapter()
    private val verticalAdapter = LookVerticalAdapter()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SearchLookGridFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.gridRv.adapter = gridAdapter
        viewModel.postList.observe(viewLifecycleOwner) { response ->

            with(binding.gridRv) {
                layoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
                Log.d(TAG, "LookGridFragment - onViewCreated: $response");
                gridAdapter.apply {
                    addItemDecoration(GridSpaceItemDecoration(3, 6))
                    submitList(response)
                }
            }
            with(binding.verticalViewpager){
                adapter = verticalAdapter.apply {
                    submitList(response)
                }
            }
        }

        viewModel.gridMode.observe(viewLifecycleOwner){ gridMode ->
            Log.d(TAG, "GRID !!!! : $gridMode");
            when(gridMode){
                1->{

                    layoutVisibilityUpdate(false)
                    //editGridSpanCount(1)
                }
                2 ->{
                    editGridSpanCount(2)
                    layoutVisibilityUpdate(true)
                }
                3 ->{
                    editGridSpanCount(3)
                    layoutVisibilityUpdate(true)
                }

            }
            gridAdapter.notifyDataSetChanged()
        }
    }
    private fun layoutVisibilityUpdate(default : Boolean){
        binding.verticalViewpager.isVisible = !default
        binding.gridRv.isVisible = default
    }
    @SuppressLint("NotifyDataSetChanged")
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
    private fun editLayoutManager(){
        with(binding.gridRv){
            layoutManager = LinearLayoutManager(requireContext())

            gridAdapter.apply {
                editSpanCountBtnClicked(1)
            }

        }

    }

}

/*
*
* //상단 바의 2,3장씩 보기 버튼 클릭
            binding.appBarPhotoGridModeBtn -> {
                when (binding.appBarPhotoGridModeBtn.text) {
                    "1" -> {
                        binding.appBarPhotoGridModeBtn.text = "2"
                        photoLayoutVisibilityMode(false) // grid visibility
                        editGridSpanCount(2)
                    }
                    "2" -> {
                        binding.appBarPhotoGridModeBtn.text = "3"
                        photoLayoutVisibilityMode(false) // grid visibility
                        editGridSpanCount(3)
                    }
                    "3" -> {
                        binding.appBarPhotoGridModeBtn.text = "1"
                        photoLayoutVisibilityMode(true) // default visibility
                    }
                }
            }
* */