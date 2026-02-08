package com.example.gro.worker

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.Calendar
import java.util.concurrent.TimeUnit

object NotificationScheduler {

    private const val MORNING_REMINDER_WORK = "morning_reminder"
    private const val STREAK_REMINDER_WORK = "streak_reminder"

    fun scheduleReminders(context: Context) {
        scheduleMorningReminder(context)
        scheduleStreakReminder(context)
    }

    private fun scheduleMorningReminder(context: Context) {
        val morningRequest = PeriodicWorkRequestBuilder<MorningReminderWorker>(
            1, TimeUnit.DAYS,
        ).setInitialDelay(
            calculateDelayUntilHour(9),
            TimeUnit.MILLISECONDS,
        ).build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            MORNING_REMINDER_WORK,
            ExistingPeriodicWorkPolicy.KEEP,
            morningRequest,
        )
    }

    private fun scheduleStreakReminder(context: Context) {
        val streakRequest = PeriodicWorkRequestBuilder<StreakReminderWorker>(
            1, TimeUnit.DAYS,
        ).setInitialDelay(
            calculateDelayUntilHour(12),
            TimeUnit.MILLISECONDS,
        ).build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            STREAK_REMINDER_WORK,
            ExistingPeriodicWorkPolicy.KEEP,
            streakRequest,
        )
    }

    private fun calculateDelayUntilHour(hour: Int): Long {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (before(now)) add(Calendar.DAY_OF_MONTH, 1)
        }
        return target.timeInMillis - now.timeInMillis
    }
}
