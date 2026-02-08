package com.example.gro.domain.model

data class Streak(
    val walletAddress: String,
    val currentStreak: Int,
    val longestStreak: Int,
    val lastActiveDate: String,
    val totalActiveDays: Int,
)
