package com.lf.fashion.ui.scrap

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import com.lf.fashion.R
import com.lf.fashion.TAG
import com.lf.fashion.databinding.ScrapFragmentBinding
import com.lf.fashion.ui.home.GridSpaceItemDecoration
import com.lf.fashion.ui.GridPhotoClickListener
import com.lf.fashion.ui.GridPostAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ScrapFragment : Fragment(), GridPhotoClickListener {
    private lateinit var binding: ScrapFragmentBinding
    private val viewModel: ScrapViewModel by hiltNavGraphViewModels(R.id.navigation_scrap)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ScrapFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding.scrapRv) {
            adapter = GridPostAdapter(3, this@ScrapFragment,null).apply {
                viewModel.postList.observe(viewLifecycleOwner) {
                    while (itemDecorationCount > 0) { // 기존 추가한 itemDecoration 을 모두 지워주지않으면 점점 쌓인다.
                        removeItemDecorationAt(0)
                    }
                    addItemDecoration(GridSpaceItemDecoration(3,6))
                    this.submitList(it)
                }
            }
        }
    }

    override fun gridPhotoClicked(postIndex:Int){
        Log.d(TAG, "ScrapFragment - gridPhotoClicked: grid 포토 클릭 $postIndex")
        // post list 에서 클릭한 포토의 포지션을 viewModel 에 저장
        viewModel.editClickedPostIndex(postIndex)
        findNavController().navigate(R.id.action_navigation_scrap_to_scrapVerticalFragment)
    }
}