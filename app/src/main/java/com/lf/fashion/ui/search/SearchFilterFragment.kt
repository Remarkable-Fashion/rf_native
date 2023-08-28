package com.lf.fashion.ui.search

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.lf.fashion.R
import com.lf.fashion.TAG
import com.lf.fashion.databinding.HomeBPhotoFilterFragmentBinding
import com.lf.fashion.ui.addUnitTextListener
import com.lf.fashion.ui.cancelBtnBackStack
import com.lf.fashion.ui.childChip
import com.lf.fashion.ui.home.frag.FilterViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * 홈 메인 상단의 필터 아이콘을 클릭시 노출되는 프래그먼트입니다.
 */
@AndroidEntryPoint
class SearchFilterFragment : Fragment(R.layout.search_filter_fragment),View.OnClickListener {
    private lateinit var binding: HomeBPhotoFilterFragmentBinding
    private val viewModel: FilterViewModel by viewModels()
    private val chipStyle = "default"

    override fun onResume() {
        viewModel.selectedGender?.let {
            if (it == "Male") { binding.filterSpace.genderManBtn.isSelected = true }
            else{ binding.filterSpace.genderWomanBtn.isSelected = true }
        }
        super.onResume()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = HomeBPhotoFilterFragmentBinding.bind(view)
        //생성 후 다른 바텀 메뉴 이동시 다시 home menu 클릭시 selected 아이콘으로 변경 안되는 오류 해결하기위해 수동 메뉴 checked 코드 추가
        val bottomNavigationView =
            requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavBar)
        val homeMenu = bottomNavigationView.menu.findItem(R.id.navigation_search)
        homeMenu.isChecked = true

        arguments?.get("searchResult")?.let {
            if (it as Boolean) {
                //searchResult 일 경우 스피너 visible
            }
        }
        binding.filterSpace.genderManBtn.setOnClickListener(this)
        binding.filterSpace.genderWomanBtn.setOnClickListener(this)

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
                childChip(it, tpoChipGroup, chipStyle, filterViewModel = viewModel){chipId,text,isChecked->
                    if(isChecked){
                        viewModel.selectedTpos.add(chipId)
                        viewModel.tposTexts.add(text)
                    }else{
                      viewModel.selectedTpos.remove(chipId)
                      viewModel.tposTexts.remove(text)
                    }
                    Log.e(TAG, "chipSetting: ${viewModel.tposTexts}")
                }
            }
        }
        viewModel.seasonChipList.observe(viewLifecycleOwner) {
            it?.let {
                val seasonChipGroup = binding.filterSpace.filterInclude.seasonChipGroup
                childChip(it, seasonChipGroup, chipStyle, filterViewModel = viewModel){chipId,text,isChecked->
                    if(isChecked){
                        viewModel.selectedSeasons.add(chipId)
                        viewModel.seasonsTexts.add(text)
                    }else{
                        viewModel.selectedSeasons.remove(chipId)
                        viewModel.seasonsTexts.remove(text)
                    }
                }
            }
        }
        viewModel.styleChipList.observe(viewLifecycleOwner) {
            it?.let {
                val styleChipGroup = binding.filterSpace.filterInclude.styleChipGroup
                childChip(it, styleChipGroup, chipStyle, filterViewModel = viewModel){chipId,text,isChecked->
                    if(isChecked){
                        viewModel.selectedStyles.add(chipId)
                        viewModel.stylesTexts.add(text)
                    }else{
                        viewModel.selectedStyles.remove(chipId)
                        viewModel.stylesTexts.remove(text)
                    }
                }
            }
        }
    }

    override fun onClick(v: View?) {
        val genderBtns = listOf(
            binding.filterSpace.genderManBtn,
            binding.filterSpace.genderWomanBtn
        )
        genderBtns.forEach { button ->
            button.isSelected = button == v
            if (button.isSelected) {
                viewModel.selectedGender = if (button.text.toString() == "MAN") "Male" else "Female"
            }
        }
    }
}