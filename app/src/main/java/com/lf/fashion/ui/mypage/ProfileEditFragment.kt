package com.lf.fashion.ui.mypage

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.kakao.sdk.user.UserApiClient
import com.lf.fashion.R
import com.lf.fashion.TAG
import com.lf.fashion.data.response.MyInfo
import com.lf.fashion.data.response.UpdateMyInfo
import com.lf.fashion.databinding.MypageProfileFragmentBinding
import com.lf.fashion.ui.*
import com.lf.fashion.ui.addPost.ImagePickerFragment
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class ProfileEditFragment : Fragment() {
    private lateinit var binding: MypageProfileFragmentBinding
    private val viewModel : MyPageViewModel by hiltNavGraphViewModels(R.id.navigation_mypage)
    private lateinit var nameValue :EditText
    private lateinit var heightValue: EditText
    private lateinit var weightValue : EditText
    private lateinit var introduceValue : EditText
    private var updatedSex :String? =null
    private var selectedImageUri :String? = null

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

        introduceValue = binding.introduceValue
        heightValue = binding.heightValue
        weightValue = binding.weightValue
        nameValue  = binding.nameValue

        getMyEmailInfo() // 이메일 정보 바인딩
        genderListener(myInfo) // 성별 선택 택 1 제한
        textListenerSetting(myInfo)
        onclickProfileImage()


        binding.submitBtn.setOnClickListener {
            if (nameValue.text.isNotBlank()) {
                val profileImageFile :File? = if(selectedImageUri != null) File(selectedImageUri!!) else null
                val weight = weightValue.text.toString().replace("kg", "").toInt()
                val height = heightValue.text.toString().replace("cm", "").toInt()
                UpdateMyInfo(profileImageFile,updatedSex ,height,weight,introduceValue.text.toString())

                // 등록 api 연결

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

        updatedSex = if(Male) "Male" else "Female"
    }

    private fun textListenerSetting(myInfo: MyInfo) {
        addTextLengthCounter(introduceValue, binding.textCounter, 50)
        addUnitTextListener(heightValue, height = true)
        addUnitTextListener(weightValue, height = false)

        // 값 변경시 완료 버튼 활성화
        addTextChangeListener(
            listOf(
                nameValue,
                //  binding.phoneValue,
                heightValue,
                weightValue
            ), myInfo
        ) { changed ->
            if (changed) {
                val heightChange =
                    heightValue.text.toString() != (myInfo.profile.height ?: "")
                val weightChange =
                    weightValue.text.toString() != (myInfo.profile.weight ?: "")
                val introduceChange =
                    introduceValue.text.toString() != (myInfo.profile.introduction ?: "")
                val nameChange = nameValue.text.toString() != myInfo.name
                /*  Log.d(
                    TAG,
                    "ProfileEditFragment - onViewCreated: $heightChange , $weightChange , $introduceChange , $nameChange"
                );
                Log.d(
                    TAG,
                    "ProfileEditFragment - onViewCreated: ${heightValue.text}"
                );
                Log.d(
                    TAG,
                    "ProfileEditFragment - onViewCreated: ${myInfo.profile.weight.toString()}"
                );*/

                if (heightChange || weightChange || introduceChange || nameChange) {
                    binding.submitBtn.isSelected = changed
                }
            }
        }
    }

    private fun onclickProfileImage(){
        binding.profileImage.setOnClickListener {
            findNavController().navigate(R.id.action_profileEditFragment_to_imagePickerFragment,
                bundleOf("from" to "ProfileEditFragment" , "limit" to 1))
        }

        setFragmentResultListener(requestKey = ImagePickerFragment.REQUEST_KEY){
                _, bundle ->
            val imageUris = bundle.get("imageURI") as Array<*>
            imageUris[0]?.let {
                selectedImageUri = imageUris[0].toString()
                Glide.with(binding.root)
                    .load(it)
                    .into(binding.profileImage)
            }
        }
    }
}
