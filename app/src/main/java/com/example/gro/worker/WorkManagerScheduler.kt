package com.example.gro.worker

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object WorkManagerScheduler {

    private const val GARDEN_SYNC_WORK = "garden_sync"

    fun scheduleGardenSync(context: Context) {
        val syncRequest = PeriodicWorkRequestBuilder<GardenSyncWorker>(
            6, TimeUnit.HOURS,
        ).build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            GARDEN_SYNC_WORK,
            ExistingPeriodicWorkPolicy.KEEP,
            syncRequest,
        )
    }
}
