package com.lf.fashion.ui.addPost

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.lf.fashion.TAG
import com.lf.fashion.data.response.ImageItem
import com.lf.fashion.databinding.PhotoImagePickerFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

/**
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

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val allPermissionsGranted = permissions.all { it.value }
            if (allPermissionsGranted) {
                //카메라로 intent
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                takePictureLauncher.launch(intent)
            } else {
                //앱 설정으로 이동 intent
                applicationSettingIntent()
            }
        }

    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result != null) {
            // 이미지를 캡처한 후의 처리 로직
            val extras = result.data?.extras
            val bitmap = extras?.get("data") as Bitmap
            val saveImage = SaveImage(requireContext())
            //이미지 저장하고 uri 얻기 -> viewModel 의 imageItem 에 추가
            val imageUri = saveImage.saveImageToGallery(bitmap)
            viewModel.addImageToImageList(ImageItem(imageUri,false))

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
    private fun galleryImageRvSetting() {
        val galleryImageAdapter = ImageAdapter(viewModel, this@ImagePickerFragment)
        binding.recyclerviewImage.adapter = galleryImageAdapter
        viewModel.imageItemList.observe(viewLifecycleOwner) { imageItemList ->
            galleryImageAdapter.submitList(imageItemList)
            galleryImageAdapter.notifyDataSetChanged()
        }
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
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.CAMERA)
            }
        } else {
            // Android 9 이하인 경우 WRITE_EXTERNAL_STORAGE 권한이 필요함
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }
        requestPermissionLauncher.launch(permissionsToRequest.toTypedArray())
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
