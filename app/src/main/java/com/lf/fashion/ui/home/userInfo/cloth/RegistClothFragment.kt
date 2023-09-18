package com.lf.fashion.ui.home.userInfo.cloth

import android.Manifest
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
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
import com.lf.fashion.data.model.UploadCloth
import com.lf.fashion.databinding.HomeBRegistClothFragmentBinding
import com.lf.fashion.ui.AddPostClothesRvAdapter
import com.lf.fashion.ui.absolutelyPath
import com.lf.fashion.ui.addPost.ImagePickerFragment
import com.lf.fashion.ui.addTextLengthCounter
import com.lf.fashion.ui.cancelBtnBackStack
import com.lf.fashion.ui.showPermissionDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlin.properties.Delegates

/**
 * 이 의상은 어때? 내부 + 버튼 클릭시 노출되는 의상 등록 fragment 입니다
 * **/
//TODO: 업데이트 안내 코드 추가

@AndroidEntryPoint
class RegistClothFragment : Fragment(R.layout.home_b_regist_cloth_fragment), View.OnClickListener {

    private lateinit var binding: HomeBRegistClothFragmentBinding
    private val regClothesList = mutableListOf<UploadCloth>()

    // private var selectedCategory: String? = null
    private val addClothesAdapter = AddPostClothesRvAdapter()
    private var selectedImageUri: String? = null
    private val viewModel: UploadClothesViewModel by hiltNavGraphViewModels(R.id.registClothFragment)
    private var clothesPostId by Delegates.notNull<Int>()

    //복수의 권한이 필요한 경우 RequestMultiplePermissions() 후 launch(배열) 로 전달
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val allPermissionsGranted = permissions.all { it.value }
            val galleryPermission = permissions[Manifest.permission.READ_EXTERNAL_STORAGE] ?: false
            //모두 허용 또는 외부저장소 읽기 권한 있을 시 커스텀 갤러리 뷰로 이동
            if (allPermissionsGranted || galleryPermission) {
                //모든 이미지타입
                // requestImageUriLauncher.launch("image/*") // 여기서 요청할경우 권한 동의 후 바로 파일접근으로 넘어갈 수 있다.
                findNavController().navigate(
                    R.id.action_registClothFragment_to_imagePickerFragment,
                    bundleOf("from" to "RegistClothFragment", "limit" to 1)
                )
            } else {
                Log.d(TAG, "PhotoFragment - : granted fail")
            }
        }

    private val permissions = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainActivity.hideNavi(true)
    }

    override fun onResume() {
        MainActivity.hideNavi(true)
        viewModel.selectedCategory?.let {
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = HomeBRegistClothFragmentBinding.bind(view)
        clothesPostId = arguments?.getInt("clothesPostId") ?: return

        //이미지 선택해서 받아온 이미지들 ..
        setFragmentResultListener(ImagePickerFragment.REQUEST_KEY) { _, bundle ->
            val imageUris = bundle.get("imageURI") as Array<*>
            imageUris[0]?.let {
                selectedImageUri = imageUris[0].toString()
                Glide.with(binding.root)
                    .load(it)
                    .into(binding.clothRegistForm.productImage)
            }
        }


        binding.clothesDetailRv.adapter = addClothesAdapter
        registerBtnValidation()
        addTextLengthCounter(binding.detailValue, binding.textCounter, 50)
        imageOnclickPermissionCheck() // 이미지 부분 눌리면 permission 체크 -> 허용시엔 imagePickerFragment 로 이동
        cancelBtnBackStack(binding.cancelBtn)
        submitClothes() // 이미지 등록 버튼 클릭
    }

    private fun imageOnclickPermissionCheck() {
        binding.clothRegistForm.productImage.setOnClickListener {
            when {
                ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) -> {
                    //권한을 deny 한 적이 있고 다시 기능을 이용하려고 시도할 때, 안내 문구를 띄워주기
                    showPermissionDialog(requestPermissionLauncher, permissions)
                }
                // 권한을 아직 허용한 적이 없고, 안내문구를 보내야하는 시점도 아닐 경우
                else -> {
                    requestPermissionLauncher.launch(permissions)
                }
            }
        }
    }

    private fun registerBtnValidation() {
        binding.clothRegistForm.topLinear.children.forEach { it.setOnClickListener(this) }
        binding.regClothBtn.setOnClickListener {
            val nameValue = binding.clothRegistForm.nameValue.text.toString()
            val priceValue = binding.clothRegistForm.priceValue.text.toString()
            val colorValue = binding.clothRegistForm.colorValue.text.toString()
            val sizeValue = binding.clothRegistForm.sizeValue.text.toString()
            val brandValue = binding.clothRegistForm.brandValue.text.toString()

            if (nameValue.isNotEmpty() && priceValue.isNotEmpty() &&
                colorValue.isNotEmpty() && sizeValue.isNotEmpty() &&
                viewModel.selectedCategory != null && selectedImageUri != null
            ) {

                regClothesList.add(
                    UploadCloth(
                        nameValue,
                        viewModel.selectedCategory!!,
                        selectedImageUri!!,
                        priceValue.toInt(),
                        colorValue,
                        sizeValue,
                        brandValue,
                        binding.detailValue.text.toString()
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
                binding.clothRegistForm.nameValue.text.clear()
                binding.clothRegistForm.priceValue.text.clear()
                binding.clothRegistForm.colorValue.text.clear()
                binding.clothRegistForm.sizeValue.text.clear()
                binding.clothRegistForm.brandValue.text.clear()
                binding.detailValue.text.clear()

            } else if (viewModel.selectedCategory == null) {
                Toast.makeText(requireContext(), "의상 카테고리를 선택해주세요!", Toast.LENGTH_SHORT).show()

            } else {
                Toast.makeText(requireContext(), "의상에 관한 정보를 모두 입력해주세요!", Toast.LENGTH_SHORT).show()
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
                viewModel.selectedCategory = button.text.toString()
            }
        }
    }

    private fun submitClothes() {
        binding.submitBtn.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            val currentList = addClothesAdapter.currentList
            //list 에서 1개씩 등록.
            currentList.forEach {
                CoroutineScope(Dispatchers.IO).launch {

                    val imagePath = absolutelyPath(Uri.parse(it.imageUrl), requireContext())
                    Log.e(TAG, "submitClothes: ${it.imageUrl}")
                    val imageResponse = viewModel.uploadClothesImage(imagePath!!)

                    if (imageResponse.success) {
                        val uploadedImageUrl = imageResponse.imgUrls!![0]
                        it.imageUrl = uploadedImageUrl

                        val infoResponse = viewModel.uploadClothesInfo(clothesPostId, it)
                        if (infoResponse.success) {
                            withContext(Dispatchers.Main) {
                                binding.progressBar.visibility = View.GONE
                                Toast.makeText(
                                    requireContext(),
                                    "의상 등록이 완료되었습니다.",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                findNavController().apply {
                                    navigate(R.id.action_registClothFragment_to_recommendFragment,
                                        bundleOf("postId" to clothesPostId,"backStackClear" to true)
                                    )
                                }
                            }
                        }else{
                            withContext(Dispatchers.Main) {
                                binding.progressBar.visibility = View.GONE
                                Toast.makeText(
                                    requireContext(),
                                    "의상 등록 오류",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()

                            }
                        }
                    }
                }

            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        MainActivity.hideNavi(false)
    }


}