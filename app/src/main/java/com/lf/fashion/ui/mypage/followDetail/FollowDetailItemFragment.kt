package com.lf.fashion.ui.mypage.followDetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.lf.fashion.databinding.MypageFollowViewpagerBinding

class FollowDetailItemFragment(private val tabName : String) : Fragment() {
    private lateinit var binding : MypageFollowViewpagerBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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