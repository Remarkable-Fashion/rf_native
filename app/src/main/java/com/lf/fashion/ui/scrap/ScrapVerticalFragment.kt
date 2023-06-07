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
import com.lf.fashion.ui.home.frag.HomeFragmentDirections
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.viewpager2.widget.ViewPager2
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
                (adapter as? DefaultPostAdapter)?.submitList(posts)
            }

            //Scrap Grid 이미지를 클릭했을 때 해당 이미지에 포스트 position 을 맞추는 로직
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    viewModel.startIndex.value = position
                }
            })

            viewModel.startIndex.value?.let { startPosition ->
                post {
                    currentItem = startPosition
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