package com.lf.fashion.ui.home.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lf.fashion.TAG
import com.lf.fashion.data.response.Post
import com.lf.fashion.databinding.HomeGridItemBinding

class GridPostAdapter : ListAdapter<Post, GridPostAdapter.GridPostViewHolder>(DefaultPostDiff()) {
    private lateinit var binding: HomeGridItemBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridPostViewHolder {
        binding = HomeGridItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        Log.d(TAG, "GridPostAdapter - onCreateViewHolder: ");
        return GridPostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GridPostViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class GridPostViewHolder(private val binding: HomeGridItemBinding) :
        RecyclerView.ViewHolder(binding.root), SpanCountEditBtnListener {
        private var spanCount = 2
        fun bind(post: Post) {
            //첫번째 사진을 grid 로 노출

            binding.photoUrl = post.photo[0].imageUrl

            when (spanCount) {
                2 -> {
                    Log.d(
                        TAG,
                        "GridPostAdapter - editSpanCountClicked: !!!${binding.gridImage.layoutParams}"
                    );
                    val layoutParams = binding.gridImage.layoutParams
                    layoutParams.height = 600
                    binding.gridImage.layoutParams = layoutParams
                    Log.d(TAG, "GridPostAdapter - editSpanCountClicked: !!!$layoutParams");
                }
                3 -> {
                    val layoutParams = binding.gridImage.layoutParams
                    layoutParams.height = 300
                    binding.gridImage.layoutParams = layoutParams

                }
            }
            binding.executePendingBindings()
        }

        override fun editSpanCountClicked(boolean: Boolean, newSpan: Int)  {
            spanCount = newSpan
            binding.executePendingBindings()
        }

    }

    /*override fun editSpanCountClicked(boolean: Boolean, spanCount: Int) {
        when(spanCount){
            2->{
                Log.d(TAG, "GridPostAdapter - editSpanCountClicked: !!!${binding.gridImage.layoutParams}");
                val layoutParams = binding.gridImage.layoutParams
                layoutParams.height = 600
                binding.gridImage.layoutParams = layoutParams
                Log.d(TAG, "GridPostAdapter - editSpanCountClicked: !!!$layoutParams");
            }
            3->{
                val layoutParams = binding.gridImage.layoutParams
                layoutParams.height = 170
                binding.gridImage.layoutParams = layoutParams

            }
        }
    }*/

}

interface SpanCountEditBtnListener {
    fun editSpanCountClicked(boolean: Boolean, spanCount: Int)
}
