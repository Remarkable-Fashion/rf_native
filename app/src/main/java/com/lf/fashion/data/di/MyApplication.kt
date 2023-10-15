package com.lf.fashion.data.di

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
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

        /* var keyHash = Utility.getKeyHash(this)
         Log.d(TAG, "MyApplication - onCreate: $keyHash");*/
    }
}