package com.example.gro.worker

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.gro.MainActivity
import com.example.gro.R
import com.example.gro.domain.repository.StreakRepository
import com.example.gro.domain.repository.WalletRepository
import com.example.gro.notification.GroNotificationChannel
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@HiltWorker
class StreakReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val walletRepository: WalletRepository,
    private val streakRepository: StreakRepository,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val address = walletRepository.getConnectedAddress() ?: return Result.success()

        if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            return Result.success()
        }

        val streak = streakRepository.getStreak(address) ?: return Result.success()
        val lastDate = LocalDate.parse(streak.lastActiveDate, DateTimeFormatter.ISO_LOCAL_DATE)
        val daysSince = ChronoUnit.DAYS.between(lastDate, LocalDate.now())

        // Only remind if they haven't visited today and have an active streak
        if (daysSince < 1 || streak.currentStreak < 1) return Result.success()

        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            applicationContext, 0, intent, PendingIntent.FLAG_IMMUTABLE,
        )

        val message = if (daysSince >= 2) {
            "Your ${streak.currentStreak}-day streak is about to end! Water your garden today."
        } else {
            "Keep your ${streak.currentStreak}-day streak going! Your plants miss you."
        }

        val notification = NotificationCompat.Builder(applicationContext, GroNotificationChannel.GARDEN_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Don't break your streak!")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(applicationContext).notify(1002, notification)
        return Result.success()
    }
}
