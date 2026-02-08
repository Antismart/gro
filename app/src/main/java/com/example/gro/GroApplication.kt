package com.example.gro

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.example.gro.notification.GroNotificationChannel
import com.example.gro.worker.NotificationScheduler
import com.example.gro.worker.WorkManagerScheduler
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class GroApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        GroNotificationChannel.createChannels(this)
        WorkManagerScheduler.scheduleGardenSync(this)
        NotificationScheduler.scheduleReminders(this)
    }
}
