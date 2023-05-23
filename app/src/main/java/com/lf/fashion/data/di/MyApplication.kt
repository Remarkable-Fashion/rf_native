package com.lf.fashion.data.di

import android.app.Application
import com.kakao.sdk.common.KakaoSdk
import com.lf.fashion.data.common.KAKAO_KEY
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication :Application(){
    override fun onCreate() {
        super.onCreate()
        KakaoSdk.init(this, KAKAO_KEY)

    }
}