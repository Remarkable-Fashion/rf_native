package com.lf.fashion.ui.search

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.chip.Chip
import com.lf.fashion.R
import com.lf.fashion.data.common.SearchLookFilterDataStore
import com.lf.fashion.data.model.ChipInfo
import com.lf.fashion.data.model.FilterItem
import com.lf.fashion.databinding.SearchFilterFragmentBinding
import com.lf.fashion.ui.common.addUnitTextListener
import com.lf.fashion.ui.common.cancelBtnBackStack
import com.lf.fashion.ui.common.childChip
import com.lf.fashion.ui.home.frag.FilterViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

/**
 * 홈 메인 상단의 필터 아이콘을 클릭시 노출되는 프래그먼트입니다.
 */
@AndroidEntryPoint
class LookFilterFragment : Fragment(R.layout.search_filter_fragment), View.OnClickListener {
    private lateinit var binding: SearchFilterFragmentBinding
    private val viewModel: FilterViewModel by viewModels()
    private val chipStyle = "default"
    private lateinit var lookFilterDataStore: SearchLookFilterDataStore
    override fun onResume() {
        viewModel.selectedGender?.let {
            if (it == "Male") {
                binding.filterSpace.genderManBtn.isSelected = true
            } else {
                binding.filterSpace.genderWomanBtn.isSelected = true
            }
        }
        viewModel.savedHeight?.let {
            binding.filterSpace.heightValue.setText("$it cm")
        }
        viewModel.savedWeight?.let {
            binding.filterSpace.weightValue.setText("$it kg")
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
        addUnitTextListener(binding.filterSpace.heightValue, height = true) {
            if (it.isNotEmpty()) {
                viewModel.savedHeight = it.toInt()
            } else {
                viewModel.savedHeight = null
            }        }
        addUnitTextListener(binding.filterSpace.weightValue, height = false) {
            if (it.isNotEmpty()) {
                viewModel.savedWeight = it.toInt()
            } else {
                viewModel.savedWeight = null
            }        }
    }

    private fun chipSetting() {
            viewModel.tpoChipList.observe(viewLifecycleOwner) {
                it?.let {
                    val tpoChipGroup = binding.filterSpace.filterInclude.tpoChipGroup
                    childChip(
                        it,
                        tpoChipGroup,
                        chipStyle,
                        filterViewModel = viewModel
                    ) { chipInfo, isChecked ->
                        if (isChecked) {
                            if(!viewModel.selectedTpos.any { it.text == chipInfo.text }) {
                                viewModel.selectedTpos.add(chipInfo)
                            }
                        } else {
                            viewModel.selectedTpos.remove(chipInfo)
                        }
                    }
                }
            }
            viewModel.seasonChipList.observe(viewLifecycleOwner) {
                it?.let {
                    val seasonChipGroup = binding.filterSpace.filterInclude.seasonChipGroup
                    childChip(
                        it,
                        seasonChipGroup,
                        chipStyle,
                        filterViewModel = viewModel
                    ) { chipInfo, isChecked ->
                        if (isChecked) {
                            if(!viewModel.selectedSeasons.any { it.text == chipInfo.text }) {
                                viewModel.selectedSeasons.add(chipInfo)
                            }
                        } else {
                            viewModel.selectedSeasons.remove(chipInfo)
                        }
                    }
                }
            }
            viewModel.styleChipList.observe(viewLifecycleOwner) {
                it?.let {
                    val styleChipGroup = binding.filterSpace.filterInclude.styleChipGroup
                    childChip(
                        it,
                        styleChipGroup,
                        chipStyle,
                        filterViewModel = viewModel
                    ) { chipInfo, isChecked ->
                        if (isChecked) {
                            if(!viewModel.selectedStyles.any { it.text == chipInfo.text }) {
                                viewModel.selectedStyles.add(chipInfo)
                            }
                        } else {
                            viewModel.selectedStyles.remove(chipInfo)
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
                viewModel.selectedGender =
                    if (button.text.toString() == "MAN") "Male" else "Female"
            }
        }
    }

    private fun clearLookFilter() {
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

    private fun saveLookFilter() {
        binding.submitBtn.setOnClickListener {
            binding.filterSpace.heightValue.clearFocus()
            binding.filterSpace.weightValue.clearFocus()
            val tpoFilterItem = FilterItem(
                viewModel.selectedTpos.joinToString(",") { it.text },
                viewModel.selectedTpos.joinToString(","){ it.id.toString() }
            )
            val seasonFilterItem = FilterItem(
                viewModel.selectedSeasons.joinToString(",") { it.text },
                viewModel.selectedSeasons.joinToString(","){ it.id.toString() }
            )
            val styleFilterItem = FilterItem(
                viewModel.selectedStyles.joinToString(",") { it.text },
                viewModel.selectedStyles.joinToString(","){ it.id.toString() }
            )
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

    private fun exposeSavedValue() {
        CoroutineScope(Dispatchers.Main).launch {
            with(lookFilterDataStore) {
                height.first()?.let {
                    binding.filterSpace.heightValue.setText("$it cm")
                    viewModel.savedHeight = it
                }
                weight.first()?.let {
                    binding.filterSpace.weightValue.setText("$it kg")
                    viewModel.savedWeight = it
                }
                lookGender.first()?.let {
                    if (it == "Male") {
                        binding.filterSpace.genderManBtn.isSelected = true
                    } else if (it == "Female") {
                        binding.filterSpace.genderWomanBtn.isSelected = true
                    }
                    viewModel.selectedGender = it
                }
                tpo.firstOrNull()?.let { tpoData ->
                    val tpo = tpoData.split(",").toMutableList()
                    val tpoIdData = tpoId.firstOrNull()
                    if (!tpoIdData.isNullOrEmpty()) {
                        val tpoIds = tpoIdData.split(",").mapNotNull { it.toIntOrNull() }.toMutableList()
                        val chipInfoList = tpo.zip(tpoIds) { text, id -> ChipInfo(id, text,null) }
                        viewModel.selectedTpos = chipInfoList.toMutableList()
                    }
                }

                season.firstOrNull()?.let { seasonData ->
                    val season = seasonData.split(",").toMutableList()
                    val seasonIdData = seasonId.firstOrNull()
                    if (!seasonIdData.isNullOrEmpty()) {
                        val seasonIds = seasonIdData.split(",").mapNotNull { it.toIntOrNull() }.toMutableList()
                        val chipInfoList = season.zip(seasonIds) { text, id -> ChipInfo(id, text,null) }
                        viewModel.selectedSeasons = chipInfoList.toMutableList()
                    }
                }

                style.firstOrNull()?.let { styleData ->
                    val style = styleData.split(",").toMutableList()
                    val styleIdData = styleId.firstOrNull()
                    if (!styleIdData.isNullOrEmpty()) {
                        val styleIds = styleIdData.split(",").mapNotNull { it.toIntOrNull() }.toMutableList()
                        val chipInfoList = style.zip(styleIds) { text, id -> ChipInfo(id, text,null) }
                        viewModel.selectedStyles = chipInfoList.toMutableList()
                    }
                }
            }
        }
    }
}