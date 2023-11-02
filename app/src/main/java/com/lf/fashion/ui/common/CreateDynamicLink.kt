package com.lf.fashion.ui.common

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.core.content.ContextCompat.startActivity
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import com.lf.fashion.TAG

class CreateDynamicLink (context: Context, key : String , value :Int){
    // 공유할 데이터를 Map에 추가합니다.
    init {
        val dynamicLink = Firebase.dynamicLinks.createDynamicLink()
            .setLink(Uri.parse("https://static.rcloset.biz?$key=$value")) // 딥 링크로 이동할 URL을 설정합니다.
            .setDomainUriPrefix("https://randomcloset.page.link") // Firebase Console에서 생성한 도메인을 설정합니다.
            .setAndroidParameters(DynamicLink.AndroidParameters.Builder().build()) // Android 앱에 대한 추가 구성(옵션)
            .setSocialMetaTagParameters(
                DynamicLink.SocialMetaTagParameters.Builder()
                    .setTitle("랜덤클로젯")
                    .setDescription("")
                    .build()
            ) // 소셜 메타태그(옵션)
            .setNavigationInfoParameters(
                DynamicLink.NavigationInfoParameters.Builder()
                    .setForcedRedirectEnabled(false) // 강제 리디렉션 비활성화
                    .build()
            )
            .buildDynamicLink()

        val dynamicLinkUri = dynamicLink.uri

        // 딥 링크를 사용하여 공유하기 기능을 실행
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, dynamicLinkUri.toString())

        // Use the Android's built-in sharing mechanism
        val chooser = Intent.createChooser(intent, "Share via")
        context.startActivity(chooser)
    }
}
/*package com.lf.fashion.ui.common

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.core.content.ContextCompat.startActivity
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import com.lf.fashion.TAG

class CreateDynamicLink (context: Context, pair1 :Pair<String ,Int> , pair2 : Pair<String,Int> ? =null){
    // 공유할 데이터를 Map에 추가합니다.
    init {
        val param1 = "${pair1.first}=${pair1.second}"
        val param2 = if(pair2 !=null ){ "&${pair2.first}=${pair2.second}"} else ""
        val dynamicLink = Firebase.dynamicLinks.createDynamicLink()
            .setLink(Uri.parse("https://static.rcloset.biz?$param1$param2")) // 딥 링크로 이동할 URL을 설정합니다.
            .setDomainUriPrefix("https://randomcloset.page.link") // Firebase Console에서 생성한 도메인을 설정합니다.
            .setAndroidParameters(DynamicLink.AndroidParameters.Builder().build()) // Android 앱에 대한 추가 구성(옵션)
            .setSocialMetaTagParameters(
                DynamicLink.SocialMetaTagParameters.Builder()
                    .setTitle("랜덤클로젯")
                    .setDescription("")
                    .build()
            ) // 소셜 메타태그(옵션)
            .setNavigationInfoParameters(
                DynamicLink.NavigationInfoParameters.Builder()
                    .setForcedRedirectEnabled(false) // 강제 리디렉션 비활성화
                    .build()
            )
            .buildDynamicLink()

        val dynamicLinkUri = dynamicLink.uri

        // 딥 링크를 사용하여 공유하기 기능을 실행
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, dynamicLinkUri.toString())

        // Use the Android's built-in sharing mechanism
        val chooser = Intent.createChooser(intent, "Share via")
        context.startActivity(chooser)
    }
}*/