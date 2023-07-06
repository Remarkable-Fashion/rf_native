package com.lf.fashion.ui.mypage

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.lf.fashion.R
import com.lf.fashion.TAG
import com.lf.fashion.data.common.PreferenceManager
import com.lf.fashion.databinding.MypageProfileFragmentBinding
import com.lf.fashion.ui.cancelBtnBackStack
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class ProfileEditFragment : Fragment() {
    private lateinit var binding: MypageProfileFragmentBinding
    private lateinit var userPreferences: PreferenceManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        userPreferences = PreferenceManager(requireContext().applicationContext)

        runBlocking {
            launch {
                if(userPreferences.accessToken.first().isNullOrEmpty()){
                    Log.d(TAG, "MyPageFragment - onCreateView: pref token null !! ");

                    findNavController().navigate(R.id.action_navigation_mypage_to_loginFragment)
                }else {
                    val first = userPreferences.accessToken.first()
                    Log.d(TAG, "MyPageFragment - onCreateView: $first");
                }
            }
        }

        binding = MypageProfileFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cancelBtnBackStack(binding.backBtn)





    }
}
