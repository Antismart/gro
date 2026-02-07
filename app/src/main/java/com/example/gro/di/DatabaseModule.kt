package com.example.gro.di

import android.content.Context
import androidx.room.Room
import com.example.gro.data.local.db.GroDatabase
import com.example.gro.data.local.db.dao.PlantDao
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
        ).build()
    }

    @Provides
    fun providePlantDao(database: GroDatabase): PlantDao {
        return database.plantDao()
    }
}
