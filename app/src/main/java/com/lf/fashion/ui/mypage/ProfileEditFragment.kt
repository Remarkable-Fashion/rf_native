package com.lf.fashion.ui.mypage

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import com.kakao.sdk.user.UserApiClient
import com.lf.fashion.MainActivity
import com.lf.fashion.R
import com.lf.fashion.TAG
import com.lf.fashion.data.network.Resource
import com.lf.fashion.data.model.MyInfo
import com.lf.fashion.databinding.MypageProfileFragmentBinding
import com.lf.fashion.ui.addPost.ImagePickerFragment
import com.lf.fashion.ui.common.absolutelyPath
import com.lf.fashion.ui.common.addTextChangeListener
import com.lf.fashion.ui.common.addTextLengthCounter
import com.lf.fashion.ui.common.addUnitTextListener
import com.lf.fashion.ui.common.cancelBtnBackStack
import com.lf.fashion.ui.common.handleApiError
import com.lf.fashion.ui.common.hideKeyboard
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileEditFragment : Fragment(R.layout.mypage_profile_fragment) {
    private lateinit var binding: MypageProfileFragmentBinding
    private val viewModel: MyPageViewModel by hiltNavGraphViewModels(R.id.navigation_mypage)
    private lateinit var nameValue: EditText
    private lateinit var heightValue: EditText
    private lateinit var weightValue: EditText
    private lateinit var introduceValue: EditText
    private var updatedSex: String? = null
    private var selectedImageUri: String? = null
    private var lastProfileRequest: String? = null // 마지막으로 요청한 값 저장 -> viewModel을 공유하고있어서 이전 success 값이 남아있기때문에 페이지 생성시마다 초기화되는 변수를 생성해둠
    private lateinit var myInfo: MyInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainActivity.hideNavi(true)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = MypageProfileFragmentBinding.bind(view)

        val fragmentNames = findNavController().backQueue.mapNotNull { navBackStackEntry ->
            val destination = navBackStackEntry.destination
            destination.label?.toString() // 프래그먼트의 이름을 가져옴
        }
        Log.e(TAG, "profile edit onViewCreated: $fragmentNames")

        cancelBtnBackStack(binding.backBtn)

        myInfo = arguments?.get("myInfo") as MyInfo
        binding.myInfo = myInfo

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
            when (resources) {
                is Resource.Success -> {
                    if (resources.value.success) {
                        Toast.makeText(requireContext(), "프로필 수정이 완료되었습니다.", Toast.LENGTH_SHORT)
                            .show()
                        viewModel.myInfoChaged = true
                        Log.e(TAG, "profile response: ${resources.value}", )
                        findNavController().navigateUp()
                    } else {
                        // success가 false인 경우 처리
                        Log.e(TAG, "RESPONSE onViewCreated: ${resources.value}")
                        Toast.makeText(requireContext(), "오류가 발생하였습니다.", Toast.LENGTH_SHORT).show()
                    }
                }

                is Resource.Failure -> {
                    Toast.makeText(requireContext(), "네트워크 오류 발생 ", Toast.LENGTH_SHORT).show()
                    handleApiError(resources)
                }

                else -> {}
            }

            lastProfileRequest = null // 처리가 완료되었으므로 마지막 요청 초기화
        }

        binding.topLayout.setOnTouchListener { v, event ->
            hideKeyboard()
            v.performClick()
        }
    }


    private fun submitProfileInfo() {
        binding.submitBtn.setOnClickListener {
            if (nameValue.text.isNotBlank()) {

                val weight = weightValue.text.toString()
                val weightNum = if (weight.isNotEmpty()) weight.replace(" kg", "").toInt() else null
                val height = heightValue.text.toString()
                val heightNum = if (height.isNotEmpty()) height.replace(" cm", "").toInt() else null
                //닉네임 변경시 업데이트
                val newName = if (myInfo.name != nameValue.text.toString()) {
                    nameValue.text.toString()
                } else null

                var imagePath: String? = null
                selectedImageUri?.let {
                    imagePath = absolutelyPath(Uri.parse(selectedImageUri), requireContext())
                }

                // 등록 api 연결
                viewModel.updateMyProfile(
                    imagePath,
                    updatedSex,
                    heightNum,
                    weightNum,
                    newName,
                    introduceValue.text.toString()
                )


                // 마지막으로 요청한 값을 설정 _ 그냥 boolean 여부로 해도됨 해당 값 사용 x
                lastProfileRequest = "${selectedImageUri ?: ""}, $updatedSex, $height, $weight, ${introduceValue.text}"
            } else {
                //닉네임이 빈값일 경우 최상단으로 스크롤 이동, 닉네임에 포커스
                val nestedScrollView = binding.nestedScrollView
                nestedScrollView.smoothScrollTo(0, 0) // (0, 0) 위치로 스무스하게 스크롤
                binding.nameValue.requestFocus()
                binding.nicknameWarning.isVisible = true
                Toast.makeText(requireContext(), "닉네임을 입력해주세요", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun getMyEmailInfo() {
        UserApiClient.instance.me { user, error ->
            if (error != null) {
            } else if (user != null) {
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
                binding.submitBtn.isSelected = true
                if(binding.nameValue.text.isNotEmpty()){
                    binding.nicknameWarning.isVisible = false
                }
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
            if (imageUris.isNotEmpty()) {
                imageUris[0]?.let {
                    selectedImageUri = imageUris[0].toString()
                    binding.myInfo?.profile?.profileImage = it as String
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        MainActivity.hideNavi(false)
    }
}
