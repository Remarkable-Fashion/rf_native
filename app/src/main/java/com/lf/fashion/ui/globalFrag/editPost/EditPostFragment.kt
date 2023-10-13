package com.lf.fashion.ui.globalFrag.editPost

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.lf.fashion.MainActivity
import com.lf.fashion.MainNaviDirections
import com.lf.fashion.R
import com.lf.fashion.TAG
import com.lf.fashion.data.model.Cloth
import com.lf.fashion.data.model.ImageItem
import com.lf.fashion.data.model.ImageUrl
import com.lf.fashion.data.model.Posts
import com.lf.fashion.databinding.EditPostFragmentBinding
import com.lf.fashion.ui.addPost.ImagePickerFragment
import com.lf.fashion.ui.common.cancelBtnBackStack
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditPostFragment : Fragment(R.layout.edit_post_fragment) {
    private val viewModel: EditPostViewModel by hiltNavGraphViewModels(R.id.navigation_eidt_post)
    private lateinit var binding: EditPostFragmentBinding
    private val editPostViewPagerAdapter = EditPostAdapter()
    private lateinit var editPostRvAdapter: EditPhotoRvAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainActivity.hideNavi(true)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = EditPostFragmentBinding.bind(view)
        val post = arguments?.get("post") as Posts

        //step2Fragment 에서 사용할 데이터 미리 요청 (Step2Fragment 에서 호출시 의상 등록하러 갔다가 오면 계속 중복요청 !)
        with(viewModel) {
            getPostInfoByPostId(post.id)
        }

        viewModel.imageList.value = post.images.toMutableList()
        viewModel.postId = post.id
        if(viewModel.postId == null) return


        binding.horizontalViewPager.apply {
            adapter = editPostViewPagerAdapter
            getChildAt(0).overScrollMode =
                RecyclerView.OVER_SCROLL_NEVER // 최상단,최하단 스크롤 이벤트 shadow 제거
        }
        editPostRvAdapter = EditPhotoRvAdapter() {
            viewModel.removeImage(it)
        }

        binding.photoList.adapter = editPostRvAdapter


        viewModel.imageList.observe(viewLifecycleOwner) {
            editPostViewPagerAdapter.submitList(it)
            editPostRvAdapter.submitList(it)
            editPostViewPagerAdapter.notifyDataSetChanged()
            editPostRvAdapter.notifyDataSetChanged()
        }

        submitBtnOnclick()
        addImageBtnOnclick()

        //ImagePickerFragment 에서 선택한 이미지를 바인딩하고 서버에 전송가능하도록 selectedImageUri 에 담아준다.
        setFragmentResultListener(requestKey = ImagePickerFragment.REQUEST_KEY) { _, bundle ->
            val imageUris = bundle.get("imageURI") as Array<String>
            val imageURlList = imageUris.map { ImageUrl(it) }.toMutableList()
            viewModel.newImageList = imageURlList
            viewModel.addToImageList(imageURlList) //
        }
        cancelBtnBackStack(binding.backBtn)
    }

    private fun addImageBtnOnclick() {
        val size = viewModel.imageList.value?.size ?: 0
        val space = 4 - size

        binding.addBtn.setOnClickListener {
            if(space <0){
                Toast.makeText(requireContext(), "사진은 4장까지 등록 가능합니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            findNavController().navigate(
                R.id.action_editPostFragment_to_imagePickerFragment,
                bundleOf("limit" to space)
            )
        }
    }

    private fun submitBtnOnclick() {
        binding.submitBtn.setOnClickListener {
            if (viewModel.imageList.value?.isEmpty() == true) {
                Toast.makeText(requireContext(), "사진을 1개 이상 등록해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            findNavController().navigate(
                R.id.action_editPostFragment_to_editPostStep2Fragment
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        MainActivity.hideNavi(false)
    }
}
