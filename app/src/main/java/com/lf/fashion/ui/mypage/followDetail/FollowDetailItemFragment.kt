package com.lf.fashion.ui.mypage.followDetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.lf.fashion.R
import com.lf.fashion.databinding.MypageFollowViewpagerBinding

class FollowDetailItemFragment(private val tabName : String) : Fragment(R.layout.mypage_follow_viewpager) {
    private lateinit var binding : MypageFollowViewpagerBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = MypageFollowViewpagerBinding.bind(view)

        val profileRecyclerView = binding.profileRV.apply {

        }

        when(tabName){
            "follow"->{

            }
            "following"->{

            }
            "block" ->{

            }
        }
    }
}