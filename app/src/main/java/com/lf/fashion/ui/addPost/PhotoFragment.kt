package com.lf.fashion.ui.addPost

import android.Manifest
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.lf.fashion.R
import com.lf.fashion.TAG
import com.lf.fashion.databinding.PhotoFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PhotoFragment : Fragment() {
    private lateinit var binding : PhotoFragmentBinding

    //복수의 권한이 필요한 경우 RequestMultiplePermissions() 후 launch(배열) 로 전달
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val allPermissionsGranted = permissions.all { it.value }
            val galleryPermission = permissions[Manifest.permission.READ_EXTERNAL_STORAGE] ?: false
            Log.d(TAG, "PhotoFragment - : $allPermissionsGranted");
            //모두 허용 또는 외부저장소 읽기 권한 있을 시 커스텀 갤러리 뷰로 이동
            if (allPermissionsGranted || galleryPermission ) {
                //모든 이미지타입
                // requestImageUriLauncher.launch("image/*") // 여기서 요청할경우 권한 동의 후 바로 파일접근으로 넘어갈 수 있다.
                Log.d(TAG, "PhotoFragment - : granted");
                val direction = PhotoFragmentDirections.actionNavigationPhotoToImagePickerFragment()
                findNavController().navigate(direction)
            } else {
                Log.d(TAG, "PhotoFragment - : granted fail");
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
        binding = PhotoFragmentBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        when{
            ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) -> {
                //권한을 deny 한 적이 있고 다시 기능을 이용하려고 시도할 때, 안내 문구를 띄워주기
                showPermissionInfoDialog()
            }
            // 권한을 아직 허용한 적이 없고, 안내문구를 보내야하는 시점도 아닐 경우
            else -> {
                requestPermissionLauncher.launch(permissions)
            }
        }
    }
    private fun showPermissionInfoDialog() {
        AlertDialog.Builder(requireContext()).apply {
            setMessage("이미지를 가져오기 위해서, 외부 저장소 읽기 권한이 필요합니다.")
            setNegativeButton("취소", null)
            setPositiveButton("동의") { _, _ -> requestPermissionLauncher.launch(permissions) }
        }.show()
    }
}