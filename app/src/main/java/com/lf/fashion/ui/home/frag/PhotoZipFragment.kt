package com.lf.fashion.ui.home.frag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.lf.fashion.databinding.HomeBPhotoZipFragmentBinding
import com.lf.fashion.ui.home.GridSpaceItemDecoration
import com.lf.fashion.ui.home.HomeViewModel
import com.lf.fashion.ui.GridPhotoClickListener
import com.lf.fashion.ui.GridPostAdapter
import dagger.hilt.android.AndroidEntryPoint

/**
 * 메인 홈에서 유저 클릭시 노출되는 특정 유저 사진 모아보기 프래그먼트입니다.
 */
@AndroidEntryPoint
class PhotoZipFragment : Fragment(), GridPhotoClickListener {
    lateinit var binding: HomeBPhotoZipFragmentBinding
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = HomeBPhotoZipFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding.gridRv) { //grid layout
            adapter = GridPostAdapter(3, this@PhotoZipFragment,null).apply {
                viewModel.response.observe(viewLifecycleOwner) { response ->
                    while (itemDecorationCount > 0) { // 기존 추가한 itemDecoration 을 모두 지워주지않으면 점점 쌓인다.
                        removeItemDecorationAt(0)
                    }
                    addItemDecoration(GridSpaceItemDecoration(3, 6))
                    submitList(response.posts)
                }
            }
        }
    }

    override fun gridPhotoClicked(postIndex:Int) {
        //grid 포토 클릭시!!
    }
}