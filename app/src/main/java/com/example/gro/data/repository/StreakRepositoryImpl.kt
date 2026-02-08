package com.example.gro.data.repository

import com.example.gro.data.local.db.dao.StreakDao
import com.example.gro.data.local.db.entity.StreakEntity
import com.example.gro.data.mapper.toDomain
import com.example.gro.domain.model.Streak
import com.example.gro.domain.repository.StreakRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StreakRepositoryImpl @Inject constructor(
    private val streakDao: StreakDao,
) : StreakRepository {

    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    override fun observeStreak(walletAddress: String): Flow<Streak?> {
        return streakDao.observeStreak(walletAddress).map { it?.toDomain() }
    }

    override suspend fun getStreak(walletAddress: String): Streak? {
        return streakDao.getStreak(walletAddress)?.toDomain()
    }

    override suspend fun recordActivity(walletAddress: String) {
        val today = LocalDate.now().format(dateFormatter)
        val existing = streakDao.getStreak(walletAddress)

        if (existing == null) {
            streakDao.upsertStreak(
                StreakEntity(
                    walletAddress = walletAddress,
                    currentStreak = 1,
                    longestStreak = 1,
                    lastActiveDate = today,
                    totalActiveDays = 1,
                ),
            )
            return
        }

        val lastDate = LocalDate.parse(existing.lastActiveDate, dateFormatter)
        val todayDate = LocalDate.now()
        val daysBetween = ChronoUnit.DAYS.between(lastDate, todayDate)

        when {
            daysBetween == 0L -> return // Already recorded today
            daysBetween == 1L -> {
                // Consecutive day — increment streak
                val newStreak = existing.currentStreak + 1
                streakDao.upsertStreak(
                    existing.copy(
                        currentStreak = newStreak,
                        longestStreak = maxOf(existing.longestStreak, newStreak),
                        lastActiveDate = today,
                        totalActiveDays = existing.totalActiveDays + 1,
                    ),
                )
            }
            else -> {
                // Streak broken — reset to 1
                streakDao.upsertStreak(
                    existing.copy(
                        currentStreak = 1,
                        lastActiveDate = today,
                        totalActiveDays = existing.totalActiveDays + 1,
                    ),
                )
            }
        }
    }

    override suspend fun getGrowthMultiplier(walletAddress: String): Float {
        val streak = streakDao.getStreak(walletAddress) ?: return 1.0f
        return when {
            streak.currentStreak >= 14 -> 2.0f
            streak.currentStreak >= 7 -> 1.5f
            streak.currentStreak >= 3 -> 1.25f
            else -> 1.0f
        }
    }
}
