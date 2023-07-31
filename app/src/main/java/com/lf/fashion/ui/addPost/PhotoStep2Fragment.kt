package com.lf.fashion.ui.addPost

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.lf.fashion.R
import com.lf.fashion.TAG
import com.lf.fashion.data.response.RegClothes
import com.lf.fashion.databinding.PhotoStep2FragmentBinding
import com.lf.fashion.ui.AddPostClothesRvAdapter
import com.lf.fashion.ui.addTextLengthCounter
import com.lf.fashion.ui.cancelBtnBackStack
import com.lf.fashion.ui.childChip
import com.lf.fashion.ui.home.FilterViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PhotoStep2Fragment : Fragment(), View.OnClickListener {
    private lateinit var binding: PhotoStep2FragmentBinding
    private val viewModel: FilterViewModel by viewModels()
    private val regClothesList = mutableListOf<RegClothes>()
    private val addClothesAdapter = AddPostClothesRvAdapter()
    private var selectedCategory: String? = null
    private var selectedImageUri :String? = null

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

        //photoFragment -> ImagePicker -> PHtoStep2Fragment 로 받아온 이미지들 ..
        val imageUris = arguments?.get("image_uri") as Array<*>
        Log.d(TAG, "PhotoStep2Fragment - onViewCreated: ${imageUris[0]}")

        with(binding.clothesDetailRv){
            adapter = addClothesAdapter
        }
        genderSelectUISetting()
        chipSetting()
        registerCloth()
        addTextLengthCounter(binding.introduceValue,binding.textCounter,50)
        imagePickerOpen()
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


    private fun registerCloth() {
        binding.clothRegistForm.topLinear.children.forEach { it.setOnClickListener(this) }
        binding.regClothBtn.setOnClickListener {
            val nameValue = binding.clothRegistForm.nameValue.text.toString()
            val priceValue = binding.clothRegistForm.priceValue.text.toString()
            val colorValue = binding.clothRegistForm.colorValue.text.toString()
            val sizeValue = binding.clothRegistForm.sizeValue.text.toString()
            val brandValue = binding.clothRegistForm.brandValue.text.toString()

            if (nameValue.isNotEmpty() && priceValue.isNotEmpty() && colorValue.isNotEmpty() && sizeValue.isNotEmpty() && selectedCategory != null) {

                regClothesList.add(
                    RegClothes(
                        selectedImageUri,
                        selectedCategory!!,
                        nameValue,
                        priceValue,
                        colorValue,
                        sizeValue,
                        brandValue
                    )
                )
                addClothesAdapter.apply {
                    submitList(regClothesList)
                    notifyItemInserted(regClothesList.size-1)
                }

                // 요소들의 텍스트를 빈 값으로 설정
                binding.clothRegistForm.productImage.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.ic_add_item_mini))
                binding.clothRegistForm.nameValue.text.clear()
                binding.clothRegistForm.priceValue.text.clear()
                binding.clothRegistForm.colorValue.text.clear()
                binding.clothRegistForm.sizeValue.text.clear()
                binding.clothRegistForm.brandValue.text.clear()

            } else if (selectedCategory == null) {
                Toast.makeText(requireContext(), "의상 카테고리를 선택해주세요!", Toast.LENGTH_SHORT).show()

            } else {
                Toast.makeText(requireContext(), "의상에 관한 정보를 모두 입력해주세요!", Toast.LENGTH_SHORT).show()
            }
        }

    }

    //TODO 의상 등록을 하고 backstack 으로 돌아오면 edittext는 남아잇지만 버튼 isSelected 가 다 해제된다.
    private fun imagePickerOpen(){
        binding.clothRegistForm.productImage.setOnClickListener {
            findNavController().navigate(
                R.id.action_photoStep2Fragment_to_imagePickerFragment,
                bundleOf("from" to "PhotoStep2Fragment" , "limit" to 4)
            )
        }
        setFragmentResultListener(requestKey = ImagePickerFragment.REQUEST_KEY){
            _, bundle ->
            //의상 등록 부분에서 이미지 받아온 것
            val imageUris = bundle.get("imageURI") as Array<*>
            imageUris[0]?.let {
                selectedImageUri = imageUris[0].toString()
                Glide.with(binding.root)
                    .load(it)
                    .into(binding.clothRegistForm.productImage)
            }
        }
    }

    override fun onClick(v: View?) {
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