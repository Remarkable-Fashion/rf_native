package com.lf.fashion.ui.addPost

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.lf.fashion.MainNaviDirections
import com.lf.fashion.R
import com.lf.fashion.TAG
import com.lf.fashion.data.common.UserDataStorePref
import com.lf.fashion.data.model.ImageItem
import com.lf.fashion.databinding.PhotoImagePickerFragmentBinding
import com.lf.fashion.ui.common.AppCustomDialog
import com.lf.fashion.ui.addPost.adapter.CheckedImageAdapter
import com.lf.fashion.ui.addPost.adapter.ImageAdapter
import com.lf.fashion.ui.home.GridSpaceItemDecoration
import com.lf.fashion.ui.common.showRequireLoginDialog
import dagger.hilt.android.AndroidEntryPoint

/**
 * 하단 네비게이션 바텀 바에서 사진 추가 아이콘을 클릭 시 노출되는 커스텀 갤러리 프래그먼트입니다
 *
 * TODO ? : viewModel init 에서 imageItem List 를 불러옴
 * 앱을 중단하고 카메라로 사진을 찍고 돌아와도 앱을 종료후 다시 시작하지 않는 이상 새로운 이미지가 뜨지 않는 문제 발생가능
 */
@AndroidEntryPoint
class ImagePickerFragment : Fragment(), GalleryRvListener,
    CheckedImageRVListener {
    private lateinit var binding: PhotoImagePickerFragmentBinding
    private val viewModel: ImagePickerViewModel by viewModels {
        ImagePickerViewModelFactory(requireContext())
    }
    private lateinit var userPref : UserDataStorePref

    private val checkedImageAdapter =CheckedImageAdapter(this@ImagePickerFragment)
    companion object{
        const val REQUEST_KEY ="REGISTER_CLOTH_IMAGE"
    }
    //권한 런처 초기화
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val allPermissionsGranted = permissions.all { it.value }
            if (allPermissionsGranted) {
                // 권한 부여시 카메라로 intent
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                takePictureLauncher.launch(intent)
            } else {
                //앱 설정으로 이동 intent
                applicationSettingIntent()
            }
        }

    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result != null) {
                // 이미지를 캡처한 후의 처리 로직
                val extras = result.data?.extras
                val bitmap = extras?.get("data") as Bitmap
                val saveImage = SaveImage(requireContext())
                //이미지 저장하고 uri 얻기 -> viewModel 의 imageItem 에 추가
                val imageUri = saveImage.saveImageToGallery(bitmap)
                viewModel.addImageToImageList(ImageItem(imageUri, false, ""))

            } else {
                // 이미지 캡처가 실패하거나 사용자가 취소한 경우의 처리 로직
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = PhotoImagePickerFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userPref = UserDataStorePref(requireContext())
        if (!userPref.loginCheck()) {
            showRequireLoginDialog()
            return
        }

        val backStackFragment = arguments?.get("from")
        val imageLimit = arguments?.get("limit") as Int


        //submit(등록) 버튼 클릭시 편집 fragment 로 이동
        binding.buttonListener = View.OnClickListener {
            // Pass Uri list to fragment outside
            Log.d(TAG, "ImagePickerFragment - onCreateView: ${viewModel.getCheckedImageUriList()}");
            val imageUriArray = viewModel.getCheckedImageUriList().toTypedArray()
            when (backStackFragment) {
                "PhotoFragment" ->{ //PhotoFragment -> ImagePicker 일땐 backstack 이 아니라 PhotoStep2로 가야하기 때문에 분리..
                val action =  MainNaviDirections.actionGlobalToPhotoStep2Fragment(imageUriArray)
                    findNavController().navigate(action)
                }
                else -> {
                    /*val action =  ImagePickerFragmentDirections.actionImagePickerFragmentToPhotoStep2Fragment(imageUriArray)
                    findNavController().navigate(action)*/
                    setFragmentResult(REQUEST_KEY, bundleOf("imageURI" to imageUriArray))
                    findNavController().navigateUp()

                }
            }
        }

        galleryImageRvSetting(imageLimit)

        checkedImageRvSetting()

    }

    private fun galleryImageRvSetting(imageLimit : Int) {
        val galleryImageAdapter = ImageAdapter(viewModel, this@ImagePickerFragment, imageLimit)
        //binding.recyclerviewImage.adapter = galleryImageAdapter
        with(binding.recyclerviewImage){
            layoutManager = StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL)
            adapter = galleryImageAdapter.apply {
                while (itemDecorationCount > 0) { // 기존 추가한 itemDecoration 을 모두 지워주지않으면 점점 쌓인다.
                    removeItemDecorationAt(0)
                }
                addItemDecoration(GridSpaceItemDecoration(3, 6)) }
        }
        viewModel.imageItemList.observe(viewLifecycleOwner) { imageItemList ->
            Log.d(TAG, "ImagePickerFragment - galleryImageRvSetting:  view model response");
            galleryImageAdapter.submitList(imageItemList)
            galleryImageAdapter.notifyDataSetChanged()

        }
    }

    private fun checkedImageRvSetting() {
        val checkedImageAdapter = checkedImageAdapter
        binding.selectedPhotoRv.adapter = checkedImageAdapter
        viewModel.checkedItemList.observe(viewLifecycleOwner) { checked ->
            if (checked.isEmpty()) {
                binding.selectedPhotoRv.visibility = View.GONE

                submitBtnUIUpdate(0)

            } else {
                //선택된 이미지 상단 리스트뷰로 보여주기위해 adapter 로 submit
                binding.selectedPhotoRv.visibility = View.VISIBLE
                checkedImageAdapter.submitList(checked)

                submitBtnUIUpdate(checked.size)
            }
            checkedImageAdapter.notifyDataSetChanged()
        }
    }

    private fun submitBtnUIUpdate(checkedListSize: Int) {
        if (checkedListSize in 1..4) {
            val dynamicText = "$checkedListSize"
            val staticText = " 등록"
            val finalText = dynamicText + staticText

            val spannableString = SpannableString(finalText)
            val colorSpan =
                ForegroundColorSpan(resources.getColor(R.color.lf_purple))  // 숫자(checked item size)는 보라색
            val startIndex = finalText.indexOf(dynamicText)  // 추가되는 동적 텍스트의 시작 인덱스의 시작과 끝을 구함
            val endIndex = startIndex + dynamicText.length

            //index 범위만큼 색깔 입히기
            spannableString.setSpan(
                colorSpan,
                startIndex,
                endIndex,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            binding.submitBtn.text = spannableString
            binding.submitBtn.isSelected = true
        }else {
            binding.submitBtn.text = "등록"
            binding.submitBtn.isSelected = false
        }

    }

    //갤러리 이미지를 선택했을 때
    override fun imageChecked(imageItem: ImageItem) {
        if (imageItem.isChecked) {
            viewModel.addCheckedItem(imageItem)
        } else {
            viewModel.cancelCheck(imageItem)
        }
    }

    override fun cameraBtnClicked() {
        val permissionsToRequest = mutableListOf<String>()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10 이상인 경우 WRITE_EXTERNAL_STORAGE 권한이 필요하지 않음
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionsToRequest.add(Manifest.permission.CAMERA)
            }
        } else {
            // Android 9 이하인 경우 WRITE_EXTERNAL_STORAGE 권한이 필요함
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionsToRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }
        requestPermissionLauncher.launch(permissionsToRequest.toTypedArray())
    }

    private fun applicationSettingIntent() {
        AppCustomDialog(
            "이미지를 촬영하기 위해서, 카메라 접근 권한이 필요합니다.\n 설정창으로 이동하시겠습니까?",
            "동의"
        ){
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val packageName = requireContext().packageName
            val uri = Uri.fromParts("package", packageName, null)
            intent.data = uri
            startActivity(intent)
        }.show(parentFragmentManager,"move_to_app_setting_alert")
      /*  AlertDialog.Builder(requireContext()).apply {
            setMessage("이미지를 촬영하기 위해서, 카메라 접근 권한이 필요합니다.\n 설정창으로 이동하시겠습니까?")
            setNegativeButton("취소", null)
            setPositiveButton("동의") { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val packageName = context.packageName
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            }
        }.show()*/
    }

    override fun checkedCountOver(limit : Int) {
        Toast.makeText(requireContext(), "사진은 ${limit}장까지 선택 가능합니다.", Toast.LENGTH_SHORT).show()
    }

    // 선택된 이미지 미리보기 뷰에서 , x 버튼을 눌렀을 때
    override fun checkedCancel(imageItem: ImageItem) {
        viewModel.cancelCheck(imageItem) // viewModel 이 들고있는 리스트에서도 isChecked 를 false로 바꿔주기
    }


}

interface GalleryRvListener {
    fun imageChecked(imageItem: ImageItem)
    fun cameraBtnClicked()
    fun checkedCountOver(imageLimit: Int)
}

interface CheckedImageRVListener {
    fun checkedCancel(imageItem: ImageItem)
}
