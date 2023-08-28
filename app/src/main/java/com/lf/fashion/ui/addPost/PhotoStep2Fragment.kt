package com.lf.fashion.ui.addPost

import android.os.Bundle
import android.text.Editable
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
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.lf.fashion.R
import com.lf.fashion.TAG
import com.lf.fashion.data.model.UploadCloth
import com.lf.fashion.data.model.UploadPost
import com.lf.fashion.databinding.PhotoStep2FragmentBinding
import com.lf.fashion.ui.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.math.log

@AndroidEntryPoint
class PhotoStep2Fragment : Fragment(), View.OnClickListener {
    private lateinit var binding: PhotoStep2FragmentBinding
    private val viewModel: UploadPostViewModel by viewModels()
    private val regClothesList = mutableListOf<UploadCloth>()
    private val addClothesAdapter = AddPostClothesRvAdapter()

    private var selectedClothImageUri: String? = null
    private val chipStyle = "default"
    private lateinit var name: Editable
    private lateinit var price: Editable
    private lateinit var color: Editable
    private lateinit var size: Editable
    private lateinit var brand: Editable

    override fun onResume() {
        viewModel.selectedGender?.let {
            if (it == "Male") { binding.filterSpace.genderManBtn.isSelected = true }
            else{ binding.filterSpace.genderWomanBtn.isSelected = true }
        }

        viewModel.selectedClothCategory?.let {
            val cloth = binding.clothRegistForm
            when (it) {
                "Outer" -> cloth.outerBtn.isSelected = true
                "Top" -> cloth.topBtn.isSelected = true
                "Bottom" -> cloth.bottomBtn.isSelected = true
                "Shoes" -> cloth.shoesBtn.isSelected = true
                else -> cloth.accBtn.isSelected = true
            }
        }

        super.onResume()
    }

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
        //생성 후 다른 바텀 메뉴 이동시 다시 home menu 클릭시 selected 아이콘으로 변경 안되는 오류 해결하기위해 수동 메뉴 checked 코드 추가
        val bottomNavigationView =
            requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavBar)
        val homeMenu = bottomNavigationView.menu.findItem(R.id.navigation_photo)
        homeMenu.isChecked = true

        name = binding.clothRegistForm.nameValue.text
        price = binding.clothRegistForm.priceValue.text
        color = binding.clothRegistForm.colorValue.text
        size = binding.clothRegistForm.sizeValue.text
        brand = binding.clothRegistForm.brandValue.text

        //photoFragment -> ImagePicker -> PHtoStep2Fragment 로 받아온 이미지들 ..
        val imageUris = arguments?.get("image_uri") as Array<String>
        viewModel.selectedPostImages = imageUris.toMutableList()

        if (viewModel.selectedPostImages.isEmpty()) {
            Toast.makeText(requireContext(), "사진을 불러오는데 실패했습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d(TAG, "PhotoStep2Fragment - onViewCreated: ${viewModel.selectedPostImages}}")

        with(binding.clothesDetailRv) {
            adapter = addClothesAdapter
        }
        binding.clothRegistForm.topLinear.children.forEach { it.setOnClickListener(this) }
        binding.filterSpace.genderManBtn.setOnClickListener(this)
        binding.filterSpace.genderWomanBtn.setOnClickListener(this)

        chipSetting() //top,season,style 칩 생성
        registerCloth() //의상 등록
        addTextLengthCounter(binding.introduceValue, binding.textCounter, 50) //소개글 글자수 카운터
        addUnitTextListener(binding.filterSpace.heightValue, height = true) //
        addUnitTextListener(binding.filterSpace.weightValue, height = false)
        imagePickerOpen()
        cancelBtnBackStack(binding.backBtn)
        submitBtnOnclick()

    }


    private fun chipSetting() {
        viewModel.tpoChipList.observe(viewLifecycleOwner) {
            it?.let {
                val tpoChipGroup = binding.filterSpace.filterInclude.tpoChipGroup
                childChip(it, tpoChipGroup, chipStyle , uploadPostViewModel =  viewModel) { chipId ,text, isChecked ->
                    if(isChecked){
                        viewModel.selectedTpos.add(chipId)
                        viewModel.tposTexts.add(text)

                    }else {
                        viewModel.selectedTpos.remove(chipId)
                        viewModel.tposTexts.remove(text)

                    }
                    Log.e(TAG, "chipSetting: ${viewModel.selectedTpos}")
                }
            }
        }
        viewModel.seasonChipList.observe(viewLifecycleOwner) {
            it?.let {
                val seasonChipGroup = binding.filterSpace.filterInclude.seasonChipGroup
                childChip(it, seasonChipGroup, chipStyle, uploadPostViewModel = viewModel) { chipId,text,isChecked ->
                    if(isChecked){
                        viewModel.selectedSeasons.add(chipId)
                        viewModel.seasonsTexts.add(text)
                    }else {
                        viewModel.selectedSeasons.remove(chipId)
                        viewModel.seasonsTexts.remove(text)

                    }
                    Log.e(TAG, "chipSetting: ${viewModel.selectedSeasons}")
                }
            }
        }
        viewModel.styleChipList.observe(viewLifecycleOwner) {
            it?.let {
                val styleChipGroup = binding.filterSpace.filterInclude.styleChipGroup
                childChip(it, styleChipGroup, chipStyle, uploadPostViewModel = viewModel) { chipId,text,isChecked ->
                    if(isChecked){
                        viewModel.selectedStyles.add(chipId)
                        viewModel.stylesTexts.add(text)

                    }else {
                        viewModel.selectedStyles.remove(chipId)
                        viewModel.stylesTexts.remove(text)

                    }
                    Log.e(TAG, "chipSetting: ${viewModel.selectedStyles}")
                }
            }
        }
    }

    //Chip checked id 불일치 , 선ㅇ택해제 반영안됨
    private fun registerCloth() {
        binding.regClothBtn.setOnClickListener {

            if (name.toString().isNotEmpty() && price.toString().isNotEmpty() && color.toString()
                    .isNotEmpty() && size.toString()
                    .isNotEmpty() && viewModel.selectedClothCategory != null
            ) {

                regClothesList.add(
                    UploadCloth(
                        name.toString(),
                        viewModel.selectedClothCategory!!,//viewModel.selectedCategory!!,
                        selectedClothImageUri!!,
                        price.toString().toInt(),
                        color.toString(),
                        size.toString(),
                        brand.toString(),
                        null
                    )
                )
                addClothesAdapter.apply {
                    submitList(regClothesList)
                    notifyItemInserted(regClothesList.size - 1)
                }

                // 요소들의 텍스트를 빈 값으로 설정
                binding.clothRegistForm.productImage.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_add_item_mini
                    )
                )
                name.clear()
                price.clear()
                color.clear()
                size.clear()
                brand.clear()

            } else if (viewModel.selectedClothCategory == null) {
                Toast.makeText(requireContext(), "의상 카테고리를 선택해주세요!", Toast.LENGTH_SHORT).show()

            } else {
                Toast.makeText(requireContext(), "의상에 관한 정보를 모두 입력해주세요!", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun imagePickerOpen() {
        binding.clothRegistForm.productImage.setOnClickListener {
            findNavController().navigate(
                R.id.action_photoStep2Fragment_to_imagePickerFragment,
                bundleOf("from" to "PhotoStep2Fragment", "limit" to 4)
            )
        }
        setFragmentResultListener(requestKey = ImagePickerFragment.REQUEST_KEY) { _, bundle ->
            //의상 등록 부분에서 이미지 받아온 것
            val imageUris = bundle.get("imageURI") as Array<*>
            imageUris[0]?.let {
                selectedClothImageUri = imageUris[0].toString()
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
                viewModel.selectedClothCategory = button.text.toString()
            }
        }

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

    private fun submitBtnOnclick() {
        binding.submitBtn.setOnClickListener {
            //비어있는 값 없고, cloth 등록하다만 기록 없을 때
            if (valueValidation()) {
                //의상 & 게시물 등록
                //TODO 수정 필요 : 우선 image upload하고 url 받은걸 post imageUrl에 추가해서 ... 하는듯 .. !?
                runBlocking {
                    launch {
                        val uploadPost = UploadPost(
                            viewModel.selectedPostImages,
                            "추후 삭제 예정",
                            binding.introduceValue.text.toString(),
                            viewModel.selectedGender!!,
                            viewModel.selectedTpos,
                            viewModel.selectedSeasons,
                            viewModel.selectedStyles,
                            clothes = addClothesAdapter.currentList
                        )
                        val postUpload = viewModel.uploadPostInfo(uploadPost)
                        val imageUpload = viewModel.uploadPostImages(viewModel.selectedPostImages)
                        if (postUpload.success && imageUpload.success) {

                        }
                    }
                }


                val clothList = addClothesAdapter.currentList

            }
        }
    }

    private fun valueValidation(): Boolean {
        var clothClear = true
        if (name.toString().isNotEmpty() || price.toString().isNotEmpty() || color.toString()
                .isNotEmpty() || size.toString().isNotEmpty() || brand.toString().isNotEmpty()
        ) {
            val loginDialog = androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setMessage("등록 중이신 의상이 있습니다. 삭제하고 등록하시겠습니까?")
                .setPositiveButton("네") { _, _ ->
                    clothClear = true
                }
                .setNegativeButton("닫기") { _, _ ->
                    clothClear = false
                }
            loginDialog.show()

        }

        val gender = viewModel.selectedGender
        val tpos = viewModel.selectedTpos
        val seasons = viewModel.selectedSeasons
        val styles = viewModel.selectedStyles
        val height = binding.filterSpace.heightValue.text.toString()
        val weight = binding.filterSpace.weightValue.text.toString()
        val introduce = binding.introduceValue.text.toString()

        //비어있는 값 없고, cloth 등록하다만 기록 없을 때
        if (gender != null &&
            tpos.isNotEmpty() &&
            seasons.isNotEmpty() &&
            styles.isNotEmpty() &&
            height.isNotEmpty() &&
            weight.isNotEmpty() &&
            introduce.isNotEmpty() &&
            clothClear
        ) {
            Log.e(TAG, "valueValidation: ${!gender.isNullOrEmpty()}")
            return true
        }
        return false
    }
}