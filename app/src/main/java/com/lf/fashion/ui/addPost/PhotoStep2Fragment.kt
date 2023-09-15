package com.lf.fashion.ui.addPost

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class PhotoStep2Fragment : Fragment(), View.OnClickListener {
    private lateinit var binding: PhotoStep2FragmentBinding
    private val viewModel: UploadPostViewModel by hiltNavGraphViewModels(R.id.navigation_photo)
    private val regClothesList = mutableListOf<UploadCloth>()
    private val addClothesAdapter = AddPostClothesRvAdapter()

    //selectedClothImageUri 는 adpater 에 보내서 띄워주는 역할을 한다
    private var selectedClothImageUri: String? = null
    private val chipStyle = "default"

    override fun onResume() {
        viewModel.selectedGender?.let {
            if (it == "Male") {
                binding.filterSpace.genderManBtn.isSelected = true
            } else {
                binding.filterSpace.genderWomanBtn.isSelected = true
            }
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
        submitBtnOnclick()
        binding.backBtn.setOnClickListener {
            backStackDialogShow()
        }

    }

    private fun backStackDialogShow() {
        AppCustomDialog(
            "이전 화면으로 이동하겠습니까?\n" +
                    "등록 중인 내용은 초기화됩니다.",
            "확인",
            "닫기", null
        ) {
            findNavController().popBackStack()
        }.show(parentFragmentManager, "photoStep2_dialog")
    }


    private fun chipSetting() {
        viewModel.tpoChipList.observe(viewLifecycleOwner) {
            it?.let {
                val tpoChipGroup = binding.filterSpace.filterInclude.tpoChipGroup
                childChip(
                    it,
                    tpoChipGroup,
                    chipStyle,
                    uploadPostViewModel = viewModel
                ) { chipId, text, isChecked ->
                    if (isChecked) {
                        if (!viewModel.selectedTpos.contains(chipId)) {
                            viewModel.selectedTpos.add(chipId)
                            viewModel.tposTexts.add(text)
                        }
                    } else {
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
                childChip(
                    it,
                    seasonChipGroup,
                    chipStyle,
                    uploadPostViewModel = viewModel
                ) { chipId, text, isChecked ->
                    if (isChecked) {
                        if (!viewModel.selectedSeasons.contains(chipId)) {
                            viewModel.selectedSeasons.add(chipId)
                            viewModel.seasonsTexts.add(text)
                        }
                    } else {
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
                childChip(
                    it,
                    styleChipGroup,
                    chipStyle,
                    uploadPostViewModel = viewModel
                ) { chipId, text, isChecked ->
                    if (isChecked) {
                        if (!viewModel.selectedStyles.contains(chipId)) {
                            viewModel.selectedStyles.add(chipId)
                            viewModel.stylesTexts.add(text)
                        }
                    } else {
                        viewModel.selectedStyles.remove(chipId)
                        viewModel.stylesTexts.remove(text)

                    }
                    Log.e(TAG, "chipSetting: ${viewModel.selectedStyles}")
                }
            }
        }
    }

    //Chip 재생성 후 checked 반영이 text를 기준으로 하기 때문에 etc를 한번 클릭시 모든 etc chip이 다눌림
    private fun registerCloth() {
        binding.regClothBtn.setOnClickListener {
            val name = binding.clothRegistForm.nameValue.text
            val price = binding.clothRegistForm.priceValue.text
            val color = binding.clothRegistForm.colorValue.text
            val size = binding.clothRegistForm.sizeValue.text
            val brand = binding.clothRegistForm.brandValue.text

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
                Log.e(TAG, "registerCloth: ${viewModel.selectedClothCategory} , binding.name.")
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
            //value validation start
            clothValueValidation()
        }
    }
    private fun clothValueValidation(){
        val name = binding.clothRegistForm.nameValue.text
        val price = binding.clothRegistForm.priceValue.text
        val color = binding.clothRegistForm.colorValue.text
        val size = binding.clothRegistForm.sizeValue.text
        val brand = binding.clothRegistForm.brandValue.text

        if (name.toString().isNotEmpty() || price.toString().isNotEmpty() || color.toString()
                .isNotEmpty() || size.toString().isNotEmpty() || brand.toString().isNotEmpty()
        ) {
            //의상 등록 부분에 작성하다가만 텍스트가 존재할 경우
            AppCustomDialog(
                "등록 중이신 의상이 있습니다.\n삭제하고 등록하시겠습니까?"
            ){
                val isPostValid = postValueValidation()
                Log.e(TAG, "clothValueValidation: $isPostValid")
                if(isPostValid){
                    //의상 & 게시물 등록
                    binding.progressBar.visibility = View.VISIBLE
                    uploadPostAndClothes()
                }
            }.show(parentFragmentManager,"alert_info_clear")

        }else{ //작성하다만 텍스트는 없지만 post 검증을 해야함
            if(postValueValidation()) {
                AppCustomDialog(
                    "사진 등록을 완료하시겠습니까?"
                ) {
                    //의상 & 게시물 등록
                    binding.progressBar.visibility = View.VISIBLE
                    uploadPostAndClothes()
                }.show(parentFragmentManager, "submit_confirm_dialog")
            }
        }
    }

    private fun postValueValidation(): Boolean {
        val gender = viewModel.selectedGender
        val tpos = viewModel.selectedTpos
        val seasons = viewModel.selectedSeasons
        val styles = viewModel.selectedStyles
        val height = binding.filterSpace.heightValue.text.toString()
        val weight = binding.filterSpace.weightValue.text.toString()
        val introduce = binding.introduceValue.text.toString()

        //비어있는 값 없고, cloth 등록하다만 기록 없을 때
        if (gender.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "성별을 선택해주세요.", Toast.LENGTH_SHORT).show()
            return false
        }

        if (tpos.isEmpty() ||
            seasons.isEmpty() ||
            styles.isEmpty() ||
            height.isEmpty() ||
            weight.isEmpty() ||
            introduce.isEmpty()
        ) {
            Toast.makeText(requireContext(), "값을 모두 입력해주세요.", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun uploadPostAndClothes() {
        CoroutineScope(Dispatchers.IO).launch {
            val requestUpload: Deferred<Boolean> = CoroutineScope(Dispatchers.IO).async {
                //의상 이미지 업로드부터 진행 , response 받은 url 을 새로운 list 로 담아 viewModel 에 보관.
                //mapNotNull -> 요소가 null 이면 건너뜀
                val clothesImages = addClothesAdapter.currentList.mapNotNull {
                    absolutelyPath(
                        Uri.parse(it.imageUrl),
                        requireContext()
                    )
                }
                val clothesImageResponse = viewModel.uploadClothImages(clothesImages)
                if (clothesImageResponse.success && clothesImageResponse.imgUrls != null) {
                    val newList =
                        addClothesAdapter.currentList.mapIndexed { index, cloth ->
                            cloth.copy(imageUrl = clothesImageResponse.imgUrls[index])
                        }
                    // Log.e(TAG, "submitBtnOnclick: ImageList $clothesImages")
                    // Log.e(TAG, "submitBtnOnclick: newList $newList")

                    viewModel.uploadedClothes = newList.toMutableList()
                }

                //post 이미지 업로드 진행 -> 성공시 post 객체에 의상 list 담아서 전달
                val selectedPostImages = viewModel.selectedPostImages
                val postImagePathList = mutableListOf<String>()
                selectedPostImages.forEach {
                    absolutelyPath(Uri.parse(it), requireContext())?.let { path ->
                        postImagePathList.add(path)
                    }
                }
                val imageUploadResponse = viewModel.uploadPostImages(postImagePathList)
                if (imageUploadResponse.success) {
                    val tpos = viewModel.selectedTpos.distinct()
                    val seasons = viewModel.selectedSeasons.distinct()
                    val styles = viewModel.selectedStyles.distinct()
                    val height =
                        binding.filterSpace.heightValue.text.toString().replace(" cm", "").toInt()
                    val weight =
                        binding.filterSpace.weightValue.text.toString().replace(" kg", "").toInt()
                    val isPublic = !(binding.notPostNow.isChecked)
                    val uploadPost = UploadPost(
                        imageUploadResponse.imgUrls!!,
                        binding.introduceValue.text.toString(),
                        viewModel.selectedGender!!,
                        tpos,
                        seasons,
                        styles,
                        clothes = viewModel.uploadedClothes,
                        height,
                        weight,
                        isPublic
                    )
                    Log.e(TAG, "uploadPost: $uploadPost")
                    val postUploadResponse = viewModel.uploadPostInfo(uploadPost)

                    return@async postUploadResponse.success
                }
                return@async false
            }
            withContext(Dispatchers.Main) {
                val uploadResponse = runBlocking { requestUpload.await() }
                binding.progressBar.visibility = View.GONE
                if (uploadResponse) {
                    Toast.makeText(
                        requireContext(),
                        "게시물이 등록되었습니다.",
                        Toast.LENGTH_SHORT
                    ).show()

                    //findNavController 로 mypage 프래그먼트 이동시 backStack 문제로 photo menu 접근이 불가,
                    //메뉴탭 수동으로 이동시키고 backStack 제거하여 viewModel 과 edittext data clear
                    //findNavController().popBackStack(R.id.navigation_photo, true)
                    navigateToMyPage(R.id.navigation_photo)
                } else {
                    Toast.makeText(requireContext(), "사진의 용량이 허용 크기를 초과하였습니다.", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback);

    }

    private val onBackPressedCallback: OnBackPressedCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                backStackDialogShow()
            }
        }


}