package com.example.gro.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.gro.data.local.db.GroDatabase
import com.example.gro.data.local.db.dao.JournalDao
import com.example.gro.data.local.db.dao.PlantDao
import com.example.gro.data.local.db.dao.StreakDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """CREATE TABLE IF NOT EXISTS `streaks` (
                    `walletAddress` TEXT NOT NULL,
                    `currentStreak` INTEGER NOT NULL,
                    `longestStreak` INTEGER NOT NULL,
                    `lastActiveDate` TEXT NOT NULL,
                    `totalActiveDays` INTEGER NOT NULL,
                    PRIMARY KEY(`walletAddress`)
                )""",
            )
            db.execSQL(
                """CREATE TABLE IF NOT EXISTS `journal_entries` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `walletAddress` TEXT NOT NULL,
                    `timestamp` INTEGER NOT NULL,
                    `action` TEXT NOT NULL,
                    `details` TEXT NOT NULL,
                    `gardenSnapshot` INTEGER NOT NULL
                )""",
            )
        }
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): GroDatabase {
        return Room.databaseBuilder(
            context,
            GroDatabase::class.java,
            "gro_database",
        ).addMigrations(MIGRATION_1_2)
            .build()
    }

    @Provides
    fun providePlantDao(database: GroDatabase): PlantDao {
        return database.plantDao()
    }

    @Provides
    fun provideStreakDao(database: GroDatabase): StreakDao {
        return database.streakDao()
    }

    @Provides
    fun provideJournalDao(database: GroDatabase): JournalDao {
        return database.journalDao()
    }
}
