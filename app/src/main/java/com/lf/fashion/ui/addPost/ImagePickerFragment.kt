package com.lf.fashion.ui.addPost

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.lf.fashion.TAG
import com.lf.fashion.data.response.ImageItem
import com.lf.fashion.databinding.PhotoImagePickerFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.IOException


@AndroidEntryPoint
class ImagePickerFragment : Fragment(), GalleryRvListener,
    CheckedImageRVListener {
    private lateinit var binding: PhotoImagePickerFragmentBinding
    private val viewModel: ImagePickerViewModel by viewModels {
        ImagePickerViewModelFactory(requireContext())
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                //카메라
                takePictureLauncher.launch(Unit)
            } else {
                applicationSettingIntent()
            }
        }

    private val takePictureLauncher = registerForActivityResult(TakePictureContract(requireContext())) { result ->
        if (result != null) {
            // 이미지를 캡처한 후의 처리 로직
            Log.d(TAG, "ImagePickerFragment - : $result");
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

        binding.buttonListener = View.OnClickListener {
            // Pass Uri list to fragment outside
            Log.d(TAG, "ImagePickerFragment - onCreateView: ${viewModel.getCheckedImageUriList()}");
            /* activity?.supportFragmentManager?.setFragmentResult(
                 URI_LIST_CHECKED,
                 bundleOf("uriList" to viewModel.getCheckedImageUriList())
             )*/
            // findNavController().navigateUp()
        }

        galleryImageRvSetting()

        checkedImageRvSetting()

    }

    private fun checkedImageRvSetting() {
        val checkedImageAdapter = CheckedImageAdapter(this@ImagePickerFragment)
        binding.selectedPhotoRv.adapter = checkedImageAdapter
        viewModel.checkedItemList.observe(viewLifecycleOwner) { checked ->
            Log.d(TAG, "ImagePickerFragment - onViewCreated: $checked");
            if (checked.isEmpty()) {
                binding.selectedPhotoRv.visibility = View.GONE
            } else {
                binding.selectedPhotoRv.visibility = View.VISIBLE
                checkedImageAdapter.submitList(checked)
            }
            checkedImageAdapter.notifyDataSetChanged()
        }
    }

    private fun galleryImageRvSetting() {
        val galleryImageAdapter = ImageAdapter(viewModel, this@ImagePickerFragment)
        binding.recyclerviewImage.adapter = galleryImageAdapter
        viewModel.imageItemList.observe(viewLifecycleOwner) { imageItemList ->
            galleryImageAdapter.submitList(imageItemList)
            galleryImageAdapter.notifyDataSetChanged()
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
        requestPermissionLauncher.launch(Manifest.permission.CAMERA)
    }

    // 선택된 이미지 미리보기 뷰에서 , x 버튼을 눌렀을 때
    override fun checkedCancel(imageItem: ImageItem) {
        viewModel.cancelCheck(imageItem) // viewModel 이 들고있는 리스트에서도 isChecked 를 false로 바꿔주기
    }

    private fun applicationSettingIntent(){
        AlertDialog.Builder(requireContext()).apply {
            setMessage("이미지를 촬영하기 위해서, 카메라 접근 권한이 필요합니다.\n 설정창으로 이동하시겠습니까?")
            setNegativeButton("취소", null)
            setPositiveButton("동의") { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val packageName = context.packageName
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            }
        }.show()
    }
}

interface GalleryRvListener {
    fun imageChecked(imageItem: ImageItem)
    fun cameraBtnClicked()
}

interface CheckedImageRVListener {
    fun checkedCancel(imageItem: ImageItem)
}
