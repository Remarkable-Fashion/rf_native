package com.lf.fashion.ui.home.frag

import android.Manifest
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.lf.fashion.R
import com.lf.fashion.TAG
import com.lf.fashion.data.response.RegClothes
import com.lf.fashion.databinding.HomeBRegistClothFragmentBinding
import com.lf.fashion.ui.AddPostClothesRvAdapter
import com.lf.fashion.ui.addPost.ImagePickerFragment
import com.lf.fashion.ui.cancelBtnBackStack
import com.lf.fashion.ui.showPermissionDialog

/**
 * 이 의상은 어때? 내부 + 버튼 클릭시 노출되는 의상 등록 fragment 입니다
 * **/
//TODO: 업데이트 안내 코드 추가 , 의상등록 이미지 클릭 -> 이미지피커프래그먼트 연결
class RegistClothFragment : Fragment(), View.OnClickListener {
    private lateinit var binding: HomeBRegistClothFragmentBinding
    private val regClothesList = mutableListOf<RegClothes>()
    private var selectedCategory: String? = null
    private val addClothesAdapter = AddPostClothesRvAdapter()
    private var selectedImageUri :String? = null
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
                    bundleOf("from" to "RegistClothFragment")
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = HomeBRegistClothFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //이미지 등록에서 받아온 이미지들 ..
        setFragmentResultListener(ImagePickerFragment.REQUEST_KEY){ requestKey, bundle ->
          //  Log.d(TAG, "PhotoStep2Fragment - onViewCreated: ${bundle.get("imageURI")}");
            val imageUris = bundle.get("imageURI") as Array<*>
            imageUris[0]?.let {
                selectedImageUri = imageUris[0].toString()
                Glide.with(binding.root)
                    .load(it)
                    .into(binding.clothRegistForm.productImage)
            }
        }


        binding.clothesDetailRv.adapter = addClothesAdapter
        registerCloth()
        detailValueLengthCounting()
        imageOnclickPermissionCheck() // 이미지 부분 눌리면 permission 체크 -> 허용시엔 imagePickerFragment 로 이동
        cancelBtnBackStack(binding.cancelBtn)
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

    private fun registerCloth() {
        binding.clothRegistForm.topLinear.children.forEach { it.setOnClickListener(this) }
        binding.regClothBtn.setOnClickListener {
            val nameValue = binding.clothRegistForm.nameValue.text.toString()
            val priceValue = binding.clothRegistForm.priceValue.text.toString()
            val colorValue = binding.clothRegistForm.colorValue.text.toString()
            val sizeValue = binding.clothRegistForm.sizeValue.text.toString()
            val urlValue = binding.clothRegistForm.brandValue.text.toString()

            if (nameValue.isNotEmpty() && priceValue.isNotEmpty() && colorValue.isNotEmpty() && sizeValue.isNotEmpty() && selectedCategory != null) {

                regClothesList.add(
                    RegClothes(
                        selectedImageUri,
                        selectedCategory!!,
                        nameValue,
                        priceValue,
                        colorValue,
                        sizeValue,
                        urlValue
                    )
                )
                addClothesAdapter.apply {
                    submitList(regClothesList)
                    notifyItemInserted(regClothesList.size - 1)
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

    private fun detailValueLengthCounting() {
        binding.detailValue.addTextChangedListener(object : TextWatcher {
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


}