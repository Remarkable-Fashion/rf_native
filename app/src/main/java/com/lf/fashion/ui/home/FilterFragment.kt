package com.lf.fashion.ui.home

import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.lf.fashion.R
import com.lf.fashion.data.response.ChipContents
import com.lf.fashion.databinding.HomeBPhotoFilterFragmentBinding
import com.lf.fashion.ui.cancelBtnBackStack
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FilterFragment : Fragment() {
    private lateinit var binding : HomeBPhotoFilterFragmentBinding
    private val viewModel : FilterViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = HomeBPhotoFilterFragmentBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.genderManBtn.setOnClickListener {
            it.isSelected = !it.isSelected
        }
        binding.genderWomanBtn.setOnClickListener {
            it.isSelected = !it.isSelected
        }


        chipSetting()

        cancelBtnBackStack(binding.cancelBtn)
    }

    private fun chipSetting(){
        viewModel.chipList.observe(viewLifecycleOwner){
            it?.let{
                val tpoChipGroup = binding.filterInclude.tpoChipGroup
                val seasonChipGroup = binding.filterInclude.seasonChipGroup
                val styleChipGroup = binding.filterInclude.styleChipGroup
                for( i in  it.indices ){
                    when(it[i].id){
                        "tpo"->{
                            childChip(it[i].chips,tpoChipGroup)

                        }
                        "season"->{
                            childChip(it[i].chips,seasonChipGroup)
                        }
                        "style" ->{
                            childChip(it[i].chips,styleChipGroup)
                        }
                    }
                }
            }
        }
    }

    private fun childChip(chipList : List<ChipContents>,chipGroup: ChipGroup){
        for (j in chipList.indices) {
            val chip = LayoutInflater.from(context)
                .inflate(R.layout.chip_item, null) as Chip

            val emoji = chipList[j].emoji?.substring(2)?.toInt(16)
            var content = chipList[j].text
            emoji?.let { unicode ->
                content += " "+ String(Character.toChars(unicode))
            }
            chip.text = content
            chipGroup.addView(chip)
        }
    }
}