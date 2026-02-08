package com.example.gro.di

import android.content.Context
import androidx.room.Room
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

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): GroDatabase {
        return Room.databaseBuilder(
            context,
            GroDatabase::class.java,
            "gro_database",
        ).fallbackToDestructiveMigration()
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
