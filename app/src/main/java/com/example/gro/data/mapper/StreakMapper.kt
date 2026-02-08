package com.example.gro.data.mapper

import com.example.gro.data.local.db.entity.StreakEntity
import com.example.gro.domain.model.Streak

fun StreakEntity.toDomain(): Streak = Streak(
    walletAddress = walletAddress,
    currentStreak = currentStreak,
    longestStreak = longestStreak,
    lastActiveDate = lastActiveDate,
    totalActiveDays = totalActiveDays,
)

fun Streak.toEntity(): StreakEntity = StreakEntity(
    walletAddress = walletAddress,
    currentStreak = currentStreak,
    longestStreak = longestStreak,
    lastActiveDate = lastActiveDate,
    totalActiveDays = totalActiveDays,
)
