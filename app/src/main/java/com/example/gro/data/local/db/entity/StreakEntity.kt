package com.example.gro.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "streaks")
data class StreakEntity(
    @PrimaryKey
    val walletAddress: String,
    val currentStreak: Int,
    val longestStreak: Int,
    val lastActiveDate: String,
    val totalActiveDays: Int,
)
