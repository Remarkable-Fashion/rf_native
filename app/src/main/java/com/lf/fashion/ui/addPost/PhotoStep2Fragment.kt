package com.lf.fashion.ui.addPost

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.view.*
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.lf.fashion.TAG
import com.lf.fashion.data.response.ClothesInfo
import com.lf.fashion.data.response.RegClothes
import com.lf.fashion.databinding.PhotoStep2FragmentBinding
import com.lf.fashion.ui.addPost.adapter.AddPostClothesRvAdapter
import com.lf.fashion.ui.cancelBtnBackStack
import com.lf.fashion.ui.childChip
import com.lf.fashion.ui.home.FilterViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PhotoStep2Fragment : Fragment(), View.OnClickListener {
    private lateinit var binding: PhotoStep2FragmentBinding
    private val viewModel: FilterViewModel by viewModels()
    private val regClothesList = MutableLiveData<MutableList<RegClothes>>()

    private var selectedCategory: String? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = PhotoStep2FragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //이미지 등록에서 받아온 이미지들 ..
        val imageUris = arguments?.get("image_uri") as Array<*>
        Log.d(TAG, "PhotoStep2Fragment - onViewCreated: ${imageUris.get(0)}");


        genderSelectUISetting()
        chipSetting()
        rvAdapterSetting()
        registerCloth()
        introduceLengthCounting()
        cancelBtnBackStack(binding.backBtn)
    }

    private fun genderSelectUISetting() {
        binding.filterSpace.genderManBtn.setOnClickListener {
            it.isSelected = !it.isSelected
        }
        binding.filterSpace.genderWomanBtn.setOnClickListener {
            it.isSelected = !it.isSelected
        }
    }

    private fun chipSetting() {
        viewModel.chipList.observe(viewLifecycleOwner) {
            it?.let {
                val tpoChipGroup = binding.filterSpace.filterInclude.tpoChipGroup
                val seasonChipGroup = binding.filterSpace.filterInclude.seasonChipGroup
                val styleChipGroup = binding.filterSpace.filterInclude.styleChipGroup
                val chipStyle = "default"

                for (i in it.indices) {
                    when (it[i].id) {
                        "tpo" -> {
                            childChip(it[i].chips, tpoChipGroup, chipStyle)

                        }
                        "season" -> {
                            childChip(it[i].chips, seasonChipGroup, chipStyle)
                        }
                        "style" -> {
                            childChip(it[i].chips, styleChipGroup, chipStyle)
                        }
                    }
                }
            }
        }
    }

    private fun rvAdapterSetting() {
        /*      val category = listOf("Outer", "Top", "Bottom", "Acc")
              with(binding.clothesDetailRv){
                  adapter = AddPostClothesRvAdapter().apply {
                      submitList(category)
                  }
              }*/
        regClothesList.observe(viewLifecycleOwner){
            if(it.size>0){
                with(binding.clothesDetailRv){
                    adapter = AddPostClothesRvAdapter().apply {
                        submitList(it)
                    }
                }
            }
        }
    }

    private fun registerCloth() {
        binding.clothRegistForm.topLinear.children.forEach { it.setOnClickListener(this) }
        binding.regClothBtn.setOnClickListener {
            val nameValue = binding.clothRegistForm.nameValue.text.toString()
            val priceValue = binding.clothRegistForm.priceValue.text.toString()
            val colorValue = binding.clothRegistForm.colorValue.text.toString()
            val sizeValue = binding.clothRegistForm.sizeValue.text.toString()
            val urlValue = binding.clothRegistForm.urlValue.text.toString()

            if (nameValue.isNotEmpty() && priceValue.isNotEmpty() && colorValue.isNotEmpty() && sizeValue.isNotEmpty() && selectedCategory != null) {
                if (regClothesList.value == null) {
                    regClothesList.value = mutableListOf()
                }
                regClothesList.value?.add(
                    RegClothes(
                        null,
                        selectedCategory!!,
                        nameValue,
                        priceValue,
                        colorValue,
                        sizeValue,
                        urlValue
                    )
                )

            } else if (selectedCategory == null) {
                Toast.makeText(requireContext(), "의상 카테고리를 선택해주세요!", Toast.LENGTH_SHORT).show()

            } else {
                Toast.makeText(requireContext(), "의상에 관한 정보를 모두 입력해주세요!", Toast.LENGTH_SHORT).show()
            }
        }

    }


    private fun introduceLengthCounting() {
        binding.introduceValue.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                val count = s.toString().count()
                binding.textCounter.text = "$count/50"
            }

        })
    }

    override fun onClick(v: View?) {

        binding.clothRegistForm.topLinear.forEach { it.isSelected = false }

        val categoryButtons = listOf(
            binding.clothRegistForm.outerBtn,
            binding.clothRegistForm.topBtn,
            binding.clothRegistForm.bottomBtn,
            binding.clothRegistForm.shoesBtn,
            binding.clothRegistForm.accBtn
        )
        categoryButtons.forEach { button ->
            button.isSelected = button == v
            if (button.isSelected) {
                selectedCategory = button.text.toString()
            }
        }
    }
}