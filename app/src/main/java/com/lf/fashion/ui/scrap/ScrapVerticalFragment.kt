package com.lf.fashion.ui.scrap

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
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
import com.lf.fashion.TAG
import okhttp3.internal.notify
import okhttp3.internal.wait


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

        with(binding.verticalViewpager) {
            adapter = DefaultPostAdapter(
                this@ScrapVerticalFragment,
                this@ScrapVerticalFragment,
               // viewModel.startIndex.value ?: 0
            ).apply {
                viewModel.postList.observe(viewLifecycleOwner) {
                    submitList(it){
                        Log.d(TAG, "ScrapVerticalFragment - onViewCreated: ${viewModel.startIndex.value}!!!");
                        this@with.currentItem = viewModel.startIndex.value!!
                    }
                }
            }
        }
   /*     viewModel.startIndex.observe(viewLifecycleOwner) {
            Log.d(TAG, "ScrapVerticalFragment - onViewCreated: ${viewModel.startIndex.value}!!!");
            binding.verticalViewpager.currentItem = it
        }*/

    }


    override fun photoClicked(bool: Boolean, photo: List<Photo>) {
        if (bool) {
            val action =
                HomeFragmentDirections.actionNavigationHomeToPhotoDetailFragment(photo.toTypedArray())
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
        findNavController().navigate(R.id.action_navigation_home_to_photoZipFragment)
    }

    override fun infoBtnClicked(bool: Boolean) {
        findNavController().navigate(R.id.action_navigation_home_to_userInfoFragment)
    }
}