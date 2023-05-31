package com.lf.fashion.ui.mypage

import androidx.lifecycle.ViewModel
import com.lf.fashion.data.repository.MyPageRepository
import com.lf.fashion.data.response.MsgResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(private val myPageRepository: MyPageRepository): ViewModel(){

    suspend fun getJWT(loginAccessToken : String) : MsgResponse {
        return myPageRepository.getJWT(loginAccessToken)
    }
}