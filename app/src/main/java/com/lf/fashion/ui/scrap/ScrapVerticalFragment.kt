package com.lf.fashion.ui.scrap

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.lf.fashion.R
import com.lf.fashion.data.response.Photo
import com.lf.fashion.databinding.ScrapVerticalFragmentBinding
import com.lf.fashion.ui.cancelBtnBackStack
import com.lf.fashion.ui.home.PhotoClickListener
import com.lf.fashion.ui.home.VerticalViewPagerClickListener
import com.lf.fashion.ui.home.adapter.DefaultPostAdapter
import com.lf.fashion.ui.home.frag.HomeBottomSheetFragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import com.lf.fashion.MainNaviDirections


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

        binding.verticalViewpager.apply {
            adapter = DefaultPostAdapter(
                this@ScrapVerticalFragment,
                this@ScrapVerticalFragment
            )

            viewModel.postList.observe(viewLifecycleOwner) { posts ->
                (adapter as? DefaultPostAdapter)?.apply {
                    submitList(posts)
                    //scrapFragment 에서 선택한 item 의 index 를 시작 index 로 지정 , animation false 처리
                    setCurrentItem(viewModel.startIndex.value?:0,false)
                }
            }
        }
    }


    override fun photoClicked(bool: Boolean, photo: List<Photo>) {
        if (bool) {
            val action =
                MainNaviDirections.actionGlobalToPhotoDetailFragment(photo.toTypedArray())
            findNavController().navigate(action)
        }
    }

    override fun shareBtnClicked(bool: Boolean) {
        if (bool) {
            val dialog = HomeBottomSheetFragment()
            dialog.show(parentFragmentManager, "bottom_sheet")
        }
    }

    override fun photoZipBtnClicked(bool: Boolean) {
        findNavController().navigate(R.id.action_global_to_photoZipFragment)
    }

    override fun infoBtnClicked(bool: Boolean) {
        findNavController().navigate(R.id.action_global_to_userInfoFragment)
    }
}