package com.lf.fashion.ui.globalFrag.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayoutMediator
import com.lf.fashion.TAG
import com.lf.fashion.data.model.Posts
import com.lf.fashion.databinding.HomeAVerticalItemBinding
import com.lf.fashion.ui.home.PhotoClickListener
import com.lf.fashion.ui.home.VerticalViewPagerClickListener
import com.lf.fashion.ui.home.adapter.PhotoHorizontalAdapter

class DefaultPostAdapter(
    private val photoClickListener: PhotoClickListener,
    private val verticalViewPagerClickListener: VerticalViewPagerClickListener,
    private val userInfoPost: Posts? = null,
    private val myPhotozip: Boolean? = null
) :
    ListAdapter<Posts, DefaultPostAdapter.DefaultPostViewHolder>(DefaultPostDiff()) {

    private lateinit var binding: HomeAVerticalItemBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DefaultPostViewHolder {
        binding =
            HomeAVerticalItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DefaultPostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DefaultPostViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class DefaultPostViewHolder(private val binding: HomeAVerticalItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val nestedAdapter = PhotoHorizontalAdapter(photoClickListener)

        init {
            with(binding.horizontalViewPager) {
                adapter = nestedAdapter
                getChildAt(0).overScrollMode =
                    RecyclerView.OVER_SCROLL_NEVER // 양옆 오버 스크롤 이벤트 shadow 제거

                TabLayoutMediator(
                    binding.viewpagerIndicator,
                    this
                ) { _, _ -> }.attach()
            }

        }

        fun bind(post: Posts) {
            binding.post = post

            if (userInfoPost != null) {   // photoZipVertical 에서 response 받는 posts 에는 user 정보가 없기 때문에, 파라미터가 존재할 시 이를 바인딩 해주도록 .
                binding.post = userInfoPost
                post.user = userInfoPost.user // 하단에서 사용할 수도 있으니까 초기화
            }
            profileSpaceVisibilityBinding(post) // 마이페이지일 경우 userInfo 도 post 내부 user 도 없기 때문에 숨김 처리
            nestedAdapter.submitList(post.images)

            val postDetailMenu = binding.postDetailMenu
            with(postDetailMenu) {
                likeBtn.isSelected = post.isFavorite ?: false
                likesValue.text = post.count.favorites.toString()
                scrapBtn.isSelected = post.isScrap ?: true //null 인 경우는 내 스크랩 모아보기이기 때문에, 모두 true

                likeBtn.setOnClickListener {
                    //if userPref logincheck !
                    verticalViewPagerClickListener.likeBtnClicked(it.isSelected, post)

                }

                scrapBtn.setOnClickListener {
                    verticalViewPagerClickListener.scrapBtnClicked(it.isSelected, post)
                }
                shareBtn.setOnClickListener {
                    verticalViewPagerClickListener.shareBtnClicked(post)
                }
                photoZipBtn.setOnClickListener {
                    verticalViewPagerClickListener.photoZipBtnClicked(post)
                }
                kebabBtn.setOnClickListener {
                    verticalViewPagerClickListener.kebabBtnClicked(post)
                }

            }

            binding.infoBtn.setOnClickListener {
                verticalViewPagerClickListener.infoBtnClicked(post.id)
            }
            binding.profileSpace.setOnClickListener {
                if (post.user == null) return@setOnClickListener
                verticalViewPagerClickListener.profileSpaceClicked(post.user!!.id)
            }
            binding.executePendingBindings()

        }

        private fun profileSpaceVisibilityBinding(post: Posts) {
            if (userInfoPost == null && post.user == null) {
                binding.profileImage.isVisible = false
                binding.profileName.isVisible = false
                binding.privateBadge.isVisible =
                    post.isPublic == false // 나의 게시물 + 미게시 게시물일 경우 미게시 뱃지 노출
            } else {
                binding.profileImage.isVisible = true
                binding.profileName.isVisible = true
            }
            //mypage 사진 모아보기 vertical 버전 내부에는 사진 모아보기 아이콘 없앰 , 무한루프/백스택 쌓임 방지
            binding.postDetailMenu.photoZipBtn.isVisible = myPhotozip != true
        }
    }

}

class DefaultPostDiff : DiffUtil.ItemCallback<Posts>() {

    override fun areItemsTheSame(oldItem: Posts, newItem: Posts): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Posts, newItem: Posts): Boolean {
        return oldItem.copy(isFavorite = newItem.isFavorite, count = newItem.count) == newItem

    }

    override fun getChangePayload(oldItem: Posts, newItem: Posts): Any? {

        val payload = mutableSetOf<String>()

        if (oldItem.isFavorite != newItem.isFavorite) {
            payload.add("IS_FAVORITE")
        }

        if (oldItem.count.favorites != newItem.count.favorites) {
            payload.add("FAVORITES_COUNT")
        }

        if (oldItem.isScrap != newItem.isScrap) {
            payload.add("SCRAP_STATE")
        }

        if (oldItem.isPublic != newItem.isPublic) {
            payload.add("PUBLIC_STATE")
        }

        return if (payload.isEmpty()) null else payload
    }


}

