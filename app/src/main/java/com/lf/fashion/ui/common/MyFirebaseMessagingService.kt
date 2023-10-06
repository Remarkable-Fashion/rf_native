package com.lf.fashion.ui.common

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.lf.fashion.MainActivity
import com.lf.fashion.R
import com.lf.fashion.TAG


class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val notificationManager = NotificationManagerCompat.from(
            applicationContext
        )
        Log.e(TAG, "onMessageReceived: ${message.data}")
        Log.e(TAG, "onMessageReceived: ${message.notification?.title} , ${message.notification?.body} , ${message.notification?.channelId}")
        var builder = NotificationCompat.Builder(applicationContext,getString(R.string.default_notification_channel_id))
//data 를 받아서

        val title = message.notification?.title ?:""
        val body = message.notification?.body ?: ""

        builder.setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.fcm_small_icon)
            .setVibrate(longArrayOf(1000,1000))

        val notification = builder.build()

        //설정 알림 허용 취소 -> 모든 푸시 알림 x
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            notificationManager.notify(1,notification)
            return
        }
        val clickAction = message.data["click_action"]
        if (clickAction == "OPEN_ACTIVITY") {
            // 클릭 시 실행될 액티비티를 지정하고 시작합니다.
            val intent = Intent(applicationContext, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) // 이미 앱이 실행 중인 경우 액티비티를 최상위로 가져옴
            startActivity(intent)
        }
    }
}