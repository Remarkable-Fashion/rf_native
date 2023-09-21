package com.lf.fashion.ui.globalFrag.editPost

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import com.lf.fashion.MainActivity
import com.lf.fashion.R
import com.lf.fashion.TAG
import com.lf.fashion.data.model.Posts
import com.lf.fashion.data.network.Resource
import com.lf.fashion.databinding.EditPostStep2FragmentBinding
import com.lf.fashion.ui.common.childChip
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditPostStep2Fragment : Fragment(R.layout.edit_post_step2_fragment) {
    private val viewModel: EditPostViewModel by hiltNavGraphViewModels(R.id.navigation_eidt_post)
    private lateinit var binding: EditPostStep2FragmentBinding
    private val chipStyle = "default"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainActivity.hideNavi(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = EditPostStep2FragmentBinding.bind(view)

        viewModel.getPostInfoByPostId(viewModel.postId!!)
        viewModel.postInfo.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    val response = resource.value

                    if (response.gender == "Male") {
                        binding.filterSpace.genderManBtn.isSelected = true
                    } else {
                        binding.filterSpace.genderWomanBtn.isSelected = true
                    }
                    viewModel.selectedTpos = response.tpos?.toMutableList()?: mutableListOf()
                    viewModel.selectedSeasons = response.season?.toMutableList()?: mutableListOf()
                    viewModel.selectedStyles = response.styles?.toMutableList()?: mutableListOf()
                    chipSetting()


                }

                else -> {

                }
            }
        }
    }
    private fun chipSetting() {
        viewModel.tpoChipList.observe(viewLifecycleOwner) {
            it?.let {
                val tpoChipGroup = binding.filterSpace.filterInclude.tpoChipGroup
                childChip(
                    it,
                    tpoChipGroup,
                    chipStyle,
                    editPostViewModel = viewModel
                ) {chipInfo, isChecked ->
                    if (isChecked) {
                        if (!viewModel.selectedTpos.any { it.text == chipInfo.text }) {
                            viewModel.selectedTpos.add(chipInfo)
                        }
                    } else {
                        viewModel.selectedTpos.remove(chipInfo)
                    }
                    Log.e(TAG, "chipSetting: ${viewModel.selectedTpos}")
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
                    editPostViewModel = viewModel
                ) { chipInfo, isChecked ->
                    if (isChecked) {
                        if (!viewModel.selectedSeasons.any { it.text == chipInfo.text }) {
                            viewModel.selectedSeasons.add(chipInfo)
                        }
                    } else {
                        viewModel.selectedSeasons.remove(chipInfo)

                    }
                    Log.e(TAG, "chipSetting: ${viewModel.selectedSeasons}")
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
                    editPostViewModel = viewModel
                ) { chipInfo, isChecked ->
                    if (isChecked) {
                        if (!viewModel.selectedStyles.any { it.text == chipInfo.text }) {
                            viewModel.selectedStyles.add(chipInfo)
                        }
                    } else {
                        viewModel.selectedStyles.remove(chipInfo)
                    }
                    Log.e(TAG, "chipSetting: ${viewModel.selectedStyles}")
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        MainActivity.hideNavi(false)
    }
}