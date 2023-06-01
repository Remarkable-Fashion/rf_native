package com.lf.fashion.ui.addPost

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.lf.fashion.TAG
import com.lf.fashion.databinding.PhotoStep2FragmentBinding
import com.lf.fashion.ui.addPost.adapter.AddPostClothesRvAdapter
import com.lf.fashion.ui.cancelBtnBackStack
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
        //이미지 등록에서 받아온 이미지들 ..
        val imageUris = arguments?.get("image_uri") as Array<*>
        Log.d(TAG, "PhotoStep2Fragment - onViewCreated: ${imageUris.get(0)}");

        binding.filterSpace.genderManBtn.setOnClickListener {
            it.isSelected = !it.isSelected
        }
        binding.filterSpace.genderWomanBtn.setOnClickListener {
            it.isSelected = !it.isSelected
        }

        chipSetting()

        rvAdapterSetting()

        introduceLengthCounting()

        cancelBtnBackStack(binding.backBtn)
    }
    private fun chipSetting(){
        viewModel.chipList.observe(viewLifecycleOwner){
            it?.let{
                val tpoChipGroup = binding.filterSpace.filterInclude.tpoChipGroup
                val seasonChipGroup = binding.filterSpace.filterInclude.seasonChipGroup
                val styleChipGroup = binding.filterSpace.filterInclude.styleChipGroup
                val chipStyle = "default"

                for( i in  it.indices ){
                    when(it[i].id){
                        "tpo"->{
                            childChip(it[i].chips,tpoChipGroup,chipStyle)

                        }
                        "season"->{
                            childChip(it[i].chips,seasonChipGroup,chipStyle)
                        }
                        "style" ->{
                            childChip(it[i].chips,styleChipGroup,chipStyle)
                        }
                    }
                }
            }
        }
    }
    private fun rvAdapterSetting(){
        val category = listOf("Outer", "Top", "Bottom", "Acc")
        with(binding.clothesDetailRv){
            adapter = AddPostClothesRvAdapter().apply {
                submitList(category)
            }
        }
    }
    private fun introduceLengthCounting(){
        binding.introduceValue.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                val count = s.toString().count()
                binding.textCounter.text="$count/50"
            }

        })
    }
}