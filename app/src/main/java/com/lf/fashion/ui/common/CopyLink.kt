package com.lf.fashion.ui.common

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import android.widget.Toast
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase

class CopyLink {
    fun copyTextToClipboard(context: Context, postId: Int, paramName: String) {
        val dynamicLink = Firebase.dynamicLinks.createDynamicLink()
            .setLink(Uri.parse("https://static.rcloset.biz?$paramName=$postId")) // 딥 링크로 이동할 URL을 설정합니다.
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


        // 클립보드 매니저 가져오기
        val clipboardManager =
            context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager



        // 클립 데이터 생성
        val clipData = ClipData.newPlainText("랜덤 클로젯", dynamicLinkUri.toString())

        // 클립보드에 클립 데이터 복사
        clipboardManager.setPrimaryClip(clipData)

        // 사용자에게 메시지 표시 (옵션)
        Toast.makeText(context, "복사가 완료되었습니다.", Toast.LENGTH_SHORT).show()
    }
}