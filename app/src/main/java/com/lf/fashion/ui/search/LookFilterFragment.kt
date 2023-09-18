package com.lf.fashion.ui.search

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.chip.Chip
import com.lf.fashion.R
import com.lf.fashion.TAG
import com.lf.fashion.data.common.SearchLookFilterDataStore
import com.lf.fashion.data.model.FilterItem
import com.lf.fashion.databinding.SearchFilterFragmentBinding
import com.lf.fashion.ui.addUnitTextListener
import com.lf.fashion.ui.cancelBtnBackStack
import com.lf.fashion.ui.childChip
import com.lf.fashion.ui.home.frag.FilterViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * 홈 메인 상단의 필터 아이콘을 클릭시 노출되는 프래그먼트입니다.
 */
@AndroidEntryPoint
class LookFilterFragment : Fragment(R.layout.search_filter_fragment),View.OnClickListener {
    private lateinit var binding: SearchFilterFragmentBinding
    private val viewModel: FilterViewModel by viewModels()
    private val chipStyle = "default"
    private lateinit var lookFilterDataStore: SearchLookFilterDataStore
    override fun onResume() {
        viewModel.selectedGender?.let {
            if (it == "Male") { binding.filterSpace.genderManBtn.isSelected = true }
            else{ binding.filterSpace.genderWomanBtn.isSelected = true }
        }
        super.onResume()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = SearchFilterFragmentBinding.bind(view)
        lookFilterDataStore = SearchLookFilterDataStore(requireContext().applicationContext)

        arguments?.get("searchResult")?.let {
            if (it as Boolean) {
                //searchResult 일 경우 스피너 visible
            }
        }
        binding.filterSpace.genderManBtn.setOnClickListener(this)
        binding.filterSpace.genderWomanBtn.setOnClickListener(this)

        chipSetting()

        cancelBtnBackStack(binding.cancelBtn)
        exposeSavedValue() // datastore 에 저장된 필터값 ui에 노출
        editTextListenerSetting()
        clearLookFilter()
        saveLookFilter()
    }

    private fun editTextListenerSetting() {
        addUnitTextListener(binding.filterSpace.heightValue, height = true){
            viewModel.savedHeight = it.toInt()
        }
        addUnitTextListener(binding.filterSpace.weightValue, height = false){
            viewModel.savedWeight = it.toInt()
        }
    }

    private fun chipSetting() {
        viewModel.tpoChipList.observe(viewLifecycleOwner) {
            it?.let {
                val tpoChipGroup = binding.filterSpace.filterInclude.tpoChipGroup
                childChip(it, tpoChipGroup, chipStyle, filterViewModel = viewModel){chipId,text,isChecked->
                    if(isChecked){
                        viewModel.selectedTposId.add(chipId)
                        viewModel.tposTexts.add(text)
                    }else{
                      viewModel.selectedTposId.remove(chipId)
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
                        viewModel.selectedSeasonsId.add(chipId)
                        viewModel.seasonsTexts.add(text)
                    }else{
                        viewModel.selectedSeasonsId.remove(chipId)
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
                        viewModel.selectedStylesId.add(chipId)
                        viewModel.stylesTexts.add(text)
                    }else{
                        viewModel.selectedStylesId.remove(chipId)
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
    private fun clearLookFilter(){
        binding.clearBtn.setOnClickListener {
            binding.filterSpace.apply {
                genderManBtn.isSelected = false
                genderWomanBtn.isSelected = false
                heightValue.setText("")
                weightValue.setText("")
                filterInclude.tpoChipGroup.children.forEach {
                    if (it is Chip) {
                        it.isChecked = false
                    }
                }
                filterInclude.seasonChipGroup.children.forEach {
                    if (it is Chip) {
                        it.isChecked = false
                    }
                }
                filterInclude.styleChipGroup.children.forEach {
                    if (it is Chip) {
                        it.isChecked = false
                    }
                }
            }
            viewModel.clearAll()
            CoroutineScope(Dispatchers.IO).launch {
                lookFilterDataStore.clearLookFilter()
            }
        }
    }
    private fun saveLookFilter(){
        binding.submitBtn.setOnClickListener {
            binding.filterSpace.heightValue.clearFocus()
            binding.filterSpace.weightValue.clearFocus()
            val tpoFilterItem = FilterItem(viewModel.tposTexts.joinToString(","),viewModel.selectedTposId.joinToString (","))
            val seasonFilterItem = FilterItem(viewModel.seasonsTexts.joinToString(","),viewModel.selectedSeasonsId.joinToString (","))
            val styleFilterItem = FilterItem(viewModel.stylesTexts.joinToString(","),viewModel.selectedStylesId.joinToString (","))
            CoroutineScope(Dispatchers.IO).launch {
                lookFilterDataStore.saveLookFilterInstance(
                    viewModel.selectedGender,
                    viewModel.savedHeight,
                    viewModel.savedWeight,
                    tpoFilterItem,
                    seasonFilterItem,
                    styleFilterItem
                )
            }
            Toast.makeText(requireContext(), "필터가 저장되었습니다.", Toast.LENGTH_SHORT).show()
        }
    }
    private fun exposeSavedValue(){
        CoroutineScope(Dispatchers.Main).launch{
            with(lookFilterDataStore){
                height.first()?.let {
                    Log.e(TAG, "exposeSavedValue: $it")
                    binding.filterSpace.heightValue.setText("$it cm")
                }
                weight.first()?.let{
                    binding.filterSpace.weightValue.setText("$it kg")
                }
                lookGender.first()?.let{
                    if(it =="Male"){
                        binding.filterSpace.genderManBtn.isSelected = true
                    }else if( it == "Female"){
                        binding.filterSpace.genderWomanBtn.isSelected = true
                    }
                }
                tpo.first()?.let{
                    val tpo = it.split(",").toMutableList()
                    viewModel.tposTexts = tpo
                }
                season.first()?.let{
                    val season = it.split(",").toMutableList()
                    viewModel.seasonsTexts = season
                }
                style.first()?.let{
                    val style = it.split(",").toMutableList()
                    viewModel.stylesTexts = style
                }
            }
        }
    }
}