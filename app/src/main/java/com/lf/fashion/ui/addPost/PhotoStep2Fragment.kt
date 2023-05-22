package com.lf.fashion.ui.addPost

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.lf.fashion.TAG
import com.lf.fashion.databinding.PhotoStep2FragmentBinding
import com.lf.fashion.ui.childChip
import com.lf.fashion.ui.home.FilterViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PhotoStep2Fragment : Fragment() {
    private lateinit var binding : PhotoStep2FragmentBinding
    private val viewModel : FilterViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = PhotoStep2FragmentBinding.inflate(inflater,container,false)
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

        rvAdapterSetting()

        val imageUris = arguments?.get("image_uri") as Array<*>
        Log.d(TAG, "PhotoStep2Fragment - onViewCreated: ${imageUris.get(0)}");
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
                            childChip(it[i].chips,tpoChipGroup,true)

                        }
                        "season"->{
                            childChip(it[i].chips,seasonChipGroup,true)
                        }
                        "style" ->{
                            childChip(it[i].chips,styleChipGroup,true)
                        }
                    }
                }
            }
        }
    }
    private fun rvAdapterSetting(){
        val category = listOf("Outer", "Top", "Bottom", "Acc")
        with(binding.clothesDetailRv){
            adapter = ClothesRvAdapter().apply {
                submitList(category)
            }
        }
    }
}