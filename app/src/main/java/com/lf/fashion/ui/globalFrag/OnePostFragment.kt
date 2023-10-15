package com.lf.fashion.ui.globalFrag

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.lf.fashion.MainActivity
import com.lf.fashion.R
import com.lf.fashion.TAG
import com.lf.fashion.data.model.Count
import com.lf.fashion.data.model.ImageUrl
import com.lf.fashion.data.model.Posts
import com.lf.fashion.data.model.Profile
import com.lf.fashion.data.model.UserInfo
import com.lf.fashion.databinding.OnePostFragmentBinding
import com.lf.fashion.ui.globalFrag.adapter.DefaultPostAdapter
import com.lf.fashion.ui.home.PhotoClickListener
import com.lf.fashion.ui.home.VerticalViewPagerClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnePostFragment : Fragment(R.layout.one_post_fragment) ,
    PhotoClickListener, VerticalViewPagerClickListener {
    private lateinit var binding: OnePostFragmentBinding
    private val defaultAdapter = DefaultPostAdapter(
        this@OnePostFragment,
        this@OnePostFragment
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainActivity.hideNavi(state = true)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = OnePostFragmentBinding.bind(view)
        val postId = arguments?.getString("postId")
        Log.e(TAG, "onePostFragment onviewCreated: $postId", )
        val test =  Posts(
            -1,
            false,
            false,
            false,
            true,
            "d",
            listOf(ImageUrl("https://dev-rc-1.s3.ap-northeast-2.amazonaws.com/1-Sc-1691125436567.jpg")),
            UserInfo(
                -1,
                "test",
                Profile(
                    "https://blog.kakaocdn.net/dn/c3vWTf/btqUuNfnDsf/VQMbJlQW4ywjeI8cUE91OK/img.jpg",
                    null,
                    null,
                    null,
                    null
                ),
                null
            ),
            Count(1,0,0,0)
        )
        binding.horizontalViewPager.apply {
            adapter = defaultAdapter
            (adapter as? DefaultPostAdapter)?.apply {
                submitList(listOf(test))
            }
            getChildAt(0).overScrollMode =
                RecyclerView.OVER_SCROLL_NEVER // 최상단,최하단 스크롤 이벤트 shadow 제거
        }

    }

    override fun photoClicked(bool: Boolean, photo: List<ImageUrl>) {
    }

    override fun likeBtnClicked(likeState: Boolean, post: Posts) {
    }

    override fun scrapBtnClicked(scrapState: Boolean, post: Posts) {
    }

    override fun shareBtnClicked(post: Posts) {
    }

    override fun kebabBtnClicked(post: Posts) {
    }

    override fun photoZipBtnClicked(post: Posts) {
    }

    override fun infoBtnClicked(postId: Int) {
    }

    override fun profileSpaceClicked(userId: Int) {
    }

    override fun onDestroy() {
        super.onDestroy()
        MainActivity.hideNavi(false)
    }
}