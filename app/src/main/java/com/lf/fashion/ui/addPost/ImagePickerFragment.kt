package com.lf.fashion.ui.addPost

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.lf.fashion.TAG
import com.lf.fashion.data.response.ImageItem
import com.lf.fashion.databinding.PhotoImagePickerFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.internal.lifecycle.HiltViewModelFactory
import dagger.hilt.android.qualifiers.ApplicationContext

@AndroidEntryPoint
class ImagePickerFragment : Fragment(), ImageCheckedListener,
    CheckedImageCancelBtnListener {
    private lateinit var binding: PhotoImagePickerFragmentBinding
    private val viewModel: ImagePickerViewModel by viewModels {
        ImagePickerViewModelFactory(requireContext())
    }
    //private val checkedImageList: MutableList<ImageItem> = mutableListOf()
    // lateinit var checkedImageAdapter :CheckedImageAdapter

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

    // 선택된 이미지 미리보기 뷰에서 , x 버튼을 눌렀을 때
    override fun checkedCancel(imageItem: ImageItem) {
        viewModel.cancelCheck(imageItem) // viewModel 이 들고있는 리스트에서도 isChecked 를 false로 바꿔주기
    }
}

interface ImageCheckedListener {
    fun imageChecked(imageItem: ImageItem)
}

interface CheckedImageCancelBtnListener {
    fun checkedCancel(imageItem: ImageItem)
}