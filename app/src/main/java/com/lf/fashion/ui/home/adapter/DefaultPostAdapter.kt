package com.lf.fashion.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayoutMediator
import com.lf.fashion.data.response.Posts
import com.lf.fashion.databinding.HomeAVerticalItemBinding
import com.lf.fashion.ui.home.PhotoClickListener
import com.lf.fashion.ui.home.VerticalViewPagerClickListener

class DefaultPostAdapter(
    private val photoClickListener: PhotoClickListener,
    private val verticalViewPagerClickListener: VerticalViewPagerClickListener
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
                getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER // 양옆 오버 스크롤 이벤트 shadow 제거

                TabLayoutMediator(
                    binding.viewpagerIndicator,
                    this
                ) { _, _ -> }.attach()
            }
        }

        fun bind(post: Posts) {
            binding.post = post
            binding.likeBtn.isSelected = post.isFavorite!!
            binding.likesValue.text = post.count.favorites.toString()
            binding.scrapBtn.isSelected = post.isScrap?:true //null 인경우는 내 스크랩 모아보기이기 때문에, 모두 true

            nestedAdapter.submitList(post.images)

            //좋아요 아이콘 ic 변경 _ 임시적으로 이미지만 처리하기에 여기서 적용함
            binding.likeBtn.setOnClickListener {
                //  it.isSelected = !it.isSelected
                verticalViewPagerClickListener.likeBtnClicked(it.isSelected,post)

            }
            binding.scrapBtn.setOnClickListener {
                verticalViewPagerClickListener.scrapBtnClicked(it.isSelected,post)
            }
            binding.shareBtn.setOnClickListener {
                verticalViewPagerClickListener.shareBtnClicked()
            }

            binding.photoZipBtn.setOnClickListener {
                verticalViewPagerClickListener.photoZipBtnClicked()
            }

            binding.infoBtn.setOnClickListener {
                verticalViewPagerClickListener.infoBtnClicked()
            }

            binding.executePendingBindings()

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

        return if (payload.isEmpty()) null else payload
    }
}

