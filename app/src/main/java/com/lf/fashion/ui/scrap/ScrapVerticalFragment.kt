package com.lf.fashion.ui.scrap

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.lf.fashion.R
import com.lf.fashion.databinding.ScrapVerticalFragmentBinding
import com.lf.fashion.ui.cancelBtnBackStack
import com.lf.fashion.ui.home.PhotoClickListener
import com.lf.fashion.ui.home.VerticalViewPagerClickListener
import com.lf.fashion.ui.home.adapter.DefaultPostAdapter
import com.lf.fashion.ui.home.frag.HomeBottomSheetFragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.recyclerview.widget.RecyclerView
import com.lf.fashion.MainNaviDirections
import com.lf.fashion.data.network.Resource
import com.lf.fashion.data.response.ImageUrl
import com.lf.fashion.data.response.Posts


class ScrapVerticalFragment : Fragment(),
    PhotoClickListener, VerticalViewPagerClickListener {
    private lateinit var binding: ScrapVerticalFragmentBinding
    private val viewModel: ScrapViewModel by hiltNavGraphViewModels(R.id.navigation_scrap) // hilt navi 함께 사용할때 viewModel 공유

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ScrapVerticalFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cancelBtnBackStack(binding.backBtn)



        viewModel.postResponse.observe(viewLifecycleOwner) { /*event ->
            event.getContentIfNotHandled()?.let {*/ resource ->
            when (resource) {
                is Resource.Success -> {
                    val response = resource.value

                    binding.verticalViewpager.apply {
                        adapter = DefaultPostAdapter(
                            this@ScrapVerticalFragment,
                            this@ScrapVerticalFragment
                        )
                        (adapter as? DefaultPostAdapter)?.apply {
                            submitList(response.posts)
                            //scrapFragment 에서 선택한 item 의 index 를 시작 index 로 지정 , animation false 처리
                            setCurrentItem(viewModel.startIndex.value ?: 0, false)
                        }
                        getChildAt(0).overScrollMode =
                            RecyclerView.OVER_SCROLL_NEVER // 최상단,최하단 스크롤 이벤트 shadow 제거
                    }
                }
                is Resource.Failure -> {

                }
                is Resource.Loading -> {

                }
            }
            // }
        }
    }


    override fun photoClicked(bool: Boolean, photo: List<ImageUrl>) {
        if (bool) {
            val action =
                MainNaviDirections.actionGlobalToPhotoDetailFragment(photo.toTypedArray())
            findNavController().navigate(action)
        }
    }

    override fun likeBtnClicked(likeState: Boolean, post: Posts) {

    }

    override fun shareBtnClicked() {
        val dialog = HomeBottomSheetFragment()
        dialog.show(parentFragmentManager, "bottom_sheet")

    }

    override fun photoZipBtnClicked() {
        findNavController().navigate(R.id.action_global_to_photoZipFragment)
    }

    override fun infoBtnClicked() {
        findNavController().navigate(R.id.action_global_to_userInfoFragment)
    }
}