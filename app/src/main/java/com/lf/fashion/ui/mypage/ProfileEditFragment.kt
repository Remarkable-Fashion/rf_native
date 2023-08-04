package com.lf.fashion.ui.mypage

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import com.kakao.sdk.user.UserApiClient
import com.lf.fashion.R
import com.lf.fashion.TAG
import com.lf.fashion.data.network.Resource
import com.lf.fashion.data.response.MyInfo
import com.lf.fashion.databinding.MypageProfileFragmentBinding
import com.lf.fashion.ui.*
import com.lf.fashion.ui.addPost.ImagePickerFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileEditFragment : Fragment() {
    private lateinit var binding: MypageProfileFragmentBinding
    private val viewModel: MyPageViewModel by hiltNavGraphViewModels(R.id.navigation_mypage)
    private lateinit var nameValue: EditText
    private lateinit var heightValue: EditText
    private lateinit var weightValue: EditText
    private lateinit var introduceValue: EditText
    private var updatedSex: String? = null
    private var selectedImageUri: String? = null
    var lastProfileRequest: String? = null // 마지막으로 요청한 값 저장

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MypageProfileFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cancelBtnBackStack(binding.backBtn)

        val myInfo = arguments?.get("myInfo") as MyInfo
        binding.myInfo = myInfo

        Log.d(TAG, "ProfileEditFragment - onViewCreated: ${myInfo.profile.profileImage}");
        introduceValue = binding.introduceValue
        heightValue = binding.heightValue
        weightValue = binding.weightValue
        nameValue = binding.nameValue

        getMyEmailInfo() // 이메일 정보 바인딩
        genderListener(myInfo) // 성별 선택 택 1 제한
        onclickProfileImage()
        textListenerSetting(myInfo)
        submitProfileInfo()

        viewModel.updateProfileResponse.observe(viewLifecycleOwner) { resources ->
            if (lastProfileRequest == null) {
                return@observe // 아직 요청이 없는 경우, 처리하지 않음
            }

            if (resources is Resource.Success && resources.value.success) {
                Toast.makeText(requireContext(), "프로필 수정이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()

            } else if (resources is Resource.Failure) {
                Toast.makeText(requireContext(), "오류 발생 ", Toast.LENGTH_SHORT).show()
                handleApiError(resources)

            } else if (resources is Resource.Success) {
                Log.d(TAG, "ProfileEditFragment - onViewCreated: ${resources.value}");
            }

            lastProfileRequest = null // 처리가 완료되었으므로 마지막 요청 초기화
        }
    }


    private fun submitProfileInfo() {
        binding.submitBtn.setOnClickListener {
            if (nameValue.text.isNotBlank()) {

                val weight = weightValue.text.toString().replace(" kg", "")
                val height = heightValue.text.toString().replace(" cm", "")

                var imagePath :String ?= null
                selectedImageUri?.let{
                    imagePath = absolutelyPath(Uri.parse(selectedImageUri), requireContext())
                }
                // 등록 api 연결
                viewModel.updateMyProfile(
                    imagePath,
                    updatedSex,
                    height,
                    weight,
                    introduceValue.text.toString()

                )

                // 마지막으로 요청한 값을 설정 _ 그냥 boolean 여부로 해도됨 해당 값 사용 x
                lastProfileRequest = "${selectedImageUri ?: ""}, $updatedSex, $height, $weight, ${introduceValue.text}"
            }
        }
    }


    private fun getMyEmailInfo() {
        UserApiClient.instance.me { user, error ->
            if (error != null) {
                Log.d(TAG, "ProfileEditFragment - Kakao user instance error :  $error");
            } else if (user != null) {
                Log.d(TAG, "ProfileEditFragment - onViewCreated: ${user.kakaoAccount?.email}")
                binding.emailAccount.text = user.kakaoAccount?.email.toString()
            }
        }
    }


    private fun genderListener(myInfo: MyInfo) {
        if (myInfo.profile.sex == "Male") {
            genderBtnSelectUi(Male = true)
        } else {
            genderBtnSelectUi(Male = false)
        }

        binding.genderInclude.genderManBtn.setOnClickListener {
            genderBtnSelectUi(Male = true)
        }
        binding.genderInclude.genderWomanBtn.setOnClickListener {
            genderBtnSelectUi(Male = false)
        }
    }

    private fun genderBtnSelectUi(Male: Boolean) {
        val manBtn = binding.genderInclude.genderManBtn
        val womanBtn = binding.genderInclude.genderWomanBtn

        if (Male) {
            manBtn.isSelected = true
            womanBtn.isSelected = false
        } else {
            manBtn.isSelected = false
            womanBtn.isSelected = true
        }

        updatedSex = if (Male) "Male" else "Female"
    }

    private fun textListenerSetting(myInfo: MyInfo) {
        addTextLengthCounter(introduceValue, binding.textCounter, 50)
        addUnitTextListener(heightValue, height = true)
        addUnitTextListener(weightValue, height = false)

        // 값 변경시 완료 버튼 활성화
        addTextChangeListener(
            listOf(nameValue, heightValue, weightValue, introduceValue),
            mapOf(
                nameValue to myInfo.name,
                heightValue to myInfo.profile.height.toString() + " cm",
                weightValue to myInfo.profile.weight.toString() + " kg",
                introduceValue to myInfo.profile.introduction
            )
        ) { changed ->
            if (changed) {
                binding.submitBtn.isSelected = changed
            }
        }
    }

    private fun onclickProfileImage() {
        binding.profileImage.setOnClickListener {
            findNavController().navigate(
                R.id.action_profileEditFragment_to_imagePickerFragment,
                bundleOf("from" to "ProfileEditFragment", "limit" to 1)
            )
        }

        //ImagePickerFragment 에서 선택한 이미지를 바인딩하고 서버에 전송가능하도록 selectedImageUri 에 담아준다.
        setFragmentResultListener(requestKey = ImagePickerFragment.REQUEST_KEY) { _, bundle ->
            val imageUris = bundle.get("imageURI") as Array<*>
            imageUris[0]?.let {
                selectedImageUri = imageUris[0].toString()
                binding.myInfo?.profile?.profileImage = it as String
            }
        }
    }
}
