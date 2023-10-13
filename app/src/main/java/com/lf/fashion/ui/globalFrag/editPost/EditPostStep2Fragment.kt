package com.lf.fashion.ui.globalFrag.editPost

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.lf.fashion.MainActivity
import com.lf.fashion.R
import com.lf.fashion.TAG
import com.lf.fashion.data.model.Cloth
import com.lf.fashion.data.model.UploadPost
import com.lf.fashion.data.network.Resource
import com.lf.fashion.databinding.EditPostStep2FragmentBinding
import com.lf.fashion.ui.addPost.ImagePickerFragment
import com.lf.fashion.ui.common.AppCustomDialog
import com.lf.fashion.ui.common.absolutelyPath
import com.lf.fashion.ui.common.addTextLengthCounter
import com.lf.fashion.ui.common.addUnitTextListener
import com.lf.fashion.ui.common.childChip
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class EditPostStep2Fragment : Fragment(R.layout.edit_post_step2_fragment), View.OnClickListener {
    private val viewModel: EditPostViewModel by hiltNavGraphViewModels(R.id.navigation_eidt_post)
    private lateinit var binding: EditPostStep2FragmentBinding
    private val chipStyle = "default"
    private val addClothesAdapter = EditPostClothesRvAdapter()
    private var regClothesList = mutableListOf<Cloth>()

    //selectedClothImageUri 는 adpater 에 보내서 띄워주는 역할을 한다
    private var selectedClothImageUri: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainActivity.hideNavi(true)
        //기존 등록된 게시물 정보 viewModel 에 oncreate 최초 1 회 viewModel에 할당
        viewModel.postInfo.value.apply {
            when (this) {
                is Resource.Success -> {
                    val response = this.value
                    viewModel.apply {
                        selectedGender = response.gender
                        savedHeight = response.height
                        savedWeight = response.weight
                        selectedGender = response.gender
                        savedIntroduce = response.description
                        selectedTpos = response.tpos?.toMutableList() ?: mutableListOf()
                        selectedSeasons = response.seasons?.toMutableList() ?: mutableListOf()
                        selectedStyles = response.styles?.toMutableList() ?: mutableListOf()
                        savedClothList = response.clothes.toMutableList()
                        //newClothImageList = response.clothes.toMutableList()
                    }
                }

                else -> {

                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = EditPostStep2FragmentBinding.bind(view)
        binding.clothesDetailRv.adapter = addClothesAdapter
        addClothesAdapter.submitList(viewModel.savedClothList)
        regClothesList = addClothesAdapter.currentList.toMutableList()
        binding.clothRegistForm.topLinear.children.forEach { it.setOnClickListener(this) }
        binding.filterSpace.genderManBtn.setOnClickListener(this)
        binding.filterSpace.genderWomanBtn.setOnClickListener(this)
        chipSetting()

        editTextListener()  // height ,weight ,introduce 리스너 세팅 및 변한 값 viewModel에 할당
        imagePickerOpen()
        submitBtnOnclick()
        binding.backBtn.setOnClickListener {
            backStackDialogShow()
        }
        registerCloth()
    }

    override fun onResume() {
        super.onResume()
        //최초에는 오리지널값 바인딩 / 재진입시 수정된 값 바인딩
        viewModel.selectedGender?.let {
            if (it == "Male") {
                binding.filterSpace.genderManBtn.isSelected = true
            } else {
                binding.filterSpace.genderWomanBtn.isSelected = true
            }
        }
        binding.filterSpace.heightValue.setText("${viewModel.savedHeight} cm")
        binding.filterSpace.weightValue.setText("${viewModel.savedWeight} kg")
        binding.introduceValue.setText(viewModel.savedIntroduce)

    }

    private fun editTextListener() {
        addTextLengthCounter(binding.introduceValue, binding.textCounter, 50)//소개글 글자수 카운터
        addUnitTextListener(binding.filterSpace.heightValue, height = true) {
            if (it.isNotEmpty()) {
                viewModel.savedHeight = it.toInt()
            } else {
                viewModel.savedHeight = null
            }
        }
        addUnitTextListener(binding.filterSpace.weightValue, height = false) {
            if (it.isNotEmpty()) {
                viewModel.savedWeight = it.toInt()
            } else {
                viewModel.savedWeight = null
            }
        }
        binding.introduceValue.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                val currentValue = s?.toString() ?: ""
                if (currentValue != viewModel.savedIntroduce) {
                    viewModel.savedIntroduce = currentValue
                }
            }

        })
    }

    private fun chipSetting() {
        viewModel.tpoChipList.observe(viewLifecycleOwner) {
            it?.let {
                Log.e(TAG, "chipSetting tpo: $it")
                val tpoChipGroup = binding.filterSpace.filterInclude.tpoChipGroup
                childChip(
                    it,
                    tpoChipGroup,
                    chipStyle,
                    editPostViewModel = viewModel
                ) { chipInfo, isChecked ->
                    if (isChecked) {
                        if (!viewModel.selectedTpos.any { it.text == chipInfo.text }) {
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
                Log.e(TAG, "chipSetting season: $it")
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
                }
            }
        }
        viewModel.styleChipList.observe(viewLifecycleOwner) {
            it?.let {
                Log.e(TAG, "chipSetting style: $it")

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
                }
            }
        }
    }

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
                val newCloth = Cloth(
                    null,
                    name.toString(),
                    viewModel.selectedClothCategory!!,//viewModel.selectedCategory!!,
                    selectedClothImageUri!!,
                    price.toString().toInt(),
                    color.toString(),
                    size.toString(),
                    brand.toString(),
                    null, null
                )

                regClothesList.add(
                    newCloth
                )
                viewModel.newClothImageList.add(newCloth)
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
                R.id.action_editPostStep2Fragment_to_imagePickerFragment,
                bundleOf("from" to "EditPostStep2Fragment", "limit" to 1)
            )
        }
        setFragmentResultListener(requestKey = ImagePickerFragment.REQUEST_KEY) { _, bundle ->
            //의상 등록 부분에서 이미지 받아온 것
            val imageUris = bundle.get("imageURI") as Array<*>
            if (imageUris.isNotEmpty()) {
                imageUris[0]?.let {
                    selectedClothImageUri = imageUris[0].toString()
                    Glide.with(binding.root)
                        .load(it)
                        .into(binding.clothRegistForm.productImage)
                }
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

    private fun clothValueValidation() {
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
            ) {
                if (postValueValidation()) {
                    //의상 & 게시물 등록
                    binding.progressBar.visibility = View.VISIBLE
                    uploadPostAndClothes()
                }
            }.show(parentFragmentManager, "alert_info_clear")

        } else { //작성하다만 텍스트는 없지만 post 검증을 해야함
            if (postValueValidation()) {
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
        binding.filterSpace.heightValue.clearFocus()
        binding.filterSpace.weightValue.clearFocus()
        //todo test !!!
        CoroutineScope(Dispatchers.IO).launch {
            val requestUpload: Deferred<Boolean> = CoroutineScope(Dispatchers.IO).async {
                val clothes = viewModel.savedClothList.toMutableList() //기존 clothes를 새로운 list로 추출

                //기존 등록된 의상 이미지가 아닌 새로 서버에 업로드 해야할 이미지만 담은 newClothImageList의 절대경로 추출
                val clothesImages = viewModel.newClothImageList.mapNotNull {
                    absolutelyPath(
                        Uri.parse(it.imageUrl),
                        requireContext()
                    )
                }
                if(clothesImages.isNotEmpty()) {
                    val clothesImageResponse = viewModel.uploadNewClothImage(clothesImages)
                    if (clothesImageResponse.success && clothesImageResponse.imgUrls != null) {
                        //cloth의 이미지 url을 서버에 업로드된 이미지 주소로 바꿔주기
                        val newList =
                            viewModel.newClothImageList.mapIndexed { index, cloth ->
                                Log.e(TAG, "uploadPostAndClothes: $index , $cloth , $clothesImageResponse")
                                cloth.copy(imageUrl = clothesImageResponse.imgUrls[index])
                            }
                        // Log.e(TAG, "submitBtnOnclick: ImageList $clothesImages")
                        // Log.e(TAG, "submitBtnOnclick: newList $newList")
                        clothes.addAll(newList) // 기존 clothes 에 새로운 cloth 더하기
                    }
                }

                viewModel.uploadedClothes = clothes

                //새로 등록한 post 이미지 업로드 진행
                //val postImagePathList = mutableListOf<String>()
                val postImagePathList = viewModel.newImageList.mapNotNull {
                    absolutelyPath(Uri.parse(it.url), requireContext())
                    }
                val tpos = viewModel.selectedTpos.distinct().map { it.id }
                val seasons = viewModel.selectedSeasons.distinct().map { it.id }
                val styles = viewModel.selectedStyles.distinct().map { it.id }
                val height =
                    binding.filterSpace.heightValue.text.toString().replace(" cm", "").toInt()
                val weight =
                    binding.filterSpace.weightValue.text.toString().replace(" kg", "").toInt()
                val isPublic = !(binding.notPostNow.isChecked)
                val originalPostImage = viewModel.imageList.value

                if(postImagePathList.isNotEmpty()){
                    val imageUploadResponse = viewModel.uploadNewPostImage(postImagePathList)
                    originalPostImage?.removeAll(viewModel.newImageList)
                    //새로운 이미지 + 기존 이미지 모두 담은 viewModel imageList 를 upload !
                    if (imageUploadResponse.success && originalPostImage != null) {
                        // List<ImageUrl> -> List<String>
                        val newPostList = originalPostImage.map { it.url }.toMutableList()
                        newPostList.addAll(imageUploadResponse.imgUrls!!)

                        val uploadPost = UploadPost(
                            newPostList,
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
                        viewModel.postId?.let { id ->
                            val postUploadResponse = viewModel.editPost(id, uploadPost)
                            return@async postUploadResponse.success
                        }
                    }

                }else if(originalPostImage!=null){
                    // List<ImageUrl> -> List<String>
                    val uploadPost = UploadPost(
                        originalPostImage.map { it.url }.toMutableList(),
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
                    viewModel.postId?.let { id ->
                        val postUploadResponse = viewModel.editPost(id, uploadPost)
                        return@async postUploadResponse.success
                    }
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
                    //findNavController().popBackStack(R.id.navigation_photo, true)
                    findNavController().navigate(R.id.navigation_mypage)
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

    private fun backStackDialogShow() {
        AppCustomDialog(
            "이전 화면으로 이동하겠습니까?\n" +
                    "등록 중인 내용은 초기화됩니다.",
                    null,
            "확인",
            "닫기", null
        ) {
            findNavController().popBackStack()
        }.show(parentFragmentManager, "photoStep2_dialog")
    }

    override fun onDestroy() {
        super.onDestroy()
        MainActivity.hideNavi(false)
    }
}