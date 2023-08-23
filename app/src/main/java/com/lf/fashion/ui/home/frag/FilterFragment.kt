package com.lf.fashion.ui.home.frag

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.lf.fashion.R
import com.lf.fashion.databinding.HomeBPhotoFilterFragmentBinding
import com.lf.fashion.ui.addUnitTextListener
import com.lf.fashion.ui.cancelBtnBackStack
import com.lf.fashion.ui.childChip
import dagger.hilt.android.AndroidEntryPoint

/**
 * 홈 메인 상단의 필터 아이콘을 클릭시 노출되는 프래그먼트입니다.
 */
@AndroidEntryPoint
class FilterFragment : Fragment(R.layout.home_b_photo_filter_fragment) {
    private lateinit var binding: HomeBPhotoFilterFragmentBinding
    private val viewModel: FilterViewModel by viewModels()
    private val chipStyle = "default"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = HomeBPhotoFilterFragmentBinding.bind(view)

        arguments?.get("searchResult")?.let {
            if (it as Boolean) {
                //searchResult 일 경우 스피너 visible
            }
        }
        binding.filterSpace.genderManBtn.setOnClickListener {
            it.isSelected = !it.isSelected
        }
        binding.filterSpace.genderWomanBtn.setOnClickListener {
            it.isSelected = !it.isSelected
        }


        chipSetting()

        cancelBtnBackStack(binding.cancelBtn)

        editTextListenerSetting()
    }

    private fun editTextListenerSetting() {
        addUnitTextListener(binding.filterSpace.heightValue, height = true)
        addUnitTextListener(binding.filterSpace.weightValue, height = false)
    }

    private fun chipSetting() {
        viewModel.tpoChipList.observe(viewLifecycleOwner) {
            it?.let {
                val tpoChipGroup = binding.filterSpace.filterInclude.tpoChipGroup
                childChip(it, tpoChipGroup, chipStyle)
            }
        }
        viewModel.seasonChipList.observe(viewLifecycleOwner) {
            it?.let {
                val seasonChipGroup = binding.filterSpace.filterInclude.seasonChipGroup
                childChip(it, seasonChipGroup, chipStyle)
            }
        }
        viewModel.styleChipList.observe(viewLifecycleOwner) {
            it?.let {
                val styleChipGroup = binding.filterSpace.filterInclude.styleChipGroup
                childChip(it, styleChipGroup, chipStyle)
            }
        }
    }
}