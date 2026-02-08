package com.example.gro.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

object GroNotificationChannel {

    const val GARDEN_CHANNEL_ID = "gro_garden"

    fun createChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                GARDEN_CHANNEL_ID,
                "Garden Updates",
                NotificationManager.IMPORTANCE_DEFAULT,
            ).apply {
                description = "Growth updates and reminders for your garden"
            }

            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
}
