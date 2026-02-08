package com.example.gro.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.gro.data.local.db.dao.JournalDao
import com.example.gro.data.local.db.dao.PlantDao
import com.example.gro.data.local.db.dao.StreakDao
import com.example.gro.data.local.db.entity.JournalEntryEntity
import com.example.gro.data.local.db.entity.PlantEntity
import com.example.gro.data.local.db.entity.StreakEntity

@Database(
    entities = [PlantEntity::class, StreakEntity::class, JournalEntryEntity::class],
    version = 2,
    exportSchema = false,
)
abstract class GroDatabase : RoomDatabase() {
    abstract fun plantDao(): PlantDao
    abstract fun streakDao(): StreakDao
    abstract fun journalDao(): JournalDao
}
