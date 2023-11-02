package com.lf.fashion.data.di

import android.app.Application
import android.util.Log
import android.widget.Toast
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.util.Utility
import com.lf.fashion.TAG
import com.lf.fashion.data.common.KAKAO_KEY
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication :Application(){
    override fun onCreate() {
        super.onCreate()
        KakaoSdk.init(this, KAKAO_KEY)
        FirebaseApp.initializeApp(this)

        Thread.setDefaultUncaughtExceptionHandler { _, throwable ->
            // 앱 내부 예외 처리
            Toast.makeText(this,"오류가 발생했습니다.",Toast.LENGTH_SHORT).show()
            Firebase.crashlytics.recordException(throwable)
        }
    }
}