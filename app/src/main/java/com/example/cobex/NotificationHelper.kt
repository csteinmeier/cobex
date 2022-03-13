package com.example.cobex

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

object NotificationHelper {

    enum class CHANNEL(val channelID: String, val importance: Int, val description: String) {
        ACTIVITY_RECOGNITION(
            "Activity Recognition",
            NotificationManager.IMPORTANCE_HIGH,
            "Channel used to inform about activity recognitions"
        )
    }


    /**
     * Will set a Notification and let a NotificationManager notify an Notification
     *
     * @param channel use or create a channel in [CHANNEL]
     * @param icon default icon = cobex Logo
     * @param title default title = [channel.channelID]
     * @param text default text = [channel.description]
     */
    fun setNotification(context: Context, channel: CHANNEL , icon: Int?, title: String?, text: String?){
        val channelText = channel.channelID
        val mNotification =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(channel.channelID, channel.channelID, channel.importance)
        channel.description = channel.description
        mNotification.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(context, channelText)
            .setSmallIcon(icon ?: R.drawable.logo)
            .setContentTitle(title ?: channelText)
            .setContentText(text ?: channel.description)
            .setAutoCancel(true)
        val intent = Intent(context, context::class.java)
        val pi = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        notification.setContentIntent(pi)
        mNotification.notify(0, notification.build())
    }


}