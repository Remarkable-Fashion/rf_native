package com.lf.fashion.ui.globalFrag.editPost

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.lf.fashion.MainActivity
import com.lf.fashion.R
import com.lf.fashion.TAG
import com.lf.fashion.data.model.Posts
import com.lf.fashion.databinding.EditPostFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditPostFragment : Fragment(R.layout.edit_post_fragment) {
    private lateinit var binding: EditPostFragmentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainActivity.hideNavi(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = EditPostFragmentBinding.bind(view)
        val post = arguments?.get("post") as Posts
        binding.horizontalViewPager.apply {
            adapter = EditPostAdapter().apply { submitList(post.images) }
            getChildAt(0).overScrollMode =
                RecyclerView.OVER_SCROLL_NEVER // 최상단,최하단 스크롤 이벤트 shadow 제거
        }
        binding.photoList.apply {
            adapter = EditPhotoRvAdapter().apply{ submitList(post.images)}
        }
        Log.e(TAG, "onViewCreated: $post")
        binding.submitBtn.setOnClickListener {
            findNavController().navigate(R.id.action_editPostFragment_to_editPostStep2Fragment , bundleOf("post" to post))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        MainActivity.hideNavi(false)
    }
}