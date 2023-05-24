package com.lf.fashion.ui.mypage

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import com.kakao.sdk.user.UserApi
import com.kakao.sdk.user.UserApiClient
import com.lf.fashion.TAG
import com.lf.fashion.data.common.PreferenceManager
import com.lf.fashion.databinding.MypageFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class MyPageFragment : Fragment() {
    private lateinit var binding: MypageFragmentBinding
    private lateinit var userPreferences: PreferenceManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MypageFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userPreferences = PreferenceManager(requireContext().applicationContext)

        binding.kakaoLoginBackground.setOnClickListener {
            UserApiClient.instance.loginWithKakaoTalk(requireContext()) { token, error ->
                if (error != null) {
                    Log.d(TAG, "MyPageFragment - onViewCreated: 카카오톡 간편 로그인 실패 : $error");
                    if (error.message == "KakaoTalk not installed") {
                        kakaoLoginWithAccount()
                    }
                } else if (token != null) {
                    Log.d(TAG, "MyPageFragment - onViewCreated: 카카오톡 간편 로그인 성공 토큰 : $token")
                    runBlocking(Dispatchers.IO) {
                        launch {
                            userPreferences.saveAccessTokens(token.accessToken, token.refreshToken)
                        }
                    }
                }
            }
        }
    }

    private fun kakaoLoginWithAccount() {
        UserApiClient.instance.loginWithKakaoAccount(requireContext()) { token, error ->
            token?.let {
                runBlocking(Dispatchers.IO) {
                    launch {
                        userPreferences.saveAccessTokens(token.accessToken, token.refreshToken)
                        Log.d(
                            TAG,
                            "MyPageFragment - kakaoLoginWithAccount: dataStore ${userPreferences.accessToken.first()}"
                        );
                        Log.d(
                            TAG,
                            "MyPageFragment - kakaoLoginWithAccount: dataStore ${userPreferences.refreshToken.first()}"
                        );
                    }
                }
            }
            agree()
        }
    }

    private fun agree() {
        var scopes = mutableListOf<String>()

        scopes.add("account_email")

        UserApiClient.instance.loginWithNewScopes(
            requireContext(),
            scopes
        ) { token, error ->
            if (error != null) {
                Log.e(TAG, "사용자 추가 동의 실패", error)
            } else {
                Log.d(TAG, "allowed scopes: ${token!!.scopes}")

                // 사용자 정보 재요청
                UserApiClient.instance.me { user, error ->
                    if (error != null) {
                        Log.e(TAG, "사용자 정보 요청 실패", error)
                    } else if (user != null) {
                        Log.i(TAG, "사용자 정보 요청 성공")
                    }
                }
            }
        }
    }

}
