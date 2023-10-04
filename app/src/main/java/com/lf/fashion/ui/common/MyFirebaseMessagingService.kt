package com.lf.fashion.ui.common

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context.NOTIFICATION_SERVICE
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.lf.fashion.R
import okhttp3.internal.notify


class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val notificationManager = NotificationManagerCompat.from(
            applicationContext
        )
        var builder: NotificationCompat.Builder? = null
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "채팅 알림"
            val descriptionText = "채팅 알림입니다."
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val mChannel = NotificationChannel(getString(R.string.default_notification_channel_id), name, importance)
            mChannel.description = descriptionText
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)

            builder = NotificationCompat.Builder(applicationContext,  getString(R.string.default_notification_channel_id))
        } else {*/
            builder = NotificationCompat.Builder(applicationContext)
        //}

        val title = message.notification?.title ?:""
        val body = message.notification?.body ?: ""

        builder.setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.app_round_icon)
            .setVibrate(longArrayOf(1000,1000))


        val notification = builder.build()
       // notificationManager.notify(1,notification)
    }
}